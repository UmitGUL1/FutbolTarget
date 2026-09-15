package com.example.ui.screens.online

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FootballerRepository
import com.example.data.model.Footballer
import com.example.data.supabase.OnlineMatchState
import com.example.data.supabase.OnlinePick
import com.example.ui.components.AppButton
import com.example.ui.components.FootballerPhotoPlaceholder
import com.example.ui.components.ModeChip
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LuxuryError
import com.example.ui.theme.LuxuryOnSurface
import com.example.ui.theme.LuxuryOutline
import com.example.ui.theme.LuxuryOutlineVariant
import com.example.ui.theme.LuxuryPrimary
import com.example.ui.theme.LuxuryPrimaryContainer
import com.example.ui.theme.LuxurySecondary
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceContainer
import com.example.ui.theme.LuxurySurfaceContainerHigh
import com.example.ui.theme.LuxurySurfaceContainerHighest
import com.example.ui.theme.LuxurySurfaceContainerLow
import com.example.ui.theme.LuxuryTertiary
import com.example.ui.theme.PlayerAColor
import com.example.ui.theme.PlayerBColor
import com.example.ui.theme.SemanticError
import com.example.ui.theme.SemanticSuccess
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * Suspenseful, dramatic round-by-round online result verification view.
 * Reveals each player's picks sequentially with counting numbers, haptic cues,
 * tense anticipation, and final championship celebration.
 */
