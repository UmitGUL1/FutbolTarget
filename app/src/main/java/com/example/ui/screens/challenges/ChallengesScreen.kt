package com.example.ui.screens.challenges

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue

import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun ChallengesScreen(
    onStartLocalMatch: (questionId: String) -> Unit,
    onStartSoloMatch: (questionId: String) -> Unit,
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
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredQuestions = remember(selectedCategory, searchQuery) {
        val query = searchQuery.trim().lowercase()
        FootballerRepository.questions.filter { q ->
            val matchesCat = if (selectedCategory == "Tümü") true else q.competition.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                q.title.lowercase().contains(query) ||
                q.description.lowercase().contains(query) ||
                q.target.toString().contains(query) ||
                q.competition.lowercase().contains(query)
            }
            matchesCat && matchesQuery
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("challenges_search_input"),
                placeholder = {
                    Text(
                        "200 Hedef Görev İçinde Ara (örn: 1000, Gol, Kariyer...)",
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

        // Categories Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
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
                            if (isSelected) NavyPrimaryBlue.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            1.dp,
                            if (isSelected) NavyPrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
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

        // Question count info banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredQuestions.size} Hedef Soru Listeleniyor",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Toplam 200 Soru Havuzu",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldAccent
            )
        }

        // Questions List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("challenges_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredQuestions, key = { it.id }, contentType = { "challenge_card" }) { question ->
                ChallengeCard(
                    question = question,
                    onPlayLocal = { onStartLocalMatch(question.id) },
                    onPlaySolo = { onStartSoloMatch(question.id) }
                )
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    question: Question,
    onPlayLocal: () -> Unit,
    onPlaySolo: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModeChip(text = question.competition)
                ModeChip(text = question.difficulty, isHighlighted = question.difficulty == "Zor")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = question.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${question.target}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        color = GoldAccent
                    )
                    Text(
                        text = "HEDEF",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPlayLocal() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2 Kişi Oyna",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { onPlaySolo() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Solo Hedef",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
