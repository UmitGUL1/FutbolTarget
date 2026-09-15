package com.example

import com.example.data.FootballerRepository
import com.example.data.model.Footballer
import com.example.data.model.GameRuleEngine
import com.example.data.model.PickSlot
import com.example.data.model.PlayerStat
import com.example.data.model.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameFlowIntegrationTest {
    private val question = Question(
        id = "flow-question",
        title = "Premier League'de 500 gole yaklaş",
        competition = "Premier League",
        statType = "goals",
        target = 500,
        pickCount = 5,
        eligibleCompetitions = listOf("Premier League"),
        description = "Flow test question"
    )

    @Test
    fun `full local match flow rejects duplicates reveals in A B order and calculates winner`() {
        val players = listOf(
            footballer("a1", 120), footballer("b1", 80),
            footballer("a2", 110), footballer("b2", 90),
            footballer("a3", 100), footballer("b3", 95),
            footballer("a4", 90), footballer("b4", 85),
            footballer("a5", 75), footballer("b5", 70)
        )
        val scripted = listOf("a1", "b1", "a2", "b2", "a3", "b3", "a4", "b4", "a5", "b5")
        val pickOrder = GameRuleEngine.buildPickOrder("A", question.pickCount)
        val picks = mutableListOf<PickSlot>()

        scripted.forEachIndexed { index, footballerId ->
            val playerId = pickOrder[index]
            val candidate = players.first { it.id == footballerId }
            assertTrue(GameRuleEngine.canAcceptPick(picks, playerId, candidate.id, question.pickCount))
            picks += PickSlot(playerId, GameRuleEngine.picksForPlayer(picks, playerId) + 1, candidate)
        }

        assertFalse("A player already selected in the match cannot be selected again", GameRuleEngine.canAcceptPick(picks, "B", "a1", question.pickCount))

        val revealSteps = GameRuleEngine.buildRevealSteps(picks, question)
        assertEquals(listOf("A1", "B1", "A2", "B2", "A3", "B3", "A4", "B4", "A5", "B5"), revealSteps.map { "${it.playerId}${it.pickNumber}" })

        val result = GameRuleEngine.calculateResult(question, revealSteps)
        assertEquals(495, result.totalA)
        assertEquals(420, result.totalB)
        assertEquals("A", result.winner)
    }

    @Test
    fun `timeout turn records zero and match still reaches result`() {
        val picks = listOf(
            PickSlot("A", 1, footballer("a1", 200)),
            PickSlot("B", 1, footballer("b1", 190)),
            PickSlot("A", 2, null, timedOut = true),
            PickSlot("B", 2, footballer("b2", 180))
        )
        val twoPickQuestion = question.copy(pickCount = 2, target = 390)

        val revealSteps = GameRuleEngine.buildRevealSteps(picks, twoPickQuestion)
        val result = GameRuleEngine.calculateResult(twoPickQuestion, revealSteps)

        assertEquals(0, revealSteps.first { it.playerId == "A" && it.pickNumber == 2 }.statValue)
        assertEquals(200, result.totalA)
        assertEquals(370, result.totalB)
        assertEquals("B", result.winner)
    }

    @Test
    fun `repository search only returns eligible unpicked footballers`() {
        val eligible = FootballerRepository.searchEligibleFootballers("ronaldo", question, emptySet())
        assertTrue(eligible.isNotEmpty())
        assertTrue(eligible.all { footballer -> question.eligibleCompetitions.any { it in footballer.eligibleCompetitions } })

        val first = eligible.first()
        val afterPick = FootballerRepository.searchEligibleFootballers(first.name.take(4), question, setOf(first.id))
        assertFalse(afterPick.any { it.id == first.id })
    }

    @Test
    fun `one hundred random matches never enter invalid scoring state`() {
        val random = Random(1234)
        val pool = (1..30).map { footballer("p$it", random.nextInt(0, 220)) }

        repeat(100) { matchIndex ->
            val pickCount = random.nextInt(1, 6)
            val matchQuestion = question.copy(id = "stress-$matchIndex", pickCount = pickCount, target = random.nextInt(100, 800))
            val pickOrder = GameRuleEngine.buildPickOrder(if (matchIndex % 2 == 0) "A" else "B", pickCount)
            val picks = mutableListOf<PickSlot>()

            pickOrder.forEach { playerId ->
                if (random.nextInt(10) == 0) {
                    picks += PickSlot(playerId, GameRuleEngine.picksForPlayer(picks, playerId) + 1, null, timedOut = true)
                } else {
                    val candidate = pool.first { f -> !GameRuleEngine.isDuplicateFootballer(picks, f.id) }
                    assertTrue(GameRuleEngine.canAcceptPick(picks, playerId, candidate.id, pickCount))
                    picks += PickSlot(playerId, GameRuleEngine.picksForPlayer(picks, playerId) + 1, candidate)
                }
            }

            val pickedIds = picks.mapNotNull { it.footballer?.id }
            assertEquals(pickedIds.size, pickedIds.toSet().size)
            assertTrue(picks.count { it.playerId == "A" } <= pickCount)
            assertTrue(picks.count { it.playerId == "B" } <= pickCount)

            val revealSteps = GameRuleEngine.buildRevealSteps(picks, matchQuestion)
            val result = GameRuleEngine.calculateResult(matchQuestion, revealSteps)

            assertEquals(picks.size, revealSteps.size)
            assertTrue(result.totalA >= 0)
            assertTrue(result.totalB >= 0)
            assertTrue(result.diffA >= 0)
            assertTrue(result.diffB >= 0)
            assertTrue(result.winner in setOf("A", "B", "draw"))
            assertNotNull(result.winner)
        }
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


