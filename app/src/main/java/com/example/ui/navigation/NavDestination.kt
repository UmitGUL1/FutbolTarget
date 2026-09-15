package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Challenges : Screen("challenges")
    data object Database : Screen("database")
    data object Profile : Screen("profile")
    data object LocalMatchSelect : Screen("local_match_select")
    data object LocalMatch : Screen("local_match/{questionId}") {
        fun createRoute(questionId: String) = "local_match/$questionId"
    }
    data object SoloMatch : Screen("solo_match/{questionId}") {
        fun createRoute(questionId: String) = "solo_match/$questionId"
    }
    data object OnlineLobby : Screen("online_lobby")
    data object OnlineMatch : Screen("online_match/{matchId}/{roomCode}/{role}") {
        fun createRoute(matchId: String, roomCode: String, role: String) = "online_match/$matchId/$roomCode/$role"
    }
}

enum class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = Screen.Home.route,
        title = "Oyna",
        selectedIcon = Icons.Filled.SportsSoccer,
        unselectedIcon = Icons.Outlined.SportsSoccer
    ),
    CHALLENGES(
        route = Screen.Challenges.route,
        title = "Hedefler",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents
    ),
    DATABASE(
        route = Screen.Database.route,
        title = "Oyuncular",
        selectedIcon = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group
    ),
    PROFILE(
        route = Screen.Profile.route,
        title = "Profil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