@Composable
fun SuspensefulResultVerificationView(
    state: OnlineMatchState,
    rematchWaiting: Boolean,
    onRequestRematch: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Verification sequence phase:
    // 0: Initial scanning / server handshake
    // 1..5: Round 1 to 5 sequential reveal
    // 6: Calculating delta to target
    // 7: Final verdict & championship celebration
    var verificationPhase by remember { mutableIntStateOf(0) }
    // Sub-phase during round reveal: 0 = Player A revealing, 1 = Player B revealing
    var roundSubPhase by remember { mutableIntStateOf(0) }
    var skipToEnd by remember { mutableStateOf(false) }

    fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40)
                }
            }
        } catch (_: Throwable) {}
    }

    // Step-by-step dramatic progression timer
    LaunchedEffect(skipToEnd) {
        if (skipToEnd) {
            verificationPhase = 7
            return@LaunchedEffect
        }

        // Phase 0: Scanning
        verificationPhase = 0
        delay(1400L)

        // Rounds 1 to 5
        for (round in 1..5) {
            verificationPhase = round
            roundSubPhase = 0
            triggerHapticFeedback()
            delay(1300L) // Player A reveal time

            roundSubPhase = 1
            triggerHapticFeedback()
            delay(1300L) // Player B reveal time
        }

        // Phase 6: Calculating delta
        verificationPhase = 6
        triggerHapticFeedback()
        delay(1800L)

        // Phase 7: Final proclamation!
        verificationPhase = 7
        triggerHapticFeedback()
    }

    // Determine current displayed picks up to the current revealed round
    val activeRound = if (skipToEnd || verificationPhase >= 6) 5 else verificationPhase
    val aPicksRevealed = when {
        skipToEnd || verificationPhase >= 6 -> 5
        verificationPhase in 1..5 -> verificationPhase
        else -> 0
    }
    val bPicksRevealed = when {
        skipToEnd || verificationPhase >= 6 -> 5
        verificationPhase in 1..5 -> if (roundSubPhase >= 1) verificationPhase else verificationPhase - 1
        else -> 0
    }

    val playerAPicks = state.picks
        .filter { it.playerId == state.playerAId }
        .sortedBy { it.pickNumber }
    val playerBPicks = state.picks
        .filter { it.playerId == state.playerBId }
        .sortedBy { it.pickNumber }

    // Running totals calculation
    val runningTotalA = playerAPicks.take(aPicksRevealed).sumOf { it.statValue }
    val runningTotalB = playerBPicks.take(bPicksRevealed).sumOf { it.statValue }

    val animatedTotalA by animateIntAsState(
        targetValue = runningTotalA,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "running_a"
    )
    val animatedTotalB by animateIntAsState(
        targetValue = runningTotalB,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "running_b"
    )

    // Scanning line animation
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_progress"
    )

    val isWinner = (state.myRole == "A" && state.winner == "A") || (state.myRole == "B" && state.winner == "B")
    val isDraw = state.winner == "draw"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top status & skip button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (verificationPhase < 7) LuxurySecondary else LuxuryTertiary)
                )
                Text(
                    text = if (verificationPhase < 7) "DOĞRULANIYOR" else "RESMİ SONUÇ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = if (verificationPhase < 7) LuxurySecondary else LuxuryTertiary
                )
            }

            if (verificationPhase < 7) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LuxurySurfaceContainerHighest)
                        .clickable { skipToEnd = true }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Hepsini Göster",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = LuxuryPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Hızlı Geç",
                        tint = LuxuryPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Banner Monolith
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
            border = BorderStroke(1.dp, LuxuryOutlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.question.statType.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = LuxuryOutline
                )
                Text(
                    text = "${state.question.target}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-1).sp
                    ),
                    color = LuxurySecondary
                )
                Text(
                    text = "Hedefe En Çok Yaklaşan Kazanır",
                    style = MaterialTheme.typography.bodySmall,
                    color = LuxuryOnSurface.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIVE SCOREBOARD COMPARISON CARD
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainerLow),
            border = BorderStroke(1.dp, LuxuryOutlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player A Live Total
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PlayerAColor)
                        )
                        Text(
                            text = if (state.myRole == "A") "Sen (A)" else "Rakip (A)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PlayerAColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$animatedTotalA",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = PlayerAColor
                    )
                    Text(
                        text = "$aPicksRevealed / 5 Seçim",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuxuryOutline
                    )
                }

                // VS / Status Center
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(LuxurySurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = LuxuryOutline
                        )
                    }
                }

                // Player B Live Total
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PlayerBColor)
                        )
                        Text(
                            text = if (state.myRole == "B") "Sen (B)" else "Rakip (B)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PlayerBColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$animatedTotalB",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = PlayerBColor
                    )
                    Text(
                        text = "$bPicksRevealed / 5 Seçim",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuxuryOutline
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // STAGE-SPECIFIC DYNAMIC CONTENT
        when (verificationPhase) {
            0 -> {
                // Initial scanning phase
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
                    border = BorderStroke(1.dp, LuxurySecondary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = LuxurySecondary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SEÇİMLER DOĞRULANIYOR",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = LuxurySecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sunucu resmi istatistikleri ve iki oyuncunun tercihlerini eşleştiriyor...",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = LuxuryOutline
                        )
                    }
                }
            }

            in 1..5 -> {
                // Round by round reveal
                val roundIndex = verificationPhase - 1
                val roundTitle = when (verificationPhase) {
                    1 -> "1. TUR: AÇILIŞ HAMLELERİ"
                    2 -> "2. TUR: STRATEJİ BELİRGİNLEŞİYOR"
                    3 -> "3. TUR: TEMPO YÜKSELİYOR"
                    4 -> "4. TUR: KRİTİK EŞİK!"
                    5 -> "5. TUR: SON VE BELİRLEYİCİ SEÇİM! 🔥"
                    else -> "TUR $verificationPhase"
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Round indicator pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (verificationPhase == 5) LuxurySecondary.copy(alpha = 0.2f) else LuxuryPrimaryContainer.copy(alpha = 0.2f))
                            .border(1.dp, if (verificationPhase == 5) LuxurySecondary else LuxuryPrimaryContainer, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = roundTitle,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = if (verificationPhase == 5) LuxurySecondary else LuxuryPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Round side-by-side pick reveal
                    val pickA = playerAPicks.getOrNull(roundIndex)
                    val pickB = playerBPicks.getOrNull(roundIndex)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Player A Card in current round
                        RevealingPickCard(
                            pick = pickA,
                            playerLabel = if (state.myRole == "A") "Sen (A)" else "Rakip (A)",
                            playerColor = PlayerAColor,
                            statType = state.question.statType,
                            isRevealed = true,
                            isHighlight = roundSubPhase == 0,
                            modifier = Modifier.weight(1f)
                        )

                        // Player B Card in current round
                        RevealingPickCard(
                            pick = pickB,
                            playerLabel = if (state.myRole == "B") "Sen (B)" else "Rakip (B)",
                            playerColor = PlayerBColor,
                            statType = state.question.statType,
                            isRevealed = roundSubPhase >= 1,
                            isHighlight = roundSubPhase == 1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { verificationPhase / 5f },
                        color = LuxurySecondary,
                        trackColor = LuxurySurfaceContainerHighest,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            }

            6 -> {
                // Calculating delta suspense phase
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
                    border = BorderStroke(1.5.dp, LuxurySecondary.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = LuxurySecondary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "HEDEFE UZAKLIKLAR HESAPLANIYOR...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = LuxurySecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("OYUNCU A", style = MaterialTheme.typography.labelSmall, color = PlayerAColor)
                                Text(
                                    "${state.playerATotal}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = PlayerAColor
                                )
                                Text("Fark: ${state.diffA}", style = MaterialTheme.typography.labelSmall, color = LuxuryOutline)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("OYUNCU B", style = MaterialTheme.typography.labelSmall, color = PlayerBColor)
                                Text(
                                    "${state.playerBTotal}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = PlayerBColor
                                )
                                Text("Fark: ${state.diffB}", style = MaterialTheme.typography.labelSmall, color = LuxuryOutline)
                            }
                        }
                    }
                }
            }

            7 -> {
                // Final celebration & review phase!
                FinalCelebrationCard(
                    state = state,
                    isWinner = isWinner,
                    isDraw = isDraw,
                    rematchWaiting = rematchWaiting,
                    onRequestRematch = onRequestRematch,
                    onNavigateBack = onNavigateBack
                )
            }
        }

        // Complete 5-pick history list below during verification or at the end
        if (verificationPhase >= 1) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "TUR GEÇMİŞİ VE SEÇİMLER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = LuxuryOutline,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            for (i in 0 until 5) {
                val roundPickA = playerAPicks.getOrNull(i)
                val roundPickB = playerBPicks.getOrNull(i)
                val isRoundRevealed = i < activeRound

                RoundHistoryRow(
                    roundNumber = i + 1,
                    pickA = roundPickA,
                    pickB = roundPickB,
                    isRevealed = isRoundRevealed,
                    statType = state.question.statType
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun RevealingPickCard(
    pick: OnlinePick?,
    playerLabel: String,
    playerColor: Color,
    statType: String,
    isRevealed: Boolean,
    isHighlight: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isHighlight) LuxurySecondary else playerColor.copy(alpha = 0.4f),
        label = "border_color"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainerHigh),
        border = BorderStroke(if (isHighlight) 2.dp else 1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = playerLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = playerColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isRevealed || pick == null) {
                // Hidden card placeholder
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(LuxurySurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = playerColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Açılıyor...",
                    style = MaterialTheme.typography.bodySmall,
                    color = LuxuryOutline
                )
                Text(
                    text = "???",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = LuxuryOutline
                )
            } else if (pick.isTimeout) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SemanticError.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Zaman Aşımı",
                        tint = SemanticError,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Zaman Aşımı",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = SemanticError
                )
                Text(
                    text = "+0 $statType",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = SemanticError
                )
            } else {
                val footballer = FootballerRepository.footballers.firstOrNull { it.id == pick.footballerId }
                FootballerPhotoPlaceholder(
                    name = footballer?.name ?: "Futbolcu",
                    size = 52.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = footballer?.name ?: "Futbolcu",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = LuxuryOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = footballer?.clubName.orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = LuxuryOutline,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(playerColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${pick.statValue} $statType",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = playerColor
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundHistoryRow(
    roundNumber: Int,
    pickA: OnlinePick?,
    pickB: OnlinePick?,
    isRevealed: Boolean,
    statType: String
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainerHigh),
        border = BorderStroke(0.5.dp, LuxuryOutlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$roundNumber.",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = LuxurySecondary,
                modifier = Modifier.width(22.dp)
            )

            // Player A pick summary
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isRevealed || pickA == null) {
                    Text("???", style = MaterialTheme.typography.bodySmall, color = LuxuryOutline)
                } else if (pickA.isTimeout) {
                    Text("Zaman Aşımı", style = MaterialTheme.typography.bodySmall, color = SemanticError)
                    Text("+0", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SemanticError)
                } else {
                    val name = FootballerRepository.footballers.firstOrNull { it.id == pickA.footballerId }?.name ?: "Futbolcu"
                    Text(name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = LuxuryOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+${pickA.statValue}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black), color = PlayerAColor)
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(1.dp)
                    .height(18.dp)
                    .background(LuxuryOutlineVariant)
            )

            // Player B pick summary
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isRevealed || pickB == null) {
                    Text("???", style = MaterialTheme.typography.bodySmall, color = LuxuryOutline)
                } else if (pickB.isTimeout) {
                    Text("Zaman Aşımı", style = MaterialTheme.typography.bodySmall, color = SemanticError)
                    Text("+0", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SemanticError)
                } else {
                    val name = FootballerRepository.footballers.firstOrNull { it.id == pickB.footballerId }?.name ?: "Futbolcu"
                    Text(name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = LuxuryOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+${pickB.statValue}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black), color = PlayerBColor)
                }
            }
        }
    }
}

