package com.example.data.supabase

import com.example.data.model.GameRuleEngine
import com.example.data.model.Question
import kotlin.math.abs
import kotlin.random.Random

data class OnlineRoom(
    val id: String,
    val roomCode: String,
    val hostUserId: String,
    val guestUserId: String? = null,
    val status: String = "waiting" // waiting, ready, in_match, finished, cancelled
)

data class OnlinePick(
    val id: String,
    val matchId: String,
    val playerId: String, // UUID of the player
    val pickNumber: Int,
    val footballerId: String?,
    val isTimeout: Boolean = false,
    val statValue: Int = 0 // Hidden during picking (0), populated during reveal/finish
)

data class OnlineMatchState(
    val matchId: String,
    val roomId: String,
    val roomCode: String,
    val playerAId: String,
    val playerBId: String,
    val myUserId: String,
    val myRole: String, // "A" or "B"
    val currentPlayerId: String,
    val isMyTurn: Boolean,
    val status: String, // "picking", "reveal", "finished"
    val currentPickNumber: Int,
    val turnDeadlineAtEpochMs: Long,
    val winner: String? = null, // "A", "B", "draw"
    val playerATotal: Int = 0,
    val playerBTotal: Int = 0,
    val question: Question,
    val picks: List<OnlinePick> = emptyList(),
    val rematchRequestedByMe: Boolean = false,
    val rematchRequestedByOpponent: Boolean = false
) {
    val myPicks: List<OnlinePick> get() = picks.filter { it.playerId == myUserId }
    val opponentPicks: List<OnlinePick> get() = picks.filter { it.playerId != myUserId }

    val isFinished: Boolean get() = status == "finished" || status == "reveal"

    val diffA: Int get() = abs(question.target - playerATotal)
    val diffB: Int get() = abs(question.target - playerBTotal)
}

object RoomCodeGenerator {
    // 32-char set omitting confusing glyphs (0, O, 1, I)
    private const val CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    fun generateCode(): String {
        val sb = StringBuilder(6)
        for (i in 0 until 6) {
            val idx = Random.nextInt(CHARSET.length)
            sb.append(CHARSET[idx])
        }
        return sb.toString()
    }

    fun isValidCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        if (trimmed.length != 6) return false
        return trimmed.all { it in CHARSET }
    }
}

object MatchRuleEngine {
    /**
     * Server-authoritative winner determination logic.
     * differenceA = abs(target - totalA)
     * differenceB = abs(target - totalB)
     * Closest to target wins. Overshooting is allowed without extra penalty.
     */
    fun calculateWinner(target: Int, totalA: Int, totalB: Int): String {
        return GameRuleEngine.calculateWinner(target, totalA, totalB)
    }

    fun isPickDuplicate(existingPicks: List<OnlinePick>, footballerId: String): Boolean {
        return existingPicks.any { !it.isTimeout && it.footballerId.equals(footballerId, ignoreCase = true) }
    }
}


