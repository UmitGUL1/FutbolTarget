package com.example

import com.example.data.model.Footballer
import com.example.data.model.GameRuleEngine
import com.example.data.model.PickSlot
import com.example.data.model.PlayerStat
import com.example.data.model.Profile
import com.example.data.model.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRuleEngineUnitTest {
    private val question = Question(
        id = "test-question",
        title = "Test hedef sorusu",
        competition = "Premier League",
        statType = "goals",
        target = 500,
        pickCount = 5,
        eligibleCompetitions = listOf("Premier League"),
        description = "Test question"
    )

    @Test
    fun `target distance uses absolute difference`() {
        assertEquals(5, GameRuleEngine.targetDistance(500, 495))
        assertEquals(20, GameRuleEngine.targetDistance(500, 520))
        assertEquals(0, GameRuleEngine.targetDistance(500, 500))
    }

    @Test
    fun `closest player wins and equal distance draws`() {
        assertEquals("A", GameRuleEngine.calculateWinner(500, 495, 520))
        assertEquals("B", GameRuleEngine.calculateWinner(500, 470, 510))
        assertEquals("draw", GameRuleEngine.calculateWinner(500, 490, 510))
        assertEquals("draw", GameRuleEngine.calculateWinner(500, 500, 500))
    }

    @Test
    fun `totals include real stats and timeout slots are zero`() {
        val picks = listOf(
            PickSlot("A", 1, footballer("a1", 200)),
            PickSlot("B", 1, footballer("b1", 100)),
            PickSlot("A", 2, null, timedOut = true),
            PickSlot("B", 2, footballer("b2", 150))
        )

        val steps = GameRuleEngine.buildRevealSteps(picks, question)

        assertEquals(200, GameRuleEngine.calculateTotal(steps, "A"))
        assertEquals(250, GameRuleEngine.calculateTotal(steps, "B"))
        assertEquals(0, steps.first { it.playerId == "A" && it.pickNumber == 2 }.statValue)
    }

    @Test
    fun `duplicate footballer selection is blocked case-insensitively`() {
        val picks = listOf(PickSlot("A", 1, footballer("lionel-messi", 474)))

        assertTrue(GameRuleEngine.isDuplicateFootballer(picks, "LIONEL-MESSI"))
        assertFalse(GameRuleEngine.canAcceptPick(picks, "B", "lionel-messi", maxPicksPerPlayer = 5))
        assertTrue(GameRuleEngine.canAcceptPick(picks, "B", "cristiano-ronaldo", maxPicksPerPlayer = 5))
    }

    @Test
    fun `maximum pick count per player is enforced`() {
        val picks = (1..5).map { PickSlot("A", it, footballer("a$it", it)) }

        assertFalse(GameRuleEngine.canAcceptPick(picks, "A", "a6", maxPicksPerPlayer = 5))
        assertTrue(GameRuleEngine.canAcceptPick(picks, "B", "b1", maxPicksPerPlayer = 5))
    }

    @Test
    fun `pick order alternates from selected starting player`() {
        assertEquals(listOf("A", "B", "A", "B", "A", "B"), GameRuleEngine.buildPickOrder("A", 3))
        assertEquals(listOf("B", "A", "B", "A", "B", "A"), GameRuleEngine.buildPickOrder("B", 3))
    }

    @Test
    fun `first turn is twenty seconds and following turns are fifteen seconds`() {
        assertEquals(20, GameRuleEngine.turnDurationSeconds(0))
        assertEquals(15, GameRuleEngine.turnDurationSeconds(1))
        assertEquals(15, GameRuleEngine.turnDurationSeconds(9))
    }

    @Test
    fun `ranked scoring changes points and ranks safely`() {
        val profile = Profile(rank = "Gold", rankPoints = 4, wins = 0, losses = 0, draws = 0)

        val promoted = GameRuleEngine.applyRankedResult(profile, "win")
        assertEquals("Platinum", promoted.rank)
        assertEquals(0, promoted.rankPoints)
        assertEquals(1, promoted.wins)

        val lossAtZero = GameRuleEngine.applyRankedResult(promoted, "loss")
        assertEquals(0, lossAtZero.rankPoints)
        assertEquals(1, lossAtZero.losses)

        val draw = GameRuleEngine.applyRankedResult(lossAtZero, "draw")
        assertEquals(1, draw.draws)
        assertEquals(lossAtZero.rankPoints, draw.rankPoints)
    }

    @Test
    fun `reconnect timeout triggers after fifteen seconds`() {
        val disconnectedAt = 1_000L

        assertFalse(GameRuleEngine.hasReconnectTimedOut(disconnectedAt, disconnectedAt + 14_999L))
        assertTrue(GameRuleEngine.hasReconnectTimedOut(disconnectedAt, disconnectedAt + 15_000L))
    }

    private fun footballer(id: String, goals: Int) = Footballer(
        id = id,
        name = id,
        nationality = "Test",
        position = "FW",
        photoUrl = "",
        clubName = "Test FC",
        eligibleCompetitions = listOf("Premier League"),
        stats = listOf(PlayerStat("Premier League", "goals", goals))
    )
}
