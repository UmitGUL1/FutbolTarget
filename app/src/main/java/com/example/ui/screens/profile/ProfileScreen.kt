package com.example.ui.screens.profile

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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.AppThemeMode
import com.example.data.model.MatchHistoryItem
import com.example.data.model.Profile
import com.example.ui.components.RankBadge
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue
import com.example.ui.theme.SemanticError
import com.example.ui.theme.SemanticSuccess

@Composable
fun ProfileScreen(
    profile: Profile,
    matchHistory: List<MatchHistoryItem>,
    currentThemeMode: AppThemeMode,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onUpdateUsername: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(profile.username) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Identity Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(NavyPrimaryBlue.copy(alpha = 0.16f))
                                .border(2.dp, NavySecondaryBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = NavySecondaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.username,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Düzenle",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            editedName = profile.username
                                            showEditDialog = true
                                        }
                                        .testTag("edit_username_button")
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hedef Ustası Oyuncu",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    RankBadge(rank = profile.rank)
                }
            }
        }

        // Stats Overview Grid
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "KARİYER İSTATİSTİKLERİ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatBox(title = "Toplam Maç", value = "${profile.totalMatches}")
                        StatBox(title = "Galibiyet", value = "${profile.wins}", color = SemanticSuccess)
                        StatBox(title = "Mağlubiyet", value = "${profile.losses}", color = SemanticError)
                        StatBox(title = "Berabere", value = "${profile.draws}", color = GoldAccent)
                        StatBox(title = "Kazanma %", value = "%${profile.winRate}", color = NavySecondaryBlue)
                    }
                }
            }
        }

        // Theme / Appearance Card (Koyu Mod / Açık Mod / Sistem)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TEMA & GÖRÜNÜM",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionButton(
                            title = "Koyu Mod",
                            icon = Icons.Default.Brightness4,
                            isSelected = currentThemeMode == AppThemeMode.DARK,
                            onClick = { onSetThemeMode(AppThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionButton(
                            title = "Açık Mod",
                            icon = Icons.Default.Brightness7,
                            isSelected = currentThemeMode == AppThemeMode.LIGHT,
                            onClick = { onSetThemeMode(AppThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionButton(
                            title = "Sistem",
                            icon = Icons.Default.SettingsBrightness,
                            isSelected = currentThemeMode == AppThemeMode.SYSTEM,
                            onClick = { onSetThemeMode(AppThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section Title: Match History
        item {
            Text(
                text = "MAÇ GEÇMİŞİ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (matchHistory.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Henüz tamamlanan bir maç bulunmuyor. Bir maç başlat ve hedefe en yakın kadroyu kur!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(matchHistory, key = { it.id }, contentType = { "match_history_item" }) { item ->
                MatchHistoryCard(item = item)
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Kullanıcı Adı Değiştir") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    singleLine = true,
                    label = { Text("Kullanıcı Adı") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            onUpdateUsername(editedName.trim())
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThemeOptionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) NavyPrimaryBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
    val border = if (isSelected) NavyPrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val color = if (isSelected) NavySecondaryBlue else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

@Composable
private fun MatchHistoryCard(item: MatchHistoryItem) {
    val (resultText, resultColor) = when (item.result) {
        "win" -> "Kazandın" to SemanticSuccess
        "loss" -> "Kaybettin" to SemanticError
        else -> "Berabere" to GoldAccent
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.questionTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "vs ${item.opponent} • Skor: ${item.myTotal} - ${item.opponentTotal} (Hedef: ${item.target})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(resultColor.copy(alpha = 0.15f))
                    .border(1.dp, resultColor.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = resultColor
                )
            }
        }
    }
}
