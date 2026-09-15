package com.example.ui.screens.solo

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.Question
import com.example.ui.components.AppButton
import com.example.ui.components.ModeChip
import com.example.ui.components.PitchBackground
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.SemanticError
import com.example.ui.theme.SemanticSuccess
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max

@Composable
fun SoloChallengeScreen(
    questionId: String,
    onNavigateBack: () -> Unit,
    onChallengeFinished: (questionTitle: String, total: Int, target: Int, outcome: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = remember(questionId) { FootballerRepository.getQuestionById(questionId) }

    var selectedFootballers by remember { mutableStateOf<List<Footballer>>(emptyList()) }
    var rawSearchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(rawSearchQuery) {
        if (rawSearchQuery.isEmpty()) {
            debouncedQuery = ""
        } else {
            delay(180L)
            debouncedQuery = rawSearchQuery
        }
    }

    val pickedIds = remember(selectedFootballers) { selectedFootballers.map { it.id }.toSet() }
    val eligibleFootballers = remember(debouncedQuery, question, pickedIds) {
        FootballerRepository.searchEligibleFootballers(debouncedQuery, question, pickedIds)
    }

    val totalScore = remember(selectedFootballers, question) {
        selectedFootballers.sumOf { it.getStatValue(question.competition, question.statType) }
    }
    val diff = remember(totalScore, question.target) { abs(question.target - totalScore) }
    val accuracy = remember(diff, question.target) {
        max(0, 100 - (diff * 100) / question.target)
    }

    val grade = remember(diff) {
        when {
            diff == 0 -> "S+ (Tam İsabet!)"
            diff <= 15 -> "S (Kusursuz)"
            diff <= 40 -> "A (Harika)"
            diff <= 80 -> "B (İyi)"
            else -> "C (Geliştirilebilir)"
        }
    }

    PitchBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("solo_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solo Hedef",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (!isSubmitted) {
                // Picking phase
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Target Overview Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = question.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${selectedFootballers.size}/${question.pickCount} Oyuncu Seçildi",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${question.target}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 28.sp
                                    ),
                                    color = GoldAccent
                                )
                                Text(
                                    text = "HEDEF",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selected Squad Chips
                    if (selectedFootballers.isNotEmpty()) {
                        Text(
                            text = "SEÇİLEN KADRO",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            selectedFootballers.forEach { player ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.name.split(" ").lastOrNull() ?: player.name,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clickable {
                                                    selectedFootballers = selectedFootballers.filter { it.id != player.id }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Search input
                    OutlinedTextField(
                        value = rawSearchQuery,
                        onValueChange = { rawSearchQuery = it },
                        placeholder = { Text("Oyuncu veya kulüp ara...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
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
                            .testTag("solo_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(eligibleFootballers, key = { it.id }, contentType = { "footballer" }) { footballer ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (selectedFootballers.size < question.pickCount) {
                                            selectedFootballers = selectedFootballers + footballer
                                            rawSearchQuery = ""
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = footballer.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${footballer.nationality} • ${footballer.clubName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    ModeChip(text = "+ Ekle", isHighlighted = true)
                                }
                            }
                        }
                    }

                    AppButton(
                        text = "Hedefe Ulaş (${selectedFootballers.size}/${question.pickCount})",
                        enabled = selectedFootballers.size == question.pickCount,
                        onClick = {
                            isSubmitted = true
                            val outcome = if (diff <= 40) "win" else "loss"
                            onChallengeFinished(question.title, totalScore, question.target, outcome)
                        },
                        modifier = Modifier.testTag("solo_submit_button")
                    )
                }
            } else {
                // Result screen
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
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
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

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = grade,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                ),
                                color = GoldAccent
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "%$accuracy İsabet Oranı",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = SemanticSuccess
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Hedef", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "${question.target}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Toplamın", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "$totalScore",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = GoldAccent
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Fark", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "$diff",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = if (diff <= 25) SemanticSuccess else SemanticError
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Breakdown of selected players and their values
                            selectedFootballers.forEach { player ->
                                val statVal = player.getStatValue(question.competition, question.statType)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = player.name,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "+$statVal",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                        color = GoldAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            AppButton(
                                text = "Yeniden Dene",
                                onClick = {
                                    selectedFootballers = emptyList()
                                    isSubmitted = false
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            AppButton(
                                text = "Ana Menüye Dön",
                                onClick = onNavigateBack,
                                isPrimary = false
                            )
                        }
                    }
                }
            }
        }
    }
}
