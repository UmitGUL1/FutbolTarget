package com.example.data.questions

import com.example.data.model.Question

object QuestionBank {
    val allQuestions: List<Question> = (
        CareerQuestions.list +
        Top5LeaguesQuestions.list +
        PremierLeagueQuestions.list +
        LaLigaSerieAQuestions.list +
        BundesligaLigue1UclQuestions.list
    ).distinctBy { it.id }

    init {
        // Verification during startup if needed
        require(allQuestions.size == 200) {
            "Expected 200 questions in QuestionBank, but found ${allQuestions.size}"
        }
    }
}
