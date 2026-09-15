package com.example.data.supabase

import android.util.Log
import com.example.data.FootballerRepository
import com.example.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class SupabaseRestClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun parseIsoToEpochMs(isoStr: String?): Long {
        if (isoStr.isNullOrBlank()) return System.currentTimeMillis() + 15000L
        return try {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss"
            )
            for (pattern in formats) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.US)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val date = sdf.parse(isoStr)
                    if (date != null) return date.time
                } catch (ignored: Exception) {}
            }
            System.currentTimeMillis() + 15000L
        } catch (e: Exception) {
            System.currentTimeMillis() + 15000L
        }
    }

    private suspend fun callRpc(functionName: String, params: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        val rawBase = SupabaseConfig.getSupabaseUrl().trim().trimEnd('/')
        val anonKey = SupabaseConfig.getSupabaseAnonKey().trim()

        if (rawBase.isBlank() || anonKey.isBlank()) {
            Log.e("[ROOM]", "callRpc failed: Supabase URL or Anon Key is not configured.")
            return@withContext Result.failure(IllegalStateException("Supabase URL veya Anon Key ayarlanmamış."))
        }

        try {
            val baseUrl = if (!rawBase.startsWith("http://") && !rawBase.startsWith("https://")) {
                "https://$rawBase"
            } else {
                rawBase
            }
            val url = "$baseUrl/rest/v1/rpc/$functionName"
            val body = params.toString().toRequestBody(jsonMediaType)

            Log.d("[ROOM]", "calling $functionName RPC with endpoint: $url")

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $anonKey")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                Log.d("[ROOM]", "RPC $functionName status: ${response.code}, response: $responseBody")

                if (!response.isSuccessful) {
                    val errorMsg = try {
                        val errJson = JSONObject(responseBody)
                        errJson.optString("message", errJson.optString("error", "Sunucu hatası: ${response.code}"))
                    } catch (e: Exception) {
                        "Sunucu hatası: ${response.code} $responseBody"
                    }
                    Log.e("[ROOM]", "RPC $functionName error response: $errorMsg")
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val json = if (responseBody.startsWith("{")) {
                    JSONObject(responseBody)
                } else if (responseBody.startsWith("[")) {
                    val arr = JSONArray(responseBody)
                    if (arr.length() > 0) arr.getJSONObject(0) else JSONObject()
                } else {
                    JSONObject().put("value", responseBody)
                }

                if (json.has("success") && !json.optBoolean("success", true)) {
                    val err = json.optString("error", "İşlem başarısız")
                    Log.e("[ROOM]", "RPC $functionName returned success=false: $err")
                    return@withContext Result.failure(Exception(err))
                }

                Result.success(json)
            }
        } catch (e: Throwable) {
            Log.e("[ROOM]", "RPC $functionName execution exception: ${e.message}", e)
            Result.failure(Exception("Bağlantı hatası: ${e.localizedMessage ?: "Bilinmeyen hata"}"))
        }
    }

    suspend fun createRoom(userId: String, username: String): Result<OnlineRoom> {
        val params = JSONObject().apply {
            put("p_user_id", userId)
            put("p_username", username)
        }
        return callRpc("create_room", params).fold(
            onSuccess = { json ->
                try {
                    // Robust field resolution: supports snake_case, camelCase, nested 'data', and fallback keys
                    val nestedData = json.optJSONObject("data") ?: json.optJSONObject("record")
                    val roomId = json.optString("room_id")
                        .ifBlank { json.optString("roomId") }
                        .ifBlank { json.optString("id") }
                        .ifBlank { nestedData?.optString("room_id").orEmpty() }
                        .ifBlank { nestedData?.optString("id").orEmpty() }

                    val roomCode = json.optString("room_code")
                        .ifBlank { json.optString("roomCode") }
                        .ifBlank { json.optString("code") }
                        .ifBlank { nestedData?.optString("room_code").orEmpty() }
                        .ifBlank { nestedData?.optString("code").orEmpty() }

                    val status = json.optString("status", "waiting")

                    if (roomId.isBlank() || roomCode.isBlank()) {
                        Log.e("[ROOM]", "create_room response missing roomId or roomCode: $json")
                        Result.failure(IllegalStateException("Sunucudan geçerli oda bilgisi alınamadı."))
                    } else {
                        Result.success(
                            OnlineRoom(
                                id = roomId,
                                roomCode = roomCode.trim().uppercase(),
                                hostUserId = userId,
                                status = status
                            )
                        )
                    }
                } catch (e: Throwable) {
                    Log.e("[ROOM]", "Failed to map create_room response: ${e.message}", e)
                    Result.failure(Exception("Oda bilgileri işlenirken hata oluştu."))
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    suspend fun joinRoom(roomCode: String, userId: String, username: String): Result<JSONObject> {
        val params = JSONObject().apply {
            put("p_room_code", roomCode.trim().uppercase())
            put("p_user_id", userId)
            put("p_username", username)
        }
        return callRpc("join_room", params)
    }

    suspend fun getRoomState(roomId: String?, roomCode: String?): Result<JSONObject> {
        val params = JSONObject().apply {
            if (!roomId.isNullOrBlank()) put("p_room_id", roomId)
            if (!roomCode.isNullOrBlank()) put("p_room_code", roomCode.trim().uppercase())
        }
        return callRpc("get_room_state", params)
    }

    suspend fun submitPick(matchId: String, userId: String, footballerId: String): Result<JSONObject> {
        val params = JSONObject().apply {
            put("p_match_id", matchId)
            put("p_user_id", userId)
            put("p_footballer_id", footballerId)
        }
        return callRpc("submit_pick", params)
    }

    suspend fun handleTurnTimeout(matchId: String): Result<JSONObject> {
        val params = JSONObject().apply {
            put("p_match_id", matchId)
        }
        return callRpc("handle_turn_timeout", params)
    }

    suspend fun getMatchState(matchId: String, userId: String): Result<OnlineMatchState> {
        val params = JSONObject().apply {
            put("p_match_id", matchId)
            put("p_user_id", userId)
        }
        return callRpc("get_match_state", params).map { json ->
            val playerAId = json.getString("player_a_id")
            val playerBId = json.getString("player_b_id")
            val myRole = if (userId == playerAId) "A" else "B"
            val currentPlayerId = json.getString("current_player_id")

            val questionObj = json.optJSONObject("question")
            val question = if (questionObj != null) {
                Question(
                    id = questionObj.optString("id", "q1"),
                    title = questionObj.optString("title", "Hedef Maçı"),
                    competition = questionObj.optString("competition", "Premier League"),
                    statType = questionObj.optString("stat_type", "goals"),
                    target = questionObj.optInt("target", 500),
                    pickCount = questionObj.optInt("pick_count", 5),
                    difficulty = questionObj.optString("difficulty", "Orta"),
                    eligibleCompetitions = listOf(questionObj.optString("competition", "Premier League")),
                    description = questionObj.optString("description", "")
                )
            } else {
                FootballerRepository.questions.first()
            }

            val picksArr = json.optJSONArray("picks") ?: JSONArray()
            val picksList = mutableListOf<OnlinePick>()
            for (i in 0 until picksArr.length()) {
                val p = picksArr.getJSONObject(i)
                picksList.add(
                    OnlinePick(
                        id = p.optString("id", "$i"),
                        matchId = matchId,
                        playerId = p.getString("player_id"),
                        pickNumber = p.getInt("pick_number"),
                        footballerId = if (p.isNull("footballer_id")) null else p.getString("footballer_id"),
                        isTimeout = p.optBoolean("is_timeout", false),
                        statValue = p.optInt("stat_value", 0)
                    )
                )
            }

            val deadlineIso = json.optString("turn_deadline_at")
            val deadlineEpochMs = parseIsoToEpochMs(deadlineIso)

            OnlineMatchState(
                matchId = matchId,
                roomId = json.optString("room_id", ""),
                roomCode = json.optString("room_code", ""),
                playerAId = playerAId,
                playerBId = playerBId,
                myUserId = userId,
                myRole = myRole,
                currentPlayerId = currentPlayerId,
                isMyTurn = currentPlayerId == userId,
                status = json.getString("status"),
                currentPickNumber = json.optInt("current_pick_number", 1),
                turnDeadlineAtEpochMs = deadlineEpochMs,
                winner = if (json.isNull("winner_id")) null else json.optString("winner_id"),
                playerATotal = json.optInt("player_a_total", 0),
                playerBTotal = json.optInt("player_b_total", 0),
                question = question,
                picks = picksList
            )
        }
    }

    suspend fun requestRematch(matchId: String, userId: String): Result<JSONObject> {
        val params = JSONObject().apply {
            put("p_match_id", matchId)
            put("p_user_id", userId)
        }
        return callRpc("request_rematch", params)
    }
}
