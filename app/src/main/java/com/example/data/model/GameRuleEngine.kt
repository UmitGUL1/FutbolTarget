package com.example.data.model

import kotlin.math.abs

object GameRuleEngine {
    const val PLAYER_A = "A"
    const val PLAYER_B = "B"
    const val DRAW = "draw"
    const val FIRST_TURN_SECONDS = 20
    const val NEXT_TURN_SECONDS = 15
    const val RECONNECT_TIMEOUT_SECONDS = 15
    private val rankOrder = listOf("Bronze", "Silver", "Gold", "Platinum", "Diamond", "Elite", "Legend")

    fun targetDistance(target: Int, total: Int): Int = abs(target - total)

    fun calculateWinner(target: Int, totalA: Int, totalB: Int): String {
        val diffA = targetDistance(target, totalA)
        val diffB = targetDistance(target, totalB)
        return when {
            diffA < diffB -> PLAYER_A
            diffB < diffA -> PLAYER_B
            else -> DRAW
        }
    }

    fun calculateTotal(steps: List<RevealStep>, playerId: String): Int {
        return steps.filter { it.playerId == playerId }.sumOf { it.statValue }
    }

    fun statValueFor(slot: PickSlot, question: Question): Int {
        if (slot.timedOut) return 0
        return slot.footballer?.getStatValue(question.competition, question.statType) ?: 0
    }

    fun isDuplicateFootballer(existingPicks: List<PickSlot>, footballerId: String): Boolean {
        return existingPicks.any { !it.timedOut && it.footballer?.id.equals(footballerId, ignoreCase = true) }
    }

    fun picksForPlayer(existingPicks: List<PickSlot>, playerId: String): Int {
        return existingPicks.count { it.playerId == playerId }
    }

    fun canAcceptPick(existingPicks: List<PickSlot>, playerId: String, footballerId: String?, maxPicksPerPlayer: Int): Boolean {
        if (picksForPlayer(existingPicks, playerId) >= maxPicksPerPlayer) return false
        if (footballerId != null && isDuplicateFootballer(existingPicks, footballerId)) return false
        return true
    }

    fun buildPickOrder(startingPlayer: String, pickCount: Int): List<String> {
        require(startingPlayer == PLAYER_A || startingPlayer == PLAYER_B) { "startingPlayer must be A or B" }
        require(pickCount >= 0) { "pickCount must be non-negative" }
        val secondPlayer = if (startingPlayer == PLAYER_A) PLAYER_B else PLAYER_A
        return buildList {
            repeat(pickCount) {
                add(startingPlayer)
                add(secondPlayer)
            }
        }
    }

    fun turnDurationSeconds(turnIndex: Int): Int {
        return if (turnIndex == 0) FIRST_TURN_SECONDS else NEXT_TURN_SECONDS
    }

    fun buildRevealSteps(picks: List<PickSlot>, question: Question): List<RevealStep> {
        val byPlayerAndNumber = picks.associateBy { it.playerId to it.pickNumber }
        val maxPickNumber = picks.maxOfOrNull { it.pickNumber } ?: 0
        return buildList {
            for (pickNumber in 1..maxPickNumber) {
                listOf(PLAYER_A, PLAYER_B).forEach { playerId ->
                    byPlayerAndNumber[playerId to pickNumber]?.let { slot ->
                        add(
                            RevealStep(
                                playerId = slot.playerId,
                                pickNumber = slot.pickNumber,
                                footballer = slot.footballer,
                                timedOut = slot.timedOut,
                                statValue = statValueFor(slot, question)
                            )
                        )
                    }
                }
            }
        }
    }

    fun calculateResult(question: Question, revealSteps: List<RevealStep>): MatchResult {
        val totalA = calculateTotal(revealSteps, PLAYER_A)
        val totalB = calculateTotal(revealSteps, PLAYER_B)
        val diffA = targetDistance(question.target, totalA)
        val diffB = targetDistance(question.target, totalB)
        return MatchResult(
            target = question.target,
            totalA = totalA,
            totalB = totalB,
            diffA = diffA,
            diffB = diffB,
            winner = calculateWinner(question.target, totalA, totalB)
        )
    }

    fun applyRankedResult(profile: Profile, result: String): Profile {
        return when (result) {
            "win" -> {
                val points = profile.rankPoints + 1
                if (points >= 5) {
                    profile.copy(wins = profile.wins + 1, rank = nextRank(profile.rank), rankPoints = 0)
                } else {
                    profile.copy(wins = profile.wins + 1, rankPoints = points)
                }
            }
            "loss" -> profile.copy(
                losses = profile.losses + 1,
                rankPoints = (profile.rankPoints - 1).coerceAtLeast(0)
            )
            "draw" -> profile.copy(draws = profile.draws + 1)
            else -> profile
        }
    }

    fun nextRank(currentRank: String): String {
        val index = rankOrder.indexOf(currentRank)
        return if (index != -1 && index < rankOrder.lastIndex) rankOrder[index + 1] else "Legend"
    }

    fun hasReconnectTimedOut(disconnectedAtEpochMs: Long, nowEpochMs: Long): Boolean {
        return nowEpochMs - disconnectedAtEpochMs >= RECONNECT_TIMEOUT_SECONDS * 1000L
    }
}
