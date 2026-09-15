package com.example.ui.screens.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FootballerRepository
import com.example.data.model.Profile
import com.example.data.model.Question
import com.example.ui.components.ModeChip
import com.example.ui.components.RankBadge
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue

@Composable
fun HomeScreen(
    profile: Profile,
    onStartLocalMatch: (questionId: String) -> Unit,
    onStartSoloMatch: (questionId: String) -> Unit,
    onNavigateToLocalMatchSelect: () -> Unit = { onStartLocalMatch(FootballerRepository.questions.first().id) },
    onNavigateToOnlineLobby: () -> Unit = {},
    onNavigateToChallenges: () -> Unit,
    onNavigateToDatabase: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredQuestion = FootballerRepository.questions.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Quick Player Bar
        item {
            QuickPlayerBar(
                profile = profile,
                onProfileClick = onNavigateToProfile
            )
        }

        // Featured Challenge Hero Card
        item {
            FeaturedChallengeCard(
                question = featuredQuestion,
                onPlayLocal = { onStartLocalMatch(featuredQuestion.id) },
                onPlaySolo = { onStartSoloMatch(featuredQuestion.id) }
            )
        }

        // Ranked Status Card
        item {
            RankedStatusCard(
                profile = profile,
                onPlayRanked = onNavigateToLocalMatchSelect
            )
        }

        // Section Title: Oyun Modları
        item {
            Text(
                text = "OYUN MODLARI",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Game Modes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameModeCard(
                    title = "Arkadaşla Oyna",
                    subtitle = "Oda Koduyla Online Maç",
                    icon = Icons.Default.Wifi,
                    tag = "online_match_mode_card",
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToOnlineLobby
                )

                GameModeCard(
                    title = "Aynı Telefonda",
                    subtitle = "2 Oyuncu • Kategori & Soru",
                    icon = Icons.Default.PhoneAndroid,
                    tag = "local_match_mode_card",
                    accentColor = NavyPrimaryBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLocalMatchSelect
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameModeCard(
                    title = "Solo Hedef",
                    subtitle = "Hedefe Tam İsabet Et",
                    icon = Icons.Default.FlashOn,
                    tag = "solo_match_mode_card",
                    accentColor = GoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onStartSoloMatch(featuredQuestion.id) }
                )

                GameModeCard(
                    title = "Meydan Okumalar",
                    subtitle = "${FootballerRepository.questions.size} Farklı Hedef Görev",
                    icon = Icons.Default.EmojiEvents,
                    tag = "challenges_mode_card",
                    accentColor = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToChallenges
                )
            }
        }

        item {
            GameModeCard(
                title = "Oyuncu Havuzu",
                subtitle = "${FootballerRepository.footballers.size}+ Efsane Futbolcu İstatistiği",
                icon = Icons.Default.Group,
                tag = "database_mode_card",
                accentColor = Color(0xFFA855F7),
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToDatabase
            )
        }
    }
}

@Composable
private fun QuickPlayerBar(
    profile: Profile,
    onProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() }
            .testTag("quick_profile_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.5.dp, NavySecondaryBlue.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NavySecondaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = profile.username,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${profile.wins}G • ${profile.losses}M (${profile.winRate}% Kazanma)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            RankBadge(rank = profile.rank)
        }
    }
}

@Composable
private fun FeaturedChallengeCard(
    question: Question,
    onPlayLocal: () -> Unit,
    onPlaySolo: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("featured_challenge_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${question.competition} • ${question.statType.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ModeChip(text = "ÖNE ÇIKAN", isHighlighted = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Target Visual
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${question.target}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 68.sp,
                        letterSpacing = (-1).sp
                    ),
                    color = GoldAccent
                )
                Text(
                    text = "HEDEF",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${question.pickCount} futbolcuyla mümkün olduğunca 500 hedefine yaklaş",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tags row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModeChip(text = "${question.pickCount} Seçim")
                ModeChip(text = question.competition)
                ModeChip(text = question.difficulty)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Play Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPlayLocal() }
                        .testTag("featured_play_local_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2 Kişi Oyna",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onPlaySolo() }
                        .testTag("featured_play_solo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Solo Oyna",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankedStatusCard(
    profile: Profile,
    onPlayRanked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GoldAccent.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ranked_status_card")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "DERECELİ LİG",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile.rank} Ligi",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                    }
                }

                ModeChip(text = "${profile.rankPoints} / 5 PUAN", isHighlighted = true)
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { profile.rankPoints / 5f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = GoldAccent,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sonraki aşama için ${5 - profile.rankPoints} galibiyet",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPlayRanked() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("ranked_matchmake_button")
                ) {
                    Text(
                        text = "Karşılaş",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = NavySecondaryBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = NavySecondaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
