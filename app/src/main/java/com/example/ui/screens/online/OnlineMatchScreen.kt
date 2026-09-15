package com.example.ui.screens.online

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.supabase.MatchRuleEngine
import com.example.data.supabase.OnlineMatchRepository
import com.example.data.supabase.OnlineMatchState
import com.example.data.supabase.OnlinePick
import com.example.ui.components.AppButton
import com.example.ui.components.FootballerPhotoPlaceholder
import com.example.ui.components.ModeChip
import com.example.ui.components.PitchBackground
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PlayerAColor
import com.example.ui.theme.PlayerBColor
import com.example.ui.theme.SemanticError
import com.example.ui.theme.SemanticSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun OnlineMatchScreen(
    matchId: String,
    roomCode: String,
    role: String, // "A" or "B"
    repository: OnlineMatchRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val matchState by repository.currentMatchState.collectAsState()

    // Realtime connection lifecycle
    DisposableEffect(matchId) {
        repository.startListeningToMatch(matchId, "")
        onDispose {
            repository.stopListening()
        }
    }

    // Local server-adjusted countdown timer
    var remainingSeconds by remember { mutableIntStateOf(15) }
    LaunchedEffect(matchState?.turnDeadlineAtEpochMs, matchState?.status) {
        val deadline = matchState?.turnDeadlineAtEpochMs ?: return@LaunchedEffect
        if (matchState?.status != "picking") return@LaunchedEffect

        while (true) {
            val now = System.currentTimeMillis()
            val diffMs = deadline - now
            val secs = max(0, (diffMs / 1000).toInt())
            remainingSeconds = secs

            if (secs <= 0) {
                // Trigger authoritative timeout
                repository.handleTurnTimeout(matchId)
                break
            }
            delay(500L)
        }
    }

    // Footballer search state
    var searchQuery by remember { mutableStateOf("") }
    var isSubmittingPick by remember { mutableStateOf(false) }
    var rematchWaiting by remember { mutableStateOf(false) }

    PitchBackground(modifier = modifier) {
        val state = matchState

        if (state == null) {
            // Loading match state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GoldAccent)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Maç verileri yükleniyor...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@PitchBackground
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("online_match_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ODA: ${state.roomCode.ifBlank { roomCode }}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        ),
                        color = GoldAccent
                    )
                    Text(
                        text = "Hedef: ${state.question.target} ${state.question.statType.uppercase()}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Role badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (state.myRole == "A") PlayerAColor.copy(alpha = 0.2f) else PlayerBColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SEN: OYUNCU ${state.myRole}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = if (state.myRole == "A") PlayerAColor else PlayerBColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (state.status) {
                "picking" -> {
                    OnlinePickingView(
                        state = state,
                        remainingSeconds = remainingSeconds,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        isSubmitting = isSubmittingPick,
                        onSelectFootballer = { footballer ->
                            scope.launch {
                                isSubmittingPick = true
                                val result = repository.submitPick(state.matchId, footballer.id)
                                isSubmittingPick = false
                                if (result.isFailure) {
                                    Toast.makeText(context, result.exceptionOrNull()?.message ?: "Seçim başarısız", Toast.LENGTH_SHORT).show()
                                } else {
                                    searchQuery = ""
                                }
                            }
                        }
                    )
                }
                "reveal", "finished" -> {
                    SuspensefulResultVerificationView(
                        state = state,
                        rematchWaiting = rematchWaiting,
                        onRequestRematch = {
                            scope.launch {
                                rematchWaiting = true
                                val res = repository.requestRematch(state.matchId)
                                if (res.isSuccess && res.getOrThrow()) {
                                    rematchWaiting = false
                                }
                            }
                        },
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.OnlinePickingView(
    state: OnlineMatchState,
    remainingSeconds: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSubmitting: Boolean,
    onSelectFootballer: (Footballer) -> Unit
) {
    val isMyTurn = state.isMyTurn
    val turnColor by animateColorAsState(
        targetValue = if (isMyTurn) SemanticSuccess else GoldAccent,
        animationSpec = tween(400),
        label = "turn_color"
    )

    // Turn countdown header
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, turnColor.copy(alpha = 0.5f)),
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
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(turnColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMyTurn) "SENİN SIRAN (Seçim ${state.myPicks.size + 1}/5)" else "RAKİBİN SIRASI...",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = turnColor
                )
            }

            // Countdown timer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (remainingSeconds <= 5) SemanticError else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${remainingSeconds}s",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (remainingSeconds <= 5) SemanticError else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Picks slots summary (Sen vs Rakip)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // My Picks column
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Sen (${state.myPicks.size}/5)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = if (state.myRole == "A") PlayerAColor else PlayerBColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                PickSlotsMiniList(picks = state.myPicks)
            }
        }

        // Opponent Picks column
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Rakip (${state.opponentPicks.size}/5)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = if (state.myRole == "A") PlayerBColor else PlayerAColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                PickSlotsMiniList(picks = state.opponentPicks)
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (isMyTurn) {
        // Footballer search & select section
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Futbolcu ara (İsim, kulüp, mevki)...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("online_footballer_search_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Footballers list
        val pickedIds = remember(state.picks) { state.picks.mapNotNull { it.footballerId }.toSet() }
        val eligibleFootballers = remember(searchQuery, state.question, pickedIds) {
            FootballerRepository.footballers.filter { f ->
                val matchesQuery = searchQuery.isBlank() ||
                        f.name.contains(searchQuery, ignoreCase = true) ||
                        f.clubName.contains(searchQuery, ignoreCase = true) ||
                        f.position.contains(searchQuery, ignoreCase = true)
                matchesQuery
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(eligibleFootballers, key = { it.id }) { footballer ->
                val isAlreadyPicked = footballer.id in pickedIds

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAlreadyPicked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        if (isAlreadyPicked) Color.Transparent else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAlreadyPicked && !isSubmitting) {
                            onSelectFootballer(footballer)
                        }
                        .testTag("footballer_item_${footballer.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FootballerPhotoPlaceholder(name = footballer.name, size = 40.dp)
                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = footballer.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isAlreadyPicked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${footballer.nationality} • ${footballer.position} • ${footballer.clubName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isAlreadyPicked) {
                            Text(
                                text = "SEÇİLDİ",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            // STAT PRIVACY: Hidden stat badge during picking
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Gizli Değer",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Gizli",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Opponent's turn placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = GoldAccent,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Rakip seçimini yapıyor...",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Seçim anında ekranında belirecektir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PickSlotsMiniList(picks: List<OnlinePick>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until 5) {
            val pick = picks.getOrNull(i)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${i + 1}.",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (pick != null) {
                    if (pick.isTimeout) {
                        Text(
                            text = "Zaman Aşımı (0)",
                            style = MaterialTheme.typography.bodySmall,
                            color = SemanticError,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        val footballer = FootballerRepository.footballers.firstOrNull { it.id == pick.footballerId }
                        Text(
                            text = footballer?.name ?: "Futbolcu",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Stat value: Lock icon during picking!
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Gizli",
                        tint = GoldAccent,
                        modifier = Modifier.size(11.dp)
                    )
                } else {
                    Text(
                        text = "Bekleniyor...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(11.dp))
                }
            }
        }
    }
}

