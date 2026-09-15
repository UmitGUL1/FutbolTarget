package com.example.data.model

data class PlayerStat(
    val competition: String,
    val statType: String,
    val value: Int
)

data class Footballer(
    val id: String,
    val name: String,
    val nationality: String,
    val position: String,
    val photoUrl: String,
    val clubName: String,
    val eligibleCompetitions: List<String>,
    val stats: List<PlayerStat>
) {
    fun getStatValue(competition: String, statType: String): Int {
        val exact = stats.firstOrNull {
            it.competition.equals(competition, ignoreCase = true) &&
            it.statType.equals(statType, ignoreCase = true)
        }?.value
        if (exact != null) return exact

        if (competition.equals("Kariyer", ignoreCase = true) || competition.equals("Career", ignoreCase = true)) {
            val careerDirect = stats.firstOrNull {
                it.competition.equals("Kariyer", ignoreCase = true) && it.statType == statType
            }?.value
            if (careerDirect != null) return careerDirect
            return stats.filter { it.statType == statType }.sumOf { it.value }
        }

        if (competition.equals("5 Büyük Lig", ignoreCase = true) || competition.equals("Top 5 Leagues", ignoreCase = true)) {
            val big5 = setOf("Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1")
            val big5Direct = stats.firstOrNull {
                it.competition.equals("5 Büyük Lig", ignoreCase = true) && it.statType == statType
            }?.value
            if (big5Direct != null) return big5Direct
            return stats.filter { it.competition in big5 && it.statType == statType }.sumOf { it.value }
        }

        return 0
    }
}

data class Question(
    val id: String,
    val title: String,
    val competition: String,
    val statType: String,
    val target: Int,
    val pickCount: Int = 5,
    val difficulty: String = "Orta", // Kolay, Orta, Zor
    val eligibleCompetitions: List<String>,
    val description: String
)

data class PickSlot(
    val playerId: String, // "A" or "B"
    val pickNumber: Int,
    val footballer: Footballer?,
    val timedOut: Boolean = false
)

data class RevealStep(
    val playerId: String,
    val pickNumber: Int,
    val footballer: Footballer?,
    val timedOut: Boolean,
    val statValue: Int
)

data class MatchResult(
    val target: Int,
    val totalA: Int,
    val totalB: Int,
    val diffA: Int,
    val diffB: Int,
    val winner: String // "A", "B", "draw"
)

data class Profile(
    val username: String = "Futbol Dehası",
    val rank: String = "Gold",
    val rankPoints: Int = 3,
    val wins: Int = 14,
    val losses: Int = 8,
    val draws: Int = 3,
    val avatarSeed: String = "Futbolcu"
) {
    val totalMatches: Int get() = wins + losses + draws
    val winRate: Int get() = if (totalMatches > 0) (wins * 100) / totalMatches else 0
}

data class MatchHistoryItem(
    val id: String,
    val opponent: String,
    val questionTitle: String,
    val myTotal: Int,
    val opponentTotal: Int,
    val target: Int,
    val result: String, // "win", "loss", "draw"
    val date: String
)

data class SoloChallengeResult(
    val target: Int,
    val total: Int,
    val diff: Int,
    val accuracy: Int,
    val grade: String // "S", "A", "B", "C"
)
