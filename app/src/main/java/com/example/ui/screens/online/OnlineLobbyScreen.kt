package com.example.ui.screens.online

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsScore
import com.example.ui.theme.LuxuryError
import com.example.ui.theme.LuxuryOnSurface
import com.example.ui.theme.LuxuryOnSurfaceVariant
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
import com.example.data.supabase.OnlineMatchRepository
import com.example.data.supabase.OnlineRoom
import com.example.data.supabase.RoomCodeGenerator
import com.example.data.supabase.SupabaseConfig
import com.example.ui.components.AppButton
import com.example.ui.components.PitchBackground
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SemanticSuccess
import kotlinx.coroutines.launch

@Composable
fun OnlineLobbyScreen(
    repository: OnlineMatchRepository,
    onNavigateBack: () -> Unit,
    onEnterMatch: (matchId: String, roomCode: String, role: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var roomCodeInput by remember { mutableStateOf("") }
    var createdRoom by remember { mutableStateOf<OnlineRoom?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfigDialog by remember { mutableStateOf(false) }

    // Pulsing animation for waiting state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Listen for guest when room is created
    LaunchedEffect(createdRoom) {
        val room = createdRoom ?: return@LaunchedEffect
        if (room.id.isBlank() || room.roomCode.isBlank()) {
            Log.w("[ROOM]", "LaunchedEffect ignored room with blank id or roomCode")
            return@LaunchedEffect
        }
        Log.d("[ROOM]", "navigating waiting room / waiting for guest: roomId=${room.id}, roomCode=${room.roomCode}")
        try {
            repository.waitForGuest(
                roomId = room.id,
                roomCode = room.roomCode,
                onGuestJoined = { matchId ->
                    if (matchId.isNotBlank()) {
                        Log.d("[ROOM]", "Guest joined, navigating to match: matchId=$matchId")
                        onEnterMatch(matchId, room.roomCode, "A")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e("[ROOM]", "Error in waitForGuest LaunchedEffect: ${e.message}", e)
        }
    }

    PitchBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (createdRoom != null) {
                                createdRoom = null
                                repository.stopListening()
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("online_lobby_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri Dön",
                            tint = LuxuryOnSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "FUTBOL TARGET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 10.sp
                            ),
                            color = LuxuryPrimary
                        )
                        Text(
                            text = "Play",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = LuxuryOnSurface
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Gold II Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LuxurySurfaceContainer)
                            .border(1.dp, LuxurySecondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = LuxurySecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gold II",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = LuxurySecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showConfigDialog = true },
                        modifier = Modifier.testTag("supabase_config_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Supabase Ayarları",
                            tint = LuxuryOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Supabase status indicator
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(LuxurySurfaceContainerLow)
                    .clickable { showConfigDialog = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isOnline = SupabaseConfig.isConfigured()
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) LuxuryTertiary else LuxurySecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnline) "Supabase Çevrimiçi" else "Supabase Bağlantısı Gerekli (Ayarla)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = LuxuryOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Error banner
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { msg ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            if (createdRoom == null) {
                // ================= LOBBY: CREATE OR JOIN (MOCKUP 3) =================

                // Title Area
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "PRIVATE MATCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontSize = 11.sp
                        ),
                        color = LuxuryOutline
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Play With Friend",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp
                        ),
                        color = LuxuryOnSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 1. CREATE ROOM CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
                    border = BorderStroke(1.dp, LuxuryOutlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Create Room",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                color = LuxuryOnSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(LuxurySurfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsScore,
                                    contentDescription = null,
                                    tint = LuxuryOutline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Host a private match and invite a friend with a unique code.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryOnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(LuxuryPrimaryContainer)
                                .clickable(enabled = !isCreating && !isJoining) {
                                    if (isCreating || isJoining) return@clickable
                                    if (!SupabaseConfig.isConfigured()) {
                                        errorMessage = "Online oynamak için önce Supabase URL ve Anon Key girilmelidir."
                                        showConfigDialog = true
                                        return@clickable
                                    }
                                    scope.launch {
                                        isCreating = true
                                        errorMessage = null
                                        try {
                                            Log.d("[ROOM]", "Oda Oluştur butonuna basıldı, createRoom çağrılıyor")
                                            val result = repository.createRoom()
                                            if (result.isSuccess) {
                                                val room = result.getOrNull()
                                                if (room != null && room.id.isNotBlank() && room.roomCode.isNotBlank()) {
                                                    Log.d("[ROOM]", "Oda başarıyla oluşturuldu: code=${room.roomCode}, waiting room gösteriliyor")
                                                    createdRoom = room
                                                } else {
                                                    Log.e("[CREATE_ROOM_ERROR]", "Oda sonucu eksik veya geçersiz: $room")
                                                    errorMessage = "Oda oluşturulamadı. Sunucu yanıtı eksik."
                                                }
                                            } else {
                                                val ex = result.exceptionOrNull()
                                                Log.e("[CREATE_ROOM_ERROR]", "createRoom başarısız oldu: ${ex?.message}", ex)
                                                val rawMsg = ex?.message.orEmpty()
                                                errorMessage = when {
                                                    rawMsg.contains("kimliği") -> rawMsg
                                                    rawMsg.contains("ayarlanmamış") -> "Supabase bağlantısı henüz yapılandırılmamış."
                                                    rawMsg.contains("Bağlantı") -> "İnternet bağlantınızı kontrol edin."
                                                    else -> "Oda oluşturulamadı. Lütfen tekrar deneyin."
                                                }
                                            }
                                        } catch (e: Throwable) {
                                            Log.e("[CREATE_ROOM_ERROR]", "Oda oluşturulurken beklenmeyen hata", e)
                                            errorMessage = "Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin."
                                        } finally {
                                            isCreating = false
                                        }
                                    }
                                }
                                .testTag("create_room_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCreating) {
                                CircularProgressIndicator(
                                    color = LuxuryOnSurface,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = "CREATE ROOM  ->",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        fontSize = 14.sp
                                    ),
                                    color = LuxuryOnSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // OR Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(LuxuryOutlineVariant.copy(alpha = 0.4f))
                    )
                    Text(
                        text = "OR JOIN MATCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontSize = 11.sp
                        ),
                        color = LuxuryOutline,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(LuxuryOutlineVariant.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. JOIN ROOM CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
                    border = BorderStroke(1.dp, LuxuryOutlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Join Room",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                color = LuxuryOnSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(LuxurySurfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pin,
                                    contentDescription = null,
                                    tint = LuxuryOutline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Enter your opponent's 6-character duel key.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryOnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large Monospace Input
                        OutlinedTextField(
                            value = roomCodeInput,
                            onValueChange = { input ->
                                val filtered = input.uppercase().filter { it.isLetterOrDigit() }.take(6)
                                roomCodeInput = filtered
                                errorMessage = null
                            },
                            placeholder = {
                                Text(
                                    text = "ENTER CODE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 4.sp,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    ),
                                    color = LuxuryOutline.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 6.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center,
                                color = LuxurySecondary
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LuxurySurfaceContainerLow,
                                unfocusedContainerColor = LuxurySurfaceContainerLow,
                                focusedBorderColor = LuxurySecondary,
                                unfocusedBorderColor = LuxuryOutlineVariant.copy(alpha = 0.5f),
                                cursorColor = LuxurySecondary
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .testTag("room_code_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "6-DIGIT CIPHER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = LuxuryOutline
                            )
                            Text(
                                text = "${roomCodeInput.length}/6",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = if (roomCodeInput.length == 6) LuxurySecondary else LuxuryOutline
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        val canJoin = roomCodeInput.length == 6 && !isCreating && !isJoining
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (canJoin) LuxuryPrimaryContainer else LuxurySurfaceContainerHighest
                                )
                                .clickable(enabled = canJoin) {
                                    val cleanInput = roomCodeInput.trim().uppercase()
                                    if (cleanInput.length != 6) return@clickable
                                    if (!SupabaseConfig.isConfigured()) {
                                        errorMessage = "Online oynamak için önce Supabase URL ve Anon Key girilmelidir."
                                        showConfigDialog = true
                                        return@clickable
                                    }
                                    focusManager.clearFocus()
                                    scope.launch {
                                        isJoining = true
                                        errorMessage = null
                                        try {
                                            Log.d("[ROOM]", "Odaya Katıl butonuna basıldı: $cleanInput")
                                            val result = repository.joinRoom(cleanInput)
                                            if (result.isSuccess) {
                                                val matchId = result.getOrNull().orEmpty()
                                                if (matchId.isNotBlank()) {
                                                    Log.d("[ROOM]", "Odaya katılma başarılı, maça geçiliyor: matchId=$matchId")
                                                    onEnterMatch(matchId, cleanInput, "B")
                                                } else {
                                                    Log.e("[ROOM]", "Katılma başarılı fakat matchId boş")
                                                    errorMessage = "Maç bilgisi alınamadı. Lütfen tekrar deneyin."
                                                }
                                            } else {
                                                val ex = result.exceptionOrNull()
                                                Log.e("[ROOM]", "Odaya katılma başarısız: ${ex?.message}", ex)
                                                errorMessage = ex?.localizedMessage ?: "Odaya bağlanılamadı"
                                            }
                                        } catch (e: Throwable) {
                                            Log.e("[ROOM]", "Odaya katılma sırasında beklenmeyen hata", e)
                                            errorMessage = "Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin."
                                        } finally {
                                            isJoining = false
                                        }
                                    }
                                }
                                .testTag("join_room_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isJoining) {
                                CircularProgressIndicator(
                                    color = LuxuryOnSurface,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = "JOIN ROOM 🔑",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        fontSize = 14.sp
                                    ),
                                    color = if (canJoin) LuxuryOnSurface else LuxuryOutline
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom server ping pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LuxurySurfaceContainerLow)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(LuxuryTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FRANKFURT SERVER • 18MS LATENCY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = LuxuryOutline
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

            } else {
                // ================= ROOM WAITING VIEW (MOCKUP 2) =================
                val room = createdRoom ?: return@PitchBackground

                // Top ambient indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LuxurySecondary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRIVATE TACTICAL ROOM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontSize = 10.sp
                            ),
                            color = LuxurySecondary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(LuxurySurfaceContainerLow)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = LuxuryOutline,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tier Locked",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = LuxuryOutline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Centerpiece Room Code Card
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainer),
                    border = BorderStroke(1.dp, LuxurySecondary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("room_waiting_card")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ROOM CODE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 11.sp
                            ),
                            color = LuxuryOutline
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Code & Copy Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = room.roomCode.ifBlank { "------" },
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 6.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 38.sp
                                ),
                                color = LuxurySecondary
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LuxurySurfaceContainerHigh)
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Futbol Target Oda Kodu", room.roomCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Oda kodu panoya kopyalandı!", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Kodu Kopyala",
                                    tint = LuxurySecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Share code with an opponent to initiate 1v1 target precision shootout.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = LuxuryOnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Waiting Status Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    CircularProgressIndicator(
                        color = LuxurySecondary,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Waiting for opponent...",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LuxuryOnSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Matchup Preview: Head-to-Head Architectural Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxurySurfaceContainerLow),
                    border = BorderStroke(1.dp, LuxuryOutlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Host (You)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(LuxurySurfaceContainerHighest)
                                        .border(1.5.dp, LuxurySecondary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = LuxurySecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(LuxurySecondary)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "HOST",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 7.sp
                                            ),
                                            color = LuxurySurface
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Alex (Sen)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LuxuryOnSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = LuxurySecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Gold II",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = LuxurySecondary
                                    )
                                }
                                Text(
                                    text = "84.2% Acc.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = LuxuryOutline
                                )
                            }

                            // Center: VS
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(LuxurySurfaceContainerHighest)
                                        .border(1.dp, LuxuryOutlineVariant, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "VS",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                        color = LuxuryOnSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1v1 Turn",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = LuxuryOutline
                                )
                            }

                            // Right: Opponent (Waiting)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(LuxurySurfaceContainerHighest.copy(alpha = 0.5f))
                                        .border(1.5.dp, LuxuryOutline.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonOutline,
                                        contentDescription = null,
                                        tint = LuxuryOutline.copy(alpha = 0.5f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Waiting...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LuxuryOutline
                                )
                                Text(
                                    text = "Any Rank",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = LuxuryOutline.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Readying",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = LuxuryOutline.copy(alpha = 0.4f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(LuxuryOutlineVariant.copy(alpha = 0.4f))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Parameters row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "SHOTS: 5 Rounds",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = LuxuryOutline
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = LuxuryOutlineVariant
                            )
                            Text(
                                text = "MODE: Precision",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = LuxuryOutline
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = LuxuryOutlineVariant
                            )
                            Text(
                                text = "STAKE: 150 Rating",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = LuxurySecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxurySurfaceContainerHigh)
                        .clickable {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Futbol Target 1v1 maç kodum: ${room.roomCode}. Hemen odaya katıl ve karşılaşalım!"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Oda Kodunu Paylaş")
                            context.startActivity(shareIntent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = LuxuryOnSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Invitation Link",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = LuxuryOnSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxurySurfaceContainerLow)
                        .border(1.dp, LuxuryOutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable {
                            createdRoom = null
                            repository.stopListening()
                        }
                        .testTag("cancel_room_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = LuxuryOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cancel & Return to Lobby",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = LuxuryOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Config dialog for testing Supabase credentials directly on device
    if (showConfigDialog) {
        var inputUrl by remember { mutableStateOf(SupabaseConfig.getSupabaseUrl()) }
        var inputKey by remember { mutableStateOf(SupabaseConfig.getSupabaseAnonKey()) }

        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Text(
                    text = "Supabase Bağlantı Ayarları",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Supabase projenizin API URL ve Anon Key değerlerini girebilir veya AI Studio Secrets panelinden ayarlayabilirsiniz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("Supabase URL") },
                        placeholder = { Text("https://xyz.supabase.co") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        label = { Text("Supabase Anon Key") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        SupabaseConfig.saveCustomConfig(inputUrl, inputKey)
                        showConfigDialog = false
                        Toast.makeText(context, "Ayarlar kaydedildi!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }
}