@Composable
private fun FinalCelebrationCard(
    state: OnlineMatchState,
    isWinner: Boolean,
    isDraw: Boolean,
    rematchWaiting: Boolean,
    onRequestRematch: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val bannerColor = when {
        isDraw -> LuxurySecondary
        isWinner -> LuxuryTertiary
        else -> SemanticError
    }

    val bannerText = when {
        isDraw -> "BERABERE!"
        isWinner -> "KAZANDIN! 🏆"
        else -> "RAKİP KAZANDI"
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
        border = BorderStroke(2.dp, bannerColor.copy(alpha = 0.8f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("online_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(bannerColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWinner) Icons.Default.MilitaryTech else Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = bannerText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                color = bannerColor
            )

            Text(
                text = if (isWinner) "Tebrikler! Hedefe daha yakın bir toplam elde ettin." else if (isDraw) "İnanılmaz! İki oyuncunun da hedefe farkı birebir eşit." else "Güzel mücadeleydi! Rövanşta tekrar dene.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = LuxuryOutline
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Comparison stats breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.winner == "A") PlayerAColor.copy(alpha = 0.15f) else LuxurySurfaceContainerHigh
                    ),
                    border = BorderStroke(1.dp, if (state.winner == "A") PlayerAColor else LuxuryOutlineVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (state.myRole == "A") "Sen (Oyuncu A)" else "Rakip (Oyuncu A)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PlayerAColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.playerATotal}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = PlayerAColor
                        )
                        Text(
                            text = "Hedefe Fark: ${state.diffA}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuxuryOutline
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.winner == "B") PlayerBColor.copy(alpha = 0.15f) else LuxurySurfaceContainerHigh
                    ),
                    border = BorderStroke(1.dp, if (state.winner == "B") PlayerBColor else LuxuryOutlineVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (state.myRole == "B") "Sen (Oyuncu B)" else "Rakip (Oyuncu B)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PlayerBColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.playerBTotal}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = PlayerBColor
                        )
                        Text(
                            text = "Hedefe Fark: ${state.diffB}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuxuryOutline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            AppButton(
                text = if (rematchWaiting) "Rakibin Yanıtı Bekleniyor..." else "Tekrar Oyna (Rövanş)",
                onClick = onRequestRematch,
                enabled = !rematchWaiting,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("online_rematch_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppButton(
                text = "Ana Menüye Dön",
                onClick = onNavigateBack,
                isPrimary = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("online_return_home_button")
            )
        }
    }
}



