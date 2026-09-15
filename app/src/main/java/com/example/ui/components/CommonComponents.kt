package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyPrimaryBlue
import com.example.ui.theme.NavySecondaryBlue

@Composable
fun PitchBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val lineColor = if (isDark) Color(0x12FFFFFF) else Color(0x18000000)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Center circle
            drawCircle(
                color = lineColor,
                radius = width * 0.35f,
                center = Offset(width * 0.5f, height * 0.45f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Center spot
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = Offset(width * 0.5f, height * 0.45f)
            )

            // Midfield line
            drawLine(
                color = lineColor,
                start = Offset(0f, height * 0.45f),
                end = Offset(width, height * 0.45f),
                strokeWidth = 2.dp.toPx()
            )

            // Penalty box top
            drawRect(
                color = lineColor,
                topLeft = Offset(width * 0.2f, 0f),
                size = androidx.compose.ui.geometry.Size(width * 0.6f, height * 0.14f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Penalty box bottom
            drawRect(
                color = lineColor,
                topLeft = Offset(width * 0.2f, height * 0.86f),
                size = androidx.compose.ui.geometry.Size(width * 0.6f, height * 0.14f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
        content()
    }
}

@Composable
fun FutbolTargetTopBar(
    title: String = "FUTBOL TARGET",
    subtitle: String? = "Competitive Trivia",
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavyPrimaryBlue.copy(alpha = 0.16f))
                        .border(1.dp, NavyPrimaryBlue.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = NavySecondaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                trailingContent?.invoke()

                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                        contentDescription = "Tema Değiştir",
                        tint = GoldAccent
                    )
                }
            }
        }
    }
}

@Composable
fun RankBadge(
    rank: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (rank) {
        "Legend" -> Color(0xFF7C3AED).copy(alpha = 0.2f) to Color(0xFFA78BFA)
        "Elite" -> Color(0xFFDC2626).copy(alpha = 0.2f) to Color(0xFFF87171)
        "Diamond" -> Color(0xFF0284C7).copy(alpha = 0.2f) to Color(0xFF38BDF8)
        "Platinum" -> Color(0xFF0D9488).copy(alpha = 0.2f) to Color(0xFF2DD4BF)
        "Gold" -> GoldAccent.copy(alpha = 0.2f) to GoldAccent
        "Silver" -> Color(0xFF94A3B8).copy(alpha = 0.2f) to Color(0xFFCBD5E1)
        else -> Color(0xFFB45309).copy(alpha = 0.2f) to Color(0xFFD97706)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun ModeChip(
    text: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false
) {
    val bg = if (isHighlighted) NavyPrimaryBlue.copy(alpha = 0.18f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)
    val border = if (isHighlighted) NavyPrimaryBlue.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val color = if (isHighlighted) NavySecondaryBlue else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, border, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth()
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth()
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun FootballerPhotoPlaceholder(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "F"
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(NavyPrimaryBlue.copy(alpha = 0.18f))
            .border(1.dp, NavySecondaryBlue.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = NavySecondaryBlue
        )
    }
}
