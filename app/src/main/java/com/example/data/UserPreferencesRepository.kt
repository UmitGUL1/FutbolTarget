package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.MatchHistoryItem
import com.example.data.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("futbol_target_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        AppThemeMode.valueOf(prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name)
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    private val _matchHistory = MutableStateFlow(loadHistory())
    val matchHistory: StateFlow<List<MatchHistoryItem>> = _matchHistory.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun updateProfile(username: String? = null) {
        val current = _profile.value
        val updated = current.copy(
            username = username ?: current.username
        )
        saveProfile(updated)
    }

    fun recordMatch(
        opponentName: String,
        questionTitle: String,
        myTotal: Int,
        opponentTotal: Int,
        target: Int,
        result: String
    ) {
        val currentProfile = _profile.value
        var wins = currentProfile.wins
        var losses = currentProfile.losses
        var draws = currentProfile.draws
        var rankPoints = currentProfile.rankPoints
        var rank = currentProfile.rank

        when (result) {
            "win" -> {
                wins += 1
                rankPoints += 1
                if (rankPoints >= 5) {
                    rank = getNextRank(rank)
                    rankPoints = 0
                }
            }
            "loss" -> {
                losses += 1
                if (rankPoints > 0) rankPoints -= 1
            }
            "draw" -> {
                draws += 1
            }
        }

        val updatedProfile = currentProfile.copy(
            wins = wins,
            losses = losses,
            draws = draws,
            rankPoints = rankPoints,
            rank = rank
        )
        saveProfile(updatedProfile)

        val newItem = MatchHistoryItem(
            id = "match_${System.currentTimeMillis()}",
            opponent = opponentName,
            questionTitle = questionTitle,
            myTotal = myTotal,
            opponentTotal = opponentTotal,
            target = target,
            result = result,
            date = "Bugün"
        )
        val updatedHistory = listOf(newItem) + _matchHistory.value
        saveHistory(updatedHistory)
    }

    private fun getNextRank(currentRank: String): String {
        val ranks = listOf("Bronze", "Silver", "Gold", "Platinum", "Diamond", "Elite", "Legend")
        val index = ranks.indexOf(currentRank)
        return if (index != -1 && index < ranks.size - 1) ranks[index + 1] else "Legend"
    }

    private fun loadProfile(): Profile {
        val username = prefs.getString("user_username", "Target Ustası") ?: "Target Ustası"
        val rank = prefs.getString("user_rank", "Gold") ?: "Gold"
        val rankPoints = prefs.getInt("user_rank_points", 3)
        val wins = prefs.getInt("user_wins", 12)
        val losses = prefs.getInt("user_losses", 7)
        val draws = prefs.getInt("user_draws", 4)
        return Profile(
            username = username,
            rank = rank,
            rankPoints = rankPoints,
            wins = wins,
            losses = losses,
            draws = draws
        )
    }

    private fun saveProfile(profile: Profile) {
        prefs.edit()
            .putString("user_username", profile.username)
            .putString("user_rank", profile.rank)
            .putInt("user_rank_points", profile.rankPoints)
            .putInt("user_wins", profile.wins)
            .putInt("user_losses", profile.losses)
            .putInt("user_draws", profile.draws)
            .apply()
        _profile.value = profile
    }

    private fun loadHistory(): List<MatchHistoryItem> {
        val jsonString = prefs.getString("match_history_json", null)
        if (jsonString.isNullOrEmpty()) {
            // Default starter sample history items
            return listOf(
                MatchHistoryItem(
                    id = "init_1",
                    opponent = "Arkadaş",
                    questionTitle = "5 Futbolcuyla 500 Premier League Golüne Yaklaş",
                    myTotal = 498,
                    opponentTotal = 482,
                    target = 500,
                    result = "win",
                    date = "Dün"
                ),
                MatchHistoryItem(
                    id = "init_2",
                    opponent = "Misafir Oyuncu",
                    questionTitle = "5 Futbolcuyla 1500 Premier League Maçına Yaklaş",
                    myTotal = 1530,
                    opponentTotal = 1490,
                    target = 1500,
                    result = "loss",
                    date = "3 gün önce"
                )
            )
        }

        val list = mutableListOf<MatchHistoryItem>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MatchHistoryItem(
                        id = obj.getString("id"),
                        opponent = obj.getString("opponent"),
                        questionTitle = obj.getString("questionTitle"),
                        myTotal = obj.getInt("myTotal"),
                        opponentTotal = obj.getInt("opponentTotal"),
                        target = obj.getInt("target"),
                        result = obj.getString("result"),
                        date = obj.getString("date")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveHistory(history: List<MatchHistoryItem>) {
        val array = JSONArray()
        for (item in history.take(30)) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("opponent", item.opponent)
                put("questionTitle", item.questionTitle)
                put("myTotal", item.myTotal)
                put("opponentTotal", item.opponentTotal)
                put("target", item.target)
                put("result", item.result)
                put("date", item.date)
            }
            array.put(obj)
        }
        prefs.edit().putString("match_history_json", array.toString()).apply()
        _matchHistory.value = history
    }
}
