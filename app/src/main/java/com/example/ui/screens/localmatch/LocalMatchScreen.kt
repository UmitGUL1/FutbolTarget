package com.example.ui.screens.localmatch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FootballerRepository
import com.example.data.model.Footballer
import com.example.data.model.GameRuleEngine
import com.example.data.model.MatchResult
import com.example.data.model.PickSlot
import com.example.data.model.Question
import com.example.data.model.RevealStep
import com.example.ui.components.AppButton
import com.example.ui.components.ModeChip
import com.example.ui.components.PitchBackground
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue
import com.example.ui.theme.PlayerAColor
import com.example.ui.theme.PlayerBColor
import com.example.ui.theme.SemanticError
import com.example.ui.theme.SemanticSuccess
import kotlinx.coroutines.delay

enum class MatchPhase {
    QUESTION,
    PICKING,
    REVEAL,
    RESULT
}

@Composable
fun LocalMatchScreen(
    questionId: String,
    onNavigateBack: () -> Unit,
    onSelectOtherQuestion: () -> Unit = onNavigateBack,
    onMatchFinished: (questionTitle: String, myTotal: Int, opponentTotal: Int, target: Int, result: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeQuestionId by remember(questionId) { mutableStateOf(questionId) }
    val question = remember(activeQuestionId) { FootballerRepository.getQuestionById(activeQuestionId) }

    var phase by remember { mutableStateOf(MatchPhase.QUESTION) }
    var startingPlayer by remember { mutableStateOf(if (Math.random() > 0.5) "A" else "B") }
    var currentTurnIndex by remember { mutableIntStateOf(0) }
    val pickOrder = remember(startingPlayer, question) {
        GameRuleEngine.buildPickOrder(startingPlayer, question.pickCount)
    }

    var picks by remember { mutableStateOf<List<PickSlot>>(emptyList()) }

    // Reveal phase state
    var revealIndex by remember { mutableIntStateOf(0) }
    var revealedSteps by remember { mutableStateOf<List<RevealStep>>(emptyList()) }
    var matchResult by remember { mutableStateOf<MatchResult?>(null) }

    val currentPlayer = if (currentTurnIndex < pickOrder.size) pickOrder[currentTurnIndex] else startingPlayer
    val currentPickNumberForPlayer = remember(picks, currentPlayer) {
        picks.count { it.playerId == currentPlayer } + 1
    }

    val onPickFinished: (Footballer?, Boolean) -> Unit = remember(picks, pickOrder, question, currentPlayer, currentPickNumberForPlayer) {
        { footballer, timedOut ->
            val canAccept = timedOut || footballer == null || GameRuleEngine.canAcceptPick(picks, currentPlayer, footballer.id, question.pickCount)
            if (canAccept) {
                val newPick = PickSlot(
                    playerId = currentPlayer,
                    pickNumber = currentPickNumberForPlayer,
                    footballer = footballer,
                    timedOut = timedOut
                )
                val updatedPicks = picks + newPick
                picks = updatedPicks

                if (updatedPicks.size >= pickOrder.size) {
                    // Transition to Reveal phase
                    revealedSteps = GameRuleEngine.buildRevealSteps(updatedPicks, question)
                    revealIndex = 0
                    phase = MatchPhase.REVEAL
                } else {
                    currentTurnIndex += 1
                }
            }
        }
    }

    PitchBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            LocalMatchTopBar(
                title = "Aynı Telefonda",
                onClose = onNavigateBack
            )

            when (phase) {
                MatchPhase.QUESTION -> {
                    QuestionIntroView(
                        question = question,
                        startingPlayer = startingPlayer,
                        onStartPicking = {
                            phase = MatchPhase.PICKING
                        },
                        onSelectOtherQuestion = onSelectOtherQuestion,
                        onRandomizeQuestion = {
                            val nextQ = FootballerRepository.questions.filter { it.id != activeQuestionId }.random()
                            activeQuestionId = nextQ.id
                            startingPlayer = if (Math.random() > 0.5) "A" else "B"
                            picks = emptyList()
                            currentTurnIndex = 0
                        }
                    )
                }

                MatchPhase.PICKING -> {
                    PickingTurnView(
                        question = question,
                        currentPlayer = currentPlayer,
                        currentTurnIndex = currentTurnIndex,
                        pickNumber = currentPickNumberForPlayer,
                        maxPicks = question.pickCount,
                        picks = picks,
                        onTimeout = {
                            onPickFinished(null, true)
                        },
                        onSelectFootballer = { footballer ->
                            onPickFinished(footballer, false)
                        }
                    )
                }

                MatchPhase.REVEAL -> {
                    RevealPhaseView(
                        question = question,
                        steps = revealedSteps,
                        revealIndex = revealIndex,
                        onNextReveal = {
                            if (revealIndex + 1 < revealedSteps.size) {
                                revealIndex += 1
                            } else {
                                // Calculate result
                                val res = GameRuleEngine.calculateResult(question, revealedSteps)
                                val winner = res.winner
                                val totalA = res.totalA
                                val totalB = res.totalB
                                matchResult = res
                                phase = MatchPhase.RESULT

                                // Record match
                                val outcomeForA = if (winner == "A") "win" else if (winner == "B") "loss" else "draw"
                                onMatchFinished(
                                    question.title,
                                    totalA,
                                    totalB,
                                    question.target,
                                    outcomeForA
                                )
                            }
                        }
                    )
                }

                MatchPhase.RESULT -> {
                    matchResult?.let { res ->
                        ResultPhaseView(
                            question = question,
                            result = res,
                            onRematch = {
                                picks = emptyList()
                                currentTurnIndex = 0
                                startingPlayer = if (Math.random() > 0.5) "A" else "B"
                                revealIndex = 0
                                matchResult = null
                                phase = MatchPhase.QUESTION
                            },
                            onSelectOtherQuestion = onSelectOtherQuestion,
                            onHome = onNavigateBack
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocalMatchTopBar(
    title: String,
    onClose: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("match_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun QuestionIntroView(
    question: Question,
    startingPlayer: String,
    onStartPicking: () -> Unit,
    onSelectOtherQuestion: () -> Unit,
    onRandomizeQuestion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("question_intro_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModeChip(text = "${question.competition} • ${question.statType.uppercase()}", isHighlighted = true)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "${question.target}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 76.sp
                    ),
                    color = GoldAccent
                )
                Text(
                    text = "HEDEF DEĞER",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = question.description,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Starting player coin flip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val playerColor = if (startingPlayer == "A") PlayerAColor else PlayerBColor
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(playerColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "İlk Seçim: Oyuncu $startingPlayer",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = playerColor
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                AppButton(
                    text = "Seçimlere Başla",
                    onClick = onStartPicking,
                    modifier = Modifier.testTag("start_picking_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppButton(
                        text = "Kategori / Soru Seç",
                        onClick = onSelectOtherQuestion,
                        isPrimary = false,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("change_question_button")
                    )

                    IconButton(
                        onClick = onRandomizeQuestion,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .size(48.dp)
                            .testTag("random_question_shuffle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Rastgele Soru",
                            tint = GoldAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TurnTimerBadge(
    turnKey: Any,
    initialSeconds: Int,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timerSeconds by remember(turnKey) { mutableIntStateOf(initialSeconds) }
    var isPaused by remember(turnKey) { mutableStateOf(false) }

    LaunchedEffect(turnKey, isPaused) {
        if (!isPaused) {
            while (timerSeconds > 0) {
                delay(1000L)
                timerSeconds -= 1
            }
            onTimeout()
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (timerSeconds <= 5) SemanticError.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (timerSeconds <= 5) SemanticError else GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${timerSeconds}s",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = if (timerSeconds <= 5) SemanticError else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        IconButton(
            onClick = { isPaused = !isPaused },
            modifier = Modifier.testTag("timer_pause_button")
        ) {
            Icon(
                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = "Zamanlayıcıyı Duraklat",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PickingTurnView(
    question: Question,
    currentPlayer: String,
    currentTurnIndex: Int,
    pickNumber: Int,
    maxPicks: Int,
    picks: List<PickSlot>,
    onTimeout: () -> Unit,
    onSelectFootballer: (Footballer) -> Unit
) {
    var rawSearchQuery by remember(currentTurnIndex) { mutableStateOf("") }
    var debouncedQuery by remember(currentTurnIndex) { mutableStateOf("") }

    LaunchedEffect(rawSearchQuery) {
        if (rawSearchQuery.isEmpty()) {
            debouncedQuery = ""
        } else {
            delay(180L)
            debouncedQuery = rawSearchQuery
        }
    }

    val pickedIds = remember(picks) { picks.mapNotNull { it.footballer?.id }.toSet() }
    val searchResults = remember(debouncedQuery, question, pickedIds) {
        FootballerRepository.searchEligibleFootballers(debouncedQuery, question, pickedIds)
    }

    val playerColor = if (currentPlayer == "A") PlayerAColor else PlayerBColor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Turn & Timer Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, playerColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("turn_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(playerColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SIRA: OYUNCU $currentPlayer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = playerColor
                        )
                        Text(
                            text = "Seçim: $pickNumber / $maxPicks • Hedef: ${question.target}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TurnTimerBadge(
                    turnKey = currentTurnIndex,
                    initialSeconds = GameRuleEngine.turnDurationSeconds(currentTurnIndex),
                    onTimeout = onTimeout
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dual Player Pick Overview Slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlayerPickListMini(
                title = "Oyuncu A",
                color = PlayerAColor,
                picks = picks.filter { it.playerId == "A" },
                maxPicks = maxPicks,
                modifier = Modifier.weight(1f)
            )

            PlayerPickListMini(
                title = "Oyuncu B",
                color = PlayerBColor,
                picks = picks.filter { it.playerId == "B" },
                maxPicks = maxPicks,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = rawSearchQuery,
            onValueChange = { rawSearchQuery = it },
            placeholder = { Text("Futbolcu veya kulüp ara...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (rawSearchQuery.isNotEmpty()) {
                    IconButton(onClick = { rawSearchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Temizle")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_player_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Footballer Options List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("eligible_players_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(searchResults, key = { it.id }, contentType = { "footballer" }) { footballer ->
                FootballerSelectableCard(
                    footballer = footballer,
                    onSelect = { onSelectFootballer(footballer) }
                )
            }
        }
    }
}

@Composable
private fun PlayerPickListMini(
    title: String,
    color: Color,
    picks: List<PickSlot>,
    maxPicks: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = color
                )
                Text(
                    text = "${picks.size}/$maxPicks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            for (i in 0 until maxPicks) {
                val pick = picks.getOrNull(i)
                val label = if (pick == null) "-" else if (pick.timedOut) "Süre doldu" else pick.footballer?.name ?: "-"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${i + 1}.",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(16.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (pick != null) FontWeight.Bold else FontWeight.Normal
                        ),
                        maxLines = 1,
                        color = if (pick != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FootballerSelectableCard(
    footballer: Footballer,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("footballer_card_${footballer.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = footballer.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = NavySecondaryBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = footballer.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${footballer.nationality} • ${footballer.position} • ${footballer.clubName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavyPrimaryBlue.copy(alpha = 0.16f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Seç",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = NavySecondaryBlue
                )
            }
        }
    }
}

@Composable
private fun RevealPhaseView(
    question: Question,
    steps: List<RevealStep>,
    revealIndex: Int,
    onNextReveal: () -> Unit
) {
    val visibleSteps = remember(steps, revealIndex) {
        steps.take(revealIndex + 1)
    }

    val totalA = GameRuleEngine.calculateTotal(visibleSteps, GameRuleEngine.PLAYER_A)
    val totalB = GameRuleEngine.calculateTotal(visibleSteps, GameRuleEngine.PLAYER_B)
    val isLast = revealIndex >= steps.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "KART AÇILIŞ AŞAMASI",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Hedef: ${question.target} ${question.statType.uppercase()}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = GoldAccent
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Scores Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PlayerAColor.copy(alpha = 0.6f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Oyuncu A", style = MaterialTheme.typography.labelSmall, color = PlayerAColor)
                    Text(
                        text = "$totalA",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontSize = 32.sp),
                        color = PlayerAColor
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PlayerBColor.copy(alpha = 0.6f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Oyuncu B", style = MaterialTheme.typography.labelSmall, color = PlayerBColor)
                    Text(
                        text = "$totalB",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontSize = 32.sp),
                        color = PlayerBColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Revealed Cards Timeline
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(visibleSteps) { step ->
                val pColor = if (step.playerId == "A") PlayerAColor else PlayerBColor
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, pColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(pColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = step.playerId,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = pColor
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = step.footballer?.name ?: "Süre Doldu",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Seçim ${step.pickNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "+${step.statValue}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = GoldAccent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        AppButton(
            text = if (isLast) "Sonuçları Gör" else "Sıradaki Kartı Aç (${revealIndex + 1}/${steps.size})",
            onClick = onNextReveal,
            modifier = Modifier.testTag("next_reveal_button")
        )
    }
}

@Composable
private fun ResultPhaseView(
    question: Question,
    result: MatchResult,
    onRematch: () -> Unit,
    onSelectOtherQuestion: () -> Unit,
    onHome: () -> Unit
) {
    val winnerText = when (result.winner) {
        "A" -> "OYUNCU A KAZANDI! 🏆"
        "B" -> "OYUNCU B KAZANDI! 🏆"
        else -> "BERABERE! 🤝"
    }

    val winnerColor = when (result.winner) {
        "A" -> PlayerAColor
        "B" -> PlayerBColor
        else -> GoldAccent
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, winnerColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("match_result_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = winnerText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    ),
                    color = winnerColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hedef: ${result.target}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Breakdown cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Oyuncu A", style = MaterialTheme.typography.labelSmall, color = PlayerAColor)
                            Text(
                                text = "${result.totalA}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = PlayerAColor
                            )
                            Text(
                                text = "Hedefe Fark: ${result.diffA}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Oyuncu B", style = MaterialTheme.typography.labelSmall, color = PlayerBColor)
                            Text(
                                text = "${result.totalB}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = PlayerBColor
                            )
                            Text(
                                text = "Hedefe Fark: ${result.diffB}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                AppButton(
                    text = "Tekrar Oyna",
                    onClick = onRematch,
                    modifier = Modifier.testTag("rematch_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                AppButton(
                    text = "Farklı Kategori / Soru Seç",
                    onClick = onSelectOtherQuestion,
                    isPrimary = false,
                    modifier = Modifier.testTag("change_question_after_match_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                AppButton(
                    text = "Ana Menüye Dön",
                    onClick = onHome,
                    isPrimary = false,
                    modifier = Modifier.testTag("return_home_button")
                )
            }
        }
    }
}



