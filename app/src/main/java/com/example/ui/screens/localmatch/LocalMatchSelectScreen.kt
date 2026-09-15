package com.example.ui.screens.localmatch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FootballerRepository
import com.example.data.model.Question
import com.example.ui.components.ModeChip
import com.example.ui.components.PitchBackground
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue
import com.example.ui.theme.PlayerAColor
import com.example.ui.theme.PlayerBColor

@Composable
fun LocalMatchSelectScreen(
    onSelectQuestion: (questionId: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
            "Tümü",
            "Kariyer",
            "5 Büyük Lig",
            "Premier League",
            "La Liga",
            "Serie A",
            "Bundesliga",
            "Ligue 1",
            "Champions League",
            "International"
        )
    }

    val statFilters = remember {
        listOf(
            "Tümü" to "Tüm İstatistikler",
            "goals" to "Goller",
            "appearances" to "Maç Sayısı",
            "assists" to "Asistler",
            "trophies" to "Kupalar",
            "cleanSheets" to "Gol Yememe"
        )
    }

    val difficultyFilters = remember {
        listOf("Tümü", "Kolay", "Orta", "Zor", "Efsane", "Hızlı")
    }

    var selectedCategory by remember { mutableStateOf("Tümü") }
    var selectedStatType by remember { mutableStateOf("Tümü") }
    var selectedDifficulty by remember { mutableStateOf("Tümü") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredQuestions = remember(selectedCategory, selectedStatType, selectedDifficulty, searchQuery) {
        val query = searchQuery.trim().lowercase()
        FootballerRepository.questions.filter { q ->
            val matchesCat = if (selectedCategory == "Tümü") true else q.competition.equals(selectedCategory, ignoreCase = true)
            val matchesStat = if (selectedStatType == "Tümü") true else q.statType.equals(selectedStatType, ignoreCase = true)
            val matchesDiff = if (selectedDifficulty == "Tümü") true else q.difficulty.equals(selectedDifficulty, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                q.title.lowercase().contains(query) ||
                q.description.lowercase().contains(query) ||
                q.target.toString().contains(query) ||
                q.competition.lowercase().contains(query)
            }
            matchesCat && matchesStat && matchesDiff && matchesQuery
        }
    }

    PitchBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
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
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("local_select_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri Dön",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Aynı Telefonda Maç",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kategori ve Hedef Soru Seçimi",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Random Quick Match Button
                    IconButton(
                        onClick = {
                            val pool = if (filteredQuestions.isNotEmpty()) filteredQuestions else FootballerRepository.questions
                            val randomQ = pool.random()
                            onSelectQuestion(randomQ.id)
                        },
                        modifier = Modifier.testTag("random_match_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Rastgele Soru",
                            tint = GoldAccent
                        )
                    }
                }
            }

            // Quick Info & 2-Player Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimaryBlue.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Player A dot
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(PlayerAColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Oyuncu A",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PlayerAColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "vs",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Player B dot
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(PlayerBColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Oyuncu B",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PlayerBColor
                            )
                        }

                        // Quick Random Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable {
                                    val pool = if (filteredQuestions.isNotEmpty()) filteredQuestions else FootballerRepository.questions
                                    onSelectQuestion(pool.random().id)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Casino,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Rastgele Başla",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }
            }

            // Search Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("local_select_search_input"),
                    placeholder = {
                        Text(
                            "200 Soru İçinde Ara (örn: 1000, Gol, Kariyer...)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Ara",
                            tint = NavySecondaryBlue
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Temizle",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Category Tabs Row (10 Categories with Counter)
            Text(
                text = "KATEGORİLER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories, key = { it }) { category ->
                    val isSelected = category == selectedCategory
                    val categoryCount = remember(category) {
                        if (category == "Tümü") FootballerRepository.questions.size
                        else FootballerRepository.questions.count { it.competition.equals(category, ignoreCase = true) }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) NavyPrimaryBlue.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) NavyPrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("category_chip_$category")
                    ) {
                        Text(
                            text = "$category ($categoryCount)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                            ),
                            color = if (isSelected) NavySecondaryBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Stat Type Filter Sub-row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(statFilters, key = { it.first }) { (statKey, statTitle) ->
                    val isSelected = statKey == selectedStatType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) GoldAccent.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedStatType = statKey }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = statTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Count summary header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredQuestions.size} Hedef Soru Hazır",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Aynı Telefonda Oynamak İçin Seçin",
                    style = MaterialTheme.typography.labelSmall,
                    color = NavySecondaryBlue
                )
            }

            // Questions List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("local_match_questions_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredQuestions, key = { it.id }) { question ->
                    LocalMatchQuestionCard(
                        question = question,
                        onPlay = { onSelectQuestion(question.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalMatchQuestionCard(
    question: Question,
    onPlay: () -> Unit
) {
    val difficultyColor = when (question.difficulty) {
        "Kolay" -> Color(0xFF4ADE80)
        "Orta" -> Color(0xFF38BDF8)
        "Zor" -> Color(0xFFFB923C)
        "Efsane" -> Color(0xFFA855F7)
        "Hızlı" -> Color(0xFFFACC15)
        else -> MaterialTheme.colorScheme.primary
    }

    val statLabel = when (question.statType) {
        "goals" -> "Gol"
        "appearances" -> "Maç"
        "assists" -> "Asist"
        "trophies" -> "Kupa"
        "cleanSheets" -> "Golsüz Maç"
        else -> question.statType
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("local_question_card_${question.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ModeChip(text = question.competition, isHighlighted = false)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(difficultyColor.copy(alpha = 0.15f))
                            .border(1.dp, difficultyColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = question.difficulty,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = difficultyColor
                        )
                    }
                }

                // Target badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldAccent.copy(alpha = 0.15f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Hedef: ${question.target} $statLabel",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = question.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${question.pickCount} Oyuncu Seçimi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Play Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyPrimaryBlue)
                        .clickable { onPlay() }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("play_local_question_${question.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aynı Telefonda Oyna",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
