package com.example.data.supabase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SupabaseRealtimeClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // Keep alive for WebSocket
        .build()
) {
    private var webSocket: WebSocket? = null
    private var heartbeatJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val refCounter = AtomicInteger(1)

    private var onMessageReceived: (() -> Unit)? = null
    private var onGuestJoined: ((matchId: String) -> Unit)? = null

    fun connect(
        roomId: String,
        matchId: String? = null,
        onUpdate: () -> Unit,
        onGuestJoinedCallback: ((matchId: String) -> Unit)? = null
    ) {
        disconnect()

        val wsUrl = SupabaseConfig.getRealtimeWebSocketUrl()
        if (!SupabaseConfig.isConfigured() || wsUrl.isBlank()) {
            Log.w("SupabaseRealtime", "Supabase is not configured; skipping WebSocket connection.")
            return
        }

        this.onMessageReceived = onUpdate
        this.onGuestJoined = onGuestJoinedCallback

        try {
            Log.d("[ROOM]", "subscribing realtime WebSocket to $wsUrl for roomId=$roomId")
            val request = Request.Builder()
                .url(wsUrl)
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("SupabaseRealtime", "Connected to Realtime WebSocket")
                    startHeartbeat()

                    // Join room channel
                    if (roomId.isNotBlank()) {
                        joinRoomChannel(roomId)
                    }

                    // Join match channel if matchId is provided
                    if (!matchId.isNullOrBlank()) {
                        joinMatchChannel(matchId)
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val json = JSONObject(text)
                        val event = json.optString("event")

                        if (event == "postgres_changes" || event == "broadcast") {
                            val payload = json.optJSONObject("payload")
                            val data = payload?.optJSONObject("data")
                            val record = data?.optJSONObject("record")
                                ?: payload?.optJSONObject("record")

                            val table = data?.optString("table")
                                ?: payload?.optString("table")

                            Log.d("[REALTIME]", "room event received: table=$table, record=$record")

                            // Check if room updated to in_match or ready
                            if (table == "rooms" || record?.has("guest_user_id") == true) {
                                val newStatus = record?.optString("status")
                                if (newStatus == "in_match" || newStatus == "ready") {
                                    val activeMatchId = record?.optString("active_match_id")
                                        ?.ifBlank { record.optString("match_id") }
                                        ?: ""
                                    if (activeMatchId.isNotBlank()) {
                                        Log.d("[REALTIME]", "match created: $activeMatchId")
                                        scope.launch(Dispatchers.Main) {
                                            onGuestJoined?.invoke(activeMatchId)
                                        }
                                    }
                                }
                            } else if (table == "matches") {
                                val matchId = record?.optString("id", "") ?: ""
                                if (matchId.isNotBlank()) {
                                    Log.d("[REALTIME]", "match created: $matchId")
                                    scope.launch(Dispatchers.Main) {
                                        onGuestJoined?.invoke(matchId)
                                    }
                                }
                            }

                            scope.launch(Dispatchers.Main) {
                                onMessageReceived?.invoke()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SupabaseRealtime", "Error parsing realtime frame", e)
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.w("SupabaseRealtime", "WebSocket failure: ${t.message}")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("SupabaseRealtime", "WebSocket closed: $code / $reason")
                }
            })
        } catch (e: Throwable) {
            Log.e("[ROOM]", "WebSocket connection attempt threw exception", e)
        }
    }

    private fun joinRoomChannel(roomId: String) {
        val ref = refCounter.getAndIncrement().toString()
        val joinMsg = JSONObject().apply {
            put("topic", "realtime:public:rooms:id=eq.$roomId")
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                put("config", JSONObject().apply {
                    put("postgres_changes", JSONArray().apply {
                        put(JSONObject().apply {
                            put("event", "*")
                            put("schema", "public")
                            put("table", "rooms")
                            put("filter", "id=eq.$roomId")
                        })
                        put(JSONObject().apply {
                            put("event", "*")
                            put("schema", "public")
                            put("table", "matches")
                            put("filter", "room_id=eq.$roomId")
                        })
                    })
                })
            })
            put("ref", ref)
        }
        webSocket?.send(joinMsg.toString())
    }

    private fun joinMatchChannel(matchId: String) {
        val ref = refCounter.getAndIncrement().toString()
        val joinMsg = JSONObject().apply {
            put("topic", "realtime:public:matches:id=eq.$matchId")
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                put("config", JSONObject().apply {
                    put("postgres_changes", JSONArray().apply {
                        put(JSONObject().apply {
                            put("event", "*")
                            put("schema", "public")
                            put("table", "matches")
                            put("filter", "id=eq.$matchId")
                        })
                        put(JSONObject().apply {
                            put("event", "*")
                            put("schema", "public")
                            put("table", "match_picks")
                            put("filter", "match_id=eq.$matchId")
                        })
                    })
                })
            })
            put("ref", ref)
        }
        webSocket?.send(joinMsg.toString())
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(25000L)
                val hb = JSONObject().apply {
                    put("topic", "phoenix")
                    put("event", "heartbeat")
                    put("payload", JSONObject())
                    put("ref", refCounter.getAndIncrement().toString())
                }
                webSocket?.send(hb.toString())
            }
        }
    }

    fun disconnect() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        try {
            webSocket?.close(1000, "Clean unmount")
        } catch (ignored: Exception) {}
        webSocket = null
        onMessageReceived = null
        onGuestJoined = null
    }
}
