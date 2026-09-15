package com.example.data.supabase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.FootballerRepository
import com.example.data.model.Footballer
import com.example.data.model.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class OnlineMatchRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("online_user_prefs", Context.MODE_PRIVATE)
    private val restClient = SupabaseRestClient()
    private val realtimeClient = SupabaseRealtimeClient()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current anonymous user ID and username
    var currentUserId: String
        private set
    var currentUsername: String
        private set

    init {
        SupabaseConfig.init(context)

        var savedId = prefs.getString("anon_user_id", null)
        if (savedId.isNullOrBlank()) {
            savedId = UUID.randomUUID().toString()
            prefs.edit().putString("anon_user_id", savedId).apply()
        }
        currentUserId = savedId

        var savedUsername = prefs.getString("anon_username", null)
        if (savedUsername.isNullOrBlank()) {
            val randomNum = (1000..9999).random()
            savedUsername = "Oyuncu-$randomNum"
            prefs.edit().putString("anon_username", savedUsername).apply()
        }
        currentUsername = savedUsername
    }

    private val _currentMatchState = MutableStateFlow<OnlineMatchState?>(null)
    val currentMatchState: StateFlow<OnlineMatchState?> = _currentMatchState.asStateFlow()

    private var autoRefreshJob: Job? = null

    suspend fun createRoom(): Result<OnlineRoom> = withContext(Dispatchers.IO) {
        try {
            Log.d("[AUTH]", "current user id: $currentUserId")
            Log.d("[ROOM]", "create started")

            // Auth / User ID validation
            val userId = currentUserId.trim()
            val username = currentUsername.trim()
            if (userId.isBlank()) {
                Log.e("[ROOM]", "createRoom aborted: User ID is blank or auth not ready")
                return@withContext Result.failure(Exception("Kullanıcı kimliği hazırlanıyor. Lütfen tekrar deneyin."))
            }

            Log.d("[ROOM]", "auth user: $userId (username: $username)")

            if (!SupabaseConfig.isConfigured()) {
                return@withContext Result.failure(
                    Exception("Online mod için Supabase bağlantısı gereklidir. Lütfen sağ üstteki ayarlar simgesine tıklayarak Supabase URL ve Anon Key girin.")
                )
            }

            Log.d("[ROOM]", "calling create_room RPC via SupabaseRestClient")
            val result = restClient.createRoom(userId, username)
            if (result.isSuccess) {
                val room = result.getOrThrow()
                Log.d("[ROOM]", "room created: id=${room.id}, code=${room.roomCode}")
                Log.d("[ROOM]", "host user id: $userId")
                Result.success(room)
            } else {
                val ex = result.exceptionOrNull()
                Log.e("[ROOM]", "create_room RPC returned failure: ${ex?.message}", ex)
                Result.failure(ex ?: Exception("Oda oluşturulamadı"))
            }
        } catch (e: Throwable) {
            Log.e("[ROOM]", "Unexpected error in createRoom: ${e.message}", e)
            Result.failure(Exception("Oda oluşturulurken hata oluştu: ${e.localizedMessage ?: "Bilinmeyen hata"}"))
        }
    }

    suspend fun joinRoom(roomCode: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanCode = roomCode.trim().uppercase()
        if (!RoomCodeGenerator.isValidCode(cleanCode)) {
            return@withContext Result.failure(Exception("Geçersiz oda kodu formatı. 6 karakter olmalı."))
        }

        if (!SupabaseConfig.isConfigured()) {
            return@withContext Result.failure(
                Exception("Online mod için Supabase bağlantısı gereklidir. Lütfen sağ üstteki ayarlardan Supabase bilgilerinizi girin.")
            )
        }

        Log.d("[AUTH]", "current user id: $currentUserId")
        Log.d("[ROOM]", "guest joining room: $cleanCode")

        val res = restClient.joinRoom(cleanCode, currentUserId, currentUsername)
        if (res.isSuccess) {
            val json = res.getOrThrow()
            val matchId = json.optString("match_id", "")
            val hostId = json.optString("player_a_id", "")
                .ifBlank { json.optString("host_user_id", "") }
            val guestId = json.optString("player_b_id", "")
                .ifBlank { json.optString("guest_user_id", "") }

            Log.d("[ROOM]", "host user id: $hostId")
            Log.d("[ROOM]", "guest user id: $guestId")
            Log.d("[REALTIME]", "match created: $matchId")
            Log.d("[REALTIME]", "navigating to match: matchId=$matchId")

            // If server auto-assigned a distinct guest ID to avoid self-match collision
            if (guestId.isNotBlank() && guestId != currentUserId) {
                currentUserId = guestId
                prefs.edit().putString("anon_user_id", guestId).apply()
            }

            Result.success(matchId)
        } else {
            val err = res.exceptionOrNull()
            Log.e("[ROOM]", "joinRoom failed: ${err?.message}", err)
            Result.failure(err ?: Exception("Odaya katılamadı"))
        }
    }

    suspend fun waitForGuest(
        roomId: String,
        roomCode: String,
        onGuestJoined: (matchId: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            if (roomId.isBlank() || roomCode.isBlank()) {
                Log.w("[ROOM]", "waitForGuest skipped because roomId or roomCode is blank")
                return@withContext
            }

            if (!SupabaseConfig.isConfigured()) {
                Log.w("[ROOM]", "Supabase is not configured; waiting for real connection.")
                return@withContext
            }

            Log.d("[AUTH]", "current user id: $currentUserId")
            Log.d("[ROOM]", "host user id: $currentUserId")
            Log.d("[REALTIME]", "subscribing room: $roomId")

            var hasNavigated = false
            fun triggerNavigate(matchId: String) {
                if (!hasNavigated && matchId.isNotBlank()) {
                    hasNavigated = true
                    Log.d("[REALTIME]", "match created: $matchId")
                    Log.d("[REALTIME]", "navigating to match: matchId=$matchId")
                    scope.launch(Dispatchers.Main) {
                        onGuestJoined(matchId)
                    }
                }
            }

            // 1. Initial fetch before or right upon subscribing (handles fast joins and missed events)
            val initialRes = restClient.getRoomState(roomId, roomCode)
            if (initialRes.isSuccess) {
                val json = initialRes.getOrThrow()
                val status = json.optString("status")
                val activeMatchId = json.optString("active_match_id")
                val guestUserId = json.optString("guest_user_id")
                if (guestUserId.isNotBlank()) {
                    Log.d("[ROOM]", "guest user id: $guestUserId")
                }
                if ((status == "in_match" || status == "ready") && activeMatchId.isNotBlank()) {
                    triggerNavigate(activeMatchId)
                    return@withContext
                }
            }

            // 2. Realtime WebSocket listener
            realtimeClient.connect(
                roomId = roomId,
                matchId = null,
                onUpdate = {
                    scope.launch {
                        val stateRes = restClient.getRoomState(roomId, roomCode)
                        if (stateRes.isSuccess) {
                            val json = stateRes.getOrThrow()
                            val status = json.optString("status")
                            val activeMatchId = json.optString("active_match_id")
                            val guestUserId = json.optString("guest_user_id")
                            if (guestUserId.isNotBlank()) {
                                Log.d("[ROOM]", "guest user id: $guestUserId")
                            }
                            if ((status == "in_match" || status == "ready") && activeMatchId.isNotBlank()) {
                                triggerNavigate(activeMatchId)
                            }
                        }
                    }
                },
                onGuestJoinedCallback = { matchId ->
                    triggerNavigate(matchId)
                }
            )

            // 3. Fallback active polling every 1.5 seconds so host is never stuck if WS frame is dropped
            while (isActive && !hasNavigated) {
                delay(1500L)
                val pollRes = restClient.getRoomState(roomId, roomCode)
                if (pollRes.isSuccess) {
                    val json = pollRes.getOrThrow()
                    val status = json.optString("status")
                    val activeMatchId = json.optString("active_match_id")
                    val guestUserId = json.optString("guest_user_id")
                    if (guestUserId.isNotBlank()) {
                        Log.d("[ROOM]", "guest user id: $guestUserId")
                    }
                    if ((status == "in_match" || status == "ready") && activeMatchId.isNotBlank()) {
                        triggerNavigate(activeMatchId)
                        break
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("[ROOM]", "Error while waiting for guest: ${e.message}", e)
        }
    }

    fun startListeningToMatch(matchId: String, roomId: String) {
        stopListening()

        if (!SupabaseConfig.isConfigured()) return

        // 1. Initial fetch
        refreshMatchState(matchId)

        // 2. Realtime WebSocket subscription
        realtimeClient.connect(
            roomId = roomId,
            matchId = matchId,
            onUpdate = {
                refreshMatchState(matchId)
            }
        )

        // 3. Fallback periodic sync every 3s to guarantee freshness
        autoRefreshJob = scope.launch {
            while (isActive) {
                delay(3000L)
                refreshMatchState(matchId)
            }
        }
    }

    fun refreshMatchState(matchId: String) {
        scope.launch {
            if (!SupabaseConfig.isConfigured()) return@launch

            val result = restClient.getMatchState(matchId, currentUserId)
            if (result.isSuccess) {
                _currentMatchState.value = result.getOrThrow()
            } else {
                Log.w("OnlineMatchRepo", "Failed to refresh match state: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    suspend fun submitPick(matchId: String, footballerId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentState = _currentMatchState.value
        if (currentState != null) {
            if (!currentState.isMyTurn) {
                return@withContext Result.failure(Exception("Sıra sizde değil."))
            }
            if (MatchRuleEngine.isPickDuplicate(currentState.picks, footballerId)) {
                return@withContext Result.failure(Exception("Bu futbolcu bu maçta daha önce seçildi."))
            }
        }

        if (!SupabaseConfig.isConfigured()) {
            return@withContext Result.failure(Exception("Supabase bağlantısı gerekli."))
        }

        val res = restClient.submitPick(matchId, currentUserId, footballerId)
        if (res.isSuccess) {
            refreshMatchState(matchId)
            Result.success(Unit)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Seçim iletilemedi."))
        }
    }

    suspend fun handleTurnTimeout(matchId: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isConfigured()) {
            return@withContext Result.failure(Exception("Supabase bağlantısı gerekli."))
        }

        val res = restClient.handleTurnTimeout(matchId)
        if (res.isSuccess) {
            refreshMatchState(matchId)
            Result.success(Unit)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Zaman aşımı işlenemedi"))
        }
    }

    suspend fun requestRematch(matchId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isConfigured()) {
            return@withContext Result.failure(Exception("Supabase bağlantısı gerekli."))
        }

        val res = restClient.requestRematch(matchId, currentUserId)
        if (res.isSuccess) {
            val json = res.getOrThrow()
            val isReady = json.optBoolean("rematch_ready", false)
            refreshMatchState(matchId)
            Result.success(isReady)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Rövanş isteği iletilemedi"))
        }
    }

    fun stopListening() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
        realtimeClient.disconnect()
    }
}
