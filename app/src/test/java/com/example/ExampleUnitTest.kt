package com.example

import com.example.data.FootballerRepository
import com.example.data.questions.QuestionBank
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testQuestionBankContainsExactly200Questions() {
        val questions = QuestionBank.allQuestions
        assertEquals("Question bank must contain exactly 200 questions", 200, questions.size)
        assertEquals("All 200 questions must have unique IDs", 200, questions.map { it.id }.toSet().size)
    }

    @Test
    fun testQuestionsHaveValidTargetsAndPickCounts() {
        val questions = FootballerRepository.questions
        for (q in questions) {
            assertTrue("Target must be greater than 0 for question ${q.id}", q.target > 0)
            assertTrue("Pick count must be between 1 and 5 for question ${q.id}", q.pickCount in 1..5)
            assertTrue("Title must not be blank for question ${q.id}", q.title.isNotBlank())
            assertTrue("Description must not be blank for question ${q.id}", q.description.isNotBlank())
        }
    }

    @Test
    fun testQuestionsCoverCareerAndTop5Leagues() {
        val questions = FootballerRepository.questions
        val categories = questions.map { it.competition }.toSet()

        assertTrue("Must include Kariyer", categories.contains("Kariyer"))
        assertTrue("Must include 5 Büyük Lig", categories.contains("5 Büyük Lig"))
        assertTrue("Must include Premier League", categories.contains("Premier League"))
        assertTrue("Must include La Liga", categories.contains("La Liga"))
        assertTrue("Must include Serie A", categories.contains("Serie A"))
        assertTrue("Must include Bundesliga", categories.contains("Bundesliga"))
        assertTrue("Must include Ligue 1", categories.contains("Ligue 1"))
    }

    @Test
    fun testFootballerStatAggregationForCareerAndTop5Leagues() {
        val footballers = FootballerRepository.footballers
        val messi = footballers.first { it.id == "lionel-messi" }
        assertTrue("Messi career goals must be over 800", messi.getStatValue("Kariyer", "goals") >= 800)
        assertTrue("Messi 5 Buyuk Lig goals must be over 450", messi.getStatValue("5 Büyük Lig", "goals") >= 450)
        assertEquals("Messi La Liga goals must be 474", 474, messi.getStatValue("La Liga", "goals"))
    }

    @Test
    fun testCategoryDistributionForLocalMatchSelection() {
        val questions = FootballerRepository.questions
        val careerCount = questions.count { it.competition.equals("Kariyer", ignoreCase = true) }
        val top5Count = questions.count { it.competition.equals("5 Büyük Lig", ignoreCase = true) }
        val plCount = questions.count { it.competition.equals("Premier League", ignoreCase = true) }
        val laLigaCount = questions.count { it.competition.equals("La Liga", ignoreCase = true) }
        val serieACount = questions.count { it.competition.equals("Serie A", ignoreCase = true) }
        val bundesligaCount = questions.count { it.competition.equals("Bundesliga", ignoreCase = true) }
        val ligue1Count = questions.count { it.competition.equals("Ligue 1", ignoreCase = true) }
        val uclCount = questions.count { it.competition.equals("Champions League", ignoreCase = true) }
        val intlCount = questions.count { it.competition.equals("International", ignoreCase = true) }

        assertEquals("Kariyer should have 50 questions", 50, careerCount)
        assertEquals("5 Büyük Lig should have 35 questions", 35, top5Count)
        assertEquals("Premier League should have 40 questions", 40, plCount)
        assertEquals("La Liga should have 20 questions", 20, laLigaCount)
        assertEquals("Serie A should have 20 questions", 20, serieACount)
        assertEquals("Bundesliga should have 15 questions", 15, bundesligaCount)
        assertEquals("Ligue 1 should have 10 questions", 10, ligue1Count)
        assertEquals("Champions League should have 5 questions", 5, uclCount)
        assertEquals("International should have 5 questions", 5, intlCount)
        assertEquals("Total sum should be 200", 200, careerCount + top5Count + plCount + laLigaCount + serieACount + bundesligaCount + ligue1Count + uclCount + intlCount)

        // Verify retrieval by ID
        for (q in questions) {
            val retrieved = FootballerRepository.getQuestionById(q.id)
            assertEquals("Retrieved question should match ID ${q.id}", q.id, retrieved.id)
        }
    }

    @Test
    fun testRoomCodeGeneratorGeneratesValid6CharacterCodes() {
        for (i in 1..100) {
            val code = com.example.data.supabase.RoomCodeGenerator.generateCode()
            assertEquals("Code must be 6 characters long", 6, code.length)
            assertTrue("Code must be valid according to isValidCode", com.example.data.supabase.RoomCodeGenerator.isValidCode(code))
            assertFalse("Code must not contain 0", code.contains('0'))
            assertFalse("Code must not contain O", code.contains('O'))
            assertFalse("Code must not contain 1", code.contains('1'))
            assertFalse("Code must not contain I", code.contains('I'))
        }
    }

    @Test
    fun testMatchRuleEngineWinnerDetermination() {
        val target = 500

        // A is closer
        assertEquals("A", com.example.data.supabase.MatchRuleEngine.calculateWinner(target, 490, 480))
        // B is closer (with overshoot)
        assertEquals("B", com.example.data.supabase.MatchRuleEngine.calculateWinner(target, 480, 505))
        // Exactly equal difference -> draw
        assertEquals("draw", com.example.data.supabase.MatchRuleEngine.calculateWinner(target, 490, 510))
        // Both exact match -> draw
        assertEquals("draw", com.example.data.supabase.MatchRuleEngine.calculateWinner(target, 500, 500))
    }

    @Test
    fun testDuplicatePickDetection() {
        val picks = listOf(
            com.example.data.supabase.OnlinePick(
                id = "p1",
                matchId = "m1",
                playerId = "u1",
                pickNumber = 1,
                footballerId = "lionel-messi",
                isTimeout = false,
                statValue = 0
            ),
            com.example.data.supabase.OnlinePick(
                id = "p2",
                matchId = "m1",
                playerId = "u2",
                pickNumber = 1,
                footballerId = null,
                isTimeout = true,
                statValue = 0
            )
        )

        assertTrue(com.example.data.supabase.MatchRuleEngine.isPickDuplicate(picks, "lionel-messi"))
        assertTrue(com.example.data.supabase.MatchRuleEngine.isPickDuplicate(picks, "LIONEL-MESSI"))
        assertFalse(com.example.data.supabase.MatchRuleEngine.isPickDuplicate(picks, "cristiano-ronaldo"))
    }
}

