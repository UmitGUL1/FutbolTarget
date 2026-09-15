package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppThemeMode
import com.example.data.FootballerRepository
import com.example.data.UserPreferencesRepository
import com.example.ui.components.FutbolTargetTopBar
import com.example.ui.navigation.BottomNavItem
import com.example.ui.navigation.Screen
import com.example.ui.screens.challenges.ChallengesScreen
import com.example.ui.screens.database.FootballerListScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.localmatch.LocalMatchScreen
import com.example.ui.screens.localmatch.LocalMatchSelectScreen
import com.example.ui.screens.online.OnlineLobbyScreen
import com.example.ui.screens.online.OnlineMatchScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.solo.SoloChallengeScreen
import com.example.ui.theme.FutbolTargetTheme
import com.example.data.supabase.OnlineMatchRepository
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userPreferencesRepository = UserPreferencesRepository(applicationContext)

        setContent {
            val themeMode by userPreferencesRepository.themeMode.collectAsState()
            val profile by userPreferencesRepository.profile.collectAsState()
            val matchHistory by userPreferencesRepository.matchHistory.collectAsState()

            val isSystemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemDark
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
            }

            FutbolTargetTheme(darkTheme = isDark) {
                FutbolTargetApp(
                    userPreferencesRepository = userPreferencesRepository,
                    isDark = isDark,
                    onToggleTheme = {
                        val nextMode = if (isDark) AppThemeMode.LIGHT else AppThemeMode.DARK
                        userPreferencesRepository.setThemeMode(nextMode)
                    }
                )
            }
        }
    }
}

@Composable
fun FutbolTargetApp(
    userPreferencesRepository: UserPreferencesRepository,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val profile by userPreferencesRepository.profile.collectAsState()
    val matchHistory by userPreferencesRepository.matchHistory.collectAsState()
    val themeMode by userPreferencesRepository.themeMode.collectAsState()

    val context = LocalContext.current
    val onlineMatchRepository = remember { OnlineMatchRepository(context) }

    val isMatchScreen = currentRoute?.startsWith("local_match") == true ||
            currentRoute?.startsWith("solo_match") == true ||
            currentRoute?.startsWith("online_match") == true ||
            currentRoute == Screen.LocalMatchSelect.route ||
            currentRoute == Screen.OnlineLobby.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isMatchScreen) {
                FutbolTargetTopBar(
                    title = "FUTBOL TARGET",
                    subtitle = "Hedefe En Yakın Kadroyu Kur",
                    isDarkMode = isDark,
                    onToggleTheme = onToggleTheme
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !isMatchScreen,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    val bottomNavItems = listOf(
                        BottomNavItem.HOME,
                        BottomNavItem.CHALLENGES,
                        BottomNavItem.DATABASE,
                        BottomNavItem.PROFILE
                    )

                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    profile = profile,
                    onStartLocalMatch = { qId ->
                        navController.navigate(Screen.LocalMatch.createRoute(qId))
                    },
                    onStartSoloMatch = { qId ->
                        navController.navigate(Screen.SoloMatch.createRoute(qId))
                    },
                    onNavigateToLocalMatchSelect = {
                        navController.navigate(Screen.LocalMatchSelect.route)
                    },
                    onNavigateToOnlineLobby = {
                        navController.navigate(Screen.OnlineLobby.route)
                    },
                    onNavigateToChallenges = {
                        navController.navigate(Screen.Challenges.route)
                    },
                    onNavigateToDatabase = {
                        navController.navigate(Screen.Database.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            composable(Screen.Challenges.route) {
                ChallengesScreen(
                    onStartLocalMatch = { qId ->
                        navController.navigate(Screen.LocalMatch.createRoute(qId))
                    },
                    onStartSoloMatch = { qId ->
                        navController.navigate(Screen.SoloMatch.createRoute(qId))
                    }
                )
            }

            composable(Screen.Database.route) {
                FootballerListScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    profile = profile,
                    matchHistory = matchHistory,
                    currentThemeMode = themeMode,
                    onSetThemeMode = { newMode ->
                        userPreferencesRepository.setThemeMode(newMode)
                    },
                    onUpdateUsername = { newName ->
                        userPreferencesRepository.updateProfile(username = newName)
                    }
                )
            }

            composable(Screen.LocalMatchSelect.route) {
                LocalMatchSelectScreen(
                    onSelectQuestion = { qId ->
                        navController.navigate(Screen.LocalMatch.createRoute(qId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.LocalMatch.route,
                arguments = listOf(navArgument("questionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val questionId = backStackEntry.arguments?.getString("questionId") ?: FootballerRepository.questions.first().id
                LocalMatchScreen(
                    questionId = questionId,
                    onNavigateBack = { navController.popBackStack() },
                    onSelectOtherQuestion = { navController.popBackStack() },
                    onMatchFinished = { questionTitle, myTotal, opponentTotal, target, result ->
                        userPreferencesRepository.recordMatch(
                            opponentName = "Oyuncu B (Aynı Telefon)",
                            questionTitle = questionTitle,
                            myTotal = myTotal,
                            opponentTotal = opponentTotal,
                            target = target,
                            result = result
                        )
                    }
                )
            }

            composable(
                route = Screen.SoloMatch.route,
                arguments = listOf(navArgument("questionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val questionId = backStackEntry.arguments?.getString("questionId") ?: FootballerRepository.questions.first().id
                SoloChallengeScreen(
                    questionId = questionId,
                    onNavigateBack = { navController.popBackStack() },
                    onChallengeFinished = { questionTitle, total, target, outcome ->
                        userPreferencesRepository.recordMatch(
                            opponentName = "Solo Hedef",
                            questionTitle = questionTitle,
                            myTotal = total,
                            opponentTotal = target,
                            target = target,
                            result = outcome
                        )
                    }
                )
            }

            composable(Screen.OnlineLobby.route) {
                OnlineLobbyScreen(
                    repository = onlineMatchRepository,
                    onNavigateBack = { navController.popBackStack() },
                    onEnterMatch = { matchId, roomCode, role ->
                        navController.navigate(Screen.OnlineMatch.createRoute(matchId, roomCode, role)) {
                            popUpTo(Screen.OnlineLobby.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.OnlineMatch.route,
                arguments = listOf(
                    navArgument("matchId") { type = NavType.StringType },
                    navArgument("roomCode") { type = NavType.StringType },
                    navArgument("role") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString("matchId").orEmpty()
                val roomCode = backStackEntry.arguments?.getString("roomCode").orEmpty()
                val role = backStackEntry.arguments?.getString("role").orEmpty().ifBlank { "A" }

                OnlineMatchScreen(
                    matchId = matchId,
                    roomCode = roomCode,
                    role = role,
                    repository = onlineMatchRepository,
                    onNavigateBack = {
                        navController.popBackStack(Screen.Home.route, false)
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
