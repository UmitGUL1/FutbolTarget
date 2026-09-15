package com.example.ui.screens.database

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FootballerRepository
import com.example.data.model.Footballer
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue
import kotlinx.coroutines.delay

@Composable
fun FootballerListScreen(
    modifier: Modifier = Modifier
) {
    var rawSearchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf("Tümü") }
    val positions = listOf("Tümü", "Forvet", "Kanat", "Orta Saha", "Kaleci")

    LaunchedEffect(rawSearchQuery) {
        if (rawSearchQuery.isEmpty()) {
            debouncedQuery = ""
        } else {
            delay(180L)
            debouncedQuery = rawSearchQuery
        }
    }

    val filteredList = remember(debouncedQuery, selectedPosition) {
        val query = debouncedQuery.trim()
        FootballerRepository.footballers.filter { player ->
            val matchesPos = selectedPosition == "Tümü" || player.position.contains(selectedPosition, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                    player.name.contains(query, ignoreCase = true) ||
                    player.nationality.contains(query, ignoreCase = true) ||
                    player.clubName.contains(query, ignoreCase = true)
            matchesPos && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = rawSearchQuery,
            onValueChange = { rawSearchQuery = it },
            placeholder = { Text("Efsane ara (ör: Henry, Ronaldo, Cech)...") },
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
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("database_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Position Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(positions) { pos ->
                val isSelected = pos == selectedPosition
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) NavyPrimaryBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isSelected) NavyPrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedPosition = pos }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pos,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                        ),
                        color = if (isSelected) NavySecondaryBlue else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Footballers Count Header
        Text(
            text = "${filteredList.size} FUTBOLCU",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
        )

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("database_footballers_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList, key = { it.id }, contentType = { "footballer_detail" }) { footballer ->
                FootballerDetailCard(footballer = footballer)
            }
        }
    }
}

@Composable
private fun FootballerDetailCard(footballer: Footballer) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NavyPrimaryBlue.copy(alpha = 0.16f))
                            .border(1.dp, NavySecondaryBlue.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = footballer.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
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
                            text = "${footballer.nationality} • ${footballer.position}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = footballer.clubName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "RESMİ İSTATİSTİKLER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    footballer.stats.forEach { stat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${stat.competition} (${stat.statType})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${stat.value}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                color = GoldAccent
                            )
                        }
                    }
                }
            }
        }
    }
}
