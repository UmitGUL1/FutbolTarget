package com.example.data.model

import com.example.BuildConfig

data class QuickTestOptions(
    val enabled: Boolean = false,
    val firstTurnSeconds: Int = GameRuleEngine.FIRST_TURN_SECONDS,
    val nextTurnSeconds: Int = GameRuleEngine.NEXT_TURN_SECONDS,
    val useBotOpponent: Boolean = false,
    val autoPickFootballers: Boolean = false,
    val jumpToReveal: Boolean = false,
    val forcedQuestionId: String? = null
)

object QuickTestConfig {
    val productionSafeDefaults = QuickTestOptions()

    fun resolve(requested: QuickTestOptions): QuickTestOptions {
        return if (BuildConfig.DEBUG && requested.enabled) requested else productionSafeDefaults
    }
}
