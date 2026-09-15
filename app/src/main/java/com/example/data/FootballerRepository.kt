package com.example.data

import com.example.data.footballers.FootballerData
import com.example.data.model.Footballer
import com.example.data.model.Question
import com.example.data.questions.QuestionBank

object FootballerRepository {

    val questions: List<Question> get() = QuestionBank.allQuestions

    val footballers: List<Footballer> get() = FootballerData.allFootballers

    fun getRandomQuestion(): Question {
        return questions.random()
    }

    fun getQuestionById(id: String): Question {
        return questions.firstOrNull { it.id == id } ?: questions.first()
    }

    fun searchEligibleFootballers(
        query: String,
        question: Question,
        pickedFootballerIds: Set<String>
    ): List<Footballer> {
        val normalizedQuery = query.trim().lowercase()
        val isGeneral = question.competition.equals("Kariyer", ignoreCase = true) ||
                question.competition.equals("5 Büyük Lig", ignoreCase = true) ||
                question.eligibleCompetitions.any {
                    it.equals("Kariyer", ignoreCase = true) || it.equals("5 Büyük Lig", ignoreCase = true)
                }

        return footballers
            .filter { footballer ->
                isGeneral || question.eligibleCompetitions.any { it in footballer.eligibleCompetitions }
            }
            .filter { footballer ->
                footballer.id !in pickedFootballerIds
            }
            .filter { footballer ->
                if (normalizedQuery.isEmpty()) true
                else footballer.name.lowercase().contains(normalizedQuery) ||
                        footballer.nationality.lowercase().contains(normalizedQuery) ||
                        footballer.clubName.lowercase().contains(normalizedQuery)
            }
            .sortedBy { it.name }
    }
}
