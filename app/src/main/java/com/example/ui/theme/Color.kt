package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Futbol Target Luxury Performance System Palette Tokens
val LuxurySurface = Color(0xFF081422)
val LuxurySurfaceDim = Color(0xFF081422)
val LuxurySurfaceContainerLowest = Color(0xFF040F1D)
val LuxurySurfaceContainerLow = Color(0xFF111C2B)
val LuxurySurfaceContainer = Color(0xFF15202F)
val LuxurySurfaceContainerHigh = Color(0xFF1F2B3A)
val LuxurySurfaceContainerHighest = Color(0xFF2A3645)
val LuxurySurfaceBright = Color(0xFF2F3A49)
val LuxuryOnSurface = Color(0xFFD7E3F7)
val LuxuryOnSurfaceVariant = Color(0xFFC1C6D6)
val LuxuryOutline = Color(0xFF8B909F)
val LuxuryOutlineVariant = Color(0xFF414753)
val LuxuryPrimary = Color(0xFFACC7FF)
val LuxuryPrimaryContainer = Color(0xFF478FFF)
val LuxuryOnPrimaryContainer = Color(0xFF00285A)
val LuxurySecondary = Color(0xFFE9C25B) // Championship Gold
val LuxuryTertiary = Color(0xFF49DFA1) // Victory Green
val LuxuryError = Color(0xFFFFB4AB)

// Dark Navy Palette (User Specified)
val NavyMainBackground = LuxurySurface
val NavyAltBackground = LuxurySurfaceContainerLow
val NavyPrimarySurface = LuxurySurfaceContainer
val NavySecondarySurface = LuxurySurfaceContainerHigh
val NavyElevatedCard = LuxurySurfaceContainerHighest
val NavyPrimaryBlue = LuxuryPrimaryContainer
val NavySecondaryBlue = LuxuryPrimary
val GoldAccent = LuxurySecondary
val GoldAccentLight = Color(0xFFFFDF93)
val NavyPrimaryText = LuxuryOnSurface
val NavySecondaryText = LuxuryOutline
val NavyBorder = Color(0x1AFFFFFF) // subtle outline
val NavyBorderVisible = LuxuryOutlineVariant

// Semantic Colors (Reserved for win/error only)
val SemanticSuccess = LuxuryTertiary
val SemanticSuccessContainer = Color(0xFF00311F)
val SemanticError = Color(0xFFEF4444)
val SemanticErrorContainer = Color(0xFF450A0A)

// Players
val PlayerAColor = Color(0xFF3282F6)
val PlayerBColor = Color(0xFFF43F5E)

// Light Mode Navy Tint Palette
val LightMainBackground = Color(0xFFF0F4F8)
val LightPrimarySurface = Color(0xFFFFFFFF)
val LightSecondarySurface = Color(0xFFE8EEF5)
val LightElevatedCard = Color(0xFFDCE5F0)
val LightPrimaryBlue = Color(0xFF2563EB)
val LightSecondaryBlue = Color(0xFF3B82F6)
val LightPrimaryText = Color(0xFF071426)
val LightSecondaryText = Color(0xFF475569)
val LightBorder = Color(0x14000000)

// Backward-compatibility aliases mapped to new Navy system
val PitchGreen = NavyPrimaryBlue
val PitchGreenLight = NavySecondaryBlue
val PitchGreenDark = NavySecondarySurface
val TrophyGold = GoldAccent
val TrophyGoldLight = GoldAccentLight

