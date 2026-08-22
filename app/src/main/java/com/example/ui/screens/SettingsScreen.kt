package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ValkuAccentBlue
import com.example.ui.theme.ValkuAccentEmerald
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuCardBorder
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSecondary
import com.example.ui.theme.ValkuSurface
import com.example.ui.theme.ValkuSurfaceVariant
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.theme.ValkuTextPrimary
import com.example.ui.theme.ValkuTextSecondary
import com.example.ui.viewmodel.ValkuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ValkuViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    var clearedCacheToast by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = ValkuBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ValkuSurface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ValkuSurfaceVariant)
                        .testTag("settings_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ValkuTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Settings / सेटिंग्स",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ValkuTextPrimary
                    )
                    Text(
                        text = "Preferences, account & customization",
                        fontSize = 11.sp,
                        color = ValkuTextSecondary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile & Account Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openAuthModal() }
                    .testTag("settings_account_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ValkuSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFFA855F7)))
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF2563EB), Color(0xFF9333EA))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = ValkuTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = currentUser.handle,
                            fontSize = 13.sp,
                            color = Color(0xFF60A5FA),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (currentUser.isLoggedIn) "📱 ${currentUser.phoneNumber} • Tap to edit / login" else "Guest Mode • Tap to Sign In",
                            fontSize = 11.sp,
                            color = ValkuTextMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = ValkuPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Chat, Media & Attachments
            SettingsSectionHeader(title = "Chat, Media & Sharing / चैटिंग और शेयर")
            SettingsCard {
                SettingsRowWithSwitch(
                    icon = Icons.Default.CloudDone,
                    iconTint = Color(0xFF10B981),
                    title = "Auto-Download Media",
                    subtitle = "Automatically download photos, voice & audio on Wi-Fi/Data",
                    isChecked = settings.autoDownloadMedia,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(autoDownloadMedia = checked) }
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsRowWithSwitch(
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF38BDF8),
                    title = "Read Receipts",
                    subtitle = "Show double blue checkmarks when messages are read",
                    isChecked = settings.readReceipts,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(readReceipts = checked) }
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsClickableRow(
                    icon = Icons.Default.Chat,
                    iconTint = Color(0xFFA855F7),
                    title = "Chat Wallpaper & Theme",
                    value = settings.chatWallpaper,
                    onClick = {
                        Toast.makeText(context, "Chat theme: ${settings.chatWallpaper}", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: AI & Gemini Engine Preferences
            SettingsSectionHeader(title = "Valku AI & Gemini Engine / आर्टिफिशियल इंटेलिजेंस")
            SettingsCard {
                SettingsClickableRow(
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFF60A5FA),
                    title = "AI Model Engine",
                    value = settings.geminiModel,
                    onClick = {
                        val nextModel = if (settings.geminiModel == "Gemini 3.5 Pro") "Gemini 3.5 Flash" else "Gemini 3.5 Pro"
                        viewModel.updateSettings { it.copy(geminiModel = nextModel) }
                        Toast.makeText(context, "Selected AI Model: $nextModel", Toast.LENGTH_SHORT).show()
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsRowWithSwitch(
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFFFACC15),
                    title = "Smart Chat Suggestions",
                    subtitle = "Generate one-tap intelligent AI replies in active chats",
                    isChecked = true,
                    onCheckedChange = {}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Notifications & Sound
            SettingsSectionHeader(title = "Notifications & Calls / सूचनाएं और रिंगटोन")
            SettingsCard {
                SettingsRowWithSwitch(
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFFEC4899),
                    title = "Push Notifications",
                    subtitle = "Alerts for new messages, friend requests & calls",
                    isChecked = settings.notificationsEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(notificationsEnabled = checked) }
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsClickableRow(
                    icon = Icons.Default.VolumeUp,
                    iconTint = Color(0xFF10B981),
                    title = "Call Ringtone",
                    value = settings.callRingtone,
                    onClick = {
                        val nextTone = if (settings.callRingtone == "Valku Cyber Wave") "Ambient Synth" else "Valku Cyber Wave"
                        viewModel.updateSettings { it.copy(callRingtone = nextTone) }
                        Toast.makeText(context, "Ringtone: $nextTone", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 4: Privacy & Security
            SettingsSectionHeader(title = "Privacy & Security / प्राइवेसी और सुरक्षा")
            SettingsCard {
                SettingsRowWithSwitch(
                    icon = Icons.Default.Lock,
                    iconTint = Color(0xFF38BDF8),
                    title = "App Biometric Lock",
                    subtitle = "Require fingerprint or PIN when launching Valku app",
                    isChecked = settings.appLockEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(appLockEnabled = checked) }
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsClickableRow(
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFF6366F1),
                    title = "Two-Step Verification",
                    value = "Enabled (OTP)",
                    onClick = {
                        Toast.makeText(context, "Two-Step Verification is active", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 5: Language & Storage
            SettingsSectionHeader(title = "Language & Storage / भाषा और स्टोरेज")
            SettingsCard {
                SettingsClickableRow(
                    icon = Icons.Default.Language,
                    iconTint = Color(0xFFF97316),
                    title = "Language / भाषा / ભાષા",
                    value = settings.language,
                    onClick = {
                        val nextLang = when (settings.language) {
                            "English" -> "Hindi (हिंदी)"
                            "Hindi (हिंदी)" -> "Gujarati (ગુજરાતી)"
                            else -> "English"
                        }
                        viewModel.updateSettings { it.copy(language = nextLang) }
                        Toast.makeText(context, "Language switched to $nextLang", Toast.LENGTH_SHORT).show()
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsClickableRow(
                    icon = Icons.Default.Delete,
                    iconTint = Color(0xFFFF5252),
                    title = "Clear Cached Media & Storage",
                    value = "142 MB cached",
                    onClick = {
                        Toast.makeText(context, "Cleared 142 MB cache memory! 🚀", Toast.LENGTH_LONG).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 6: About & Share App
            SettingsSectionHeader(title = "About / ऐप के बारे में")
            SettingsCard {
                SettingsClickableRow(
                    icon = Icons.Default.Share,
                    iconTint = Color(0xFF00E676),
                    title = "Share Valku Sarvaiya App",
                    value = "Invite link",
                    onClick = {
                        viewModel.shareInviteLink(context)
                    }
                )
                HorizontalDivider(color = ValkuCardBorder.copy(alpha = 0.5f))
                SettingsClickableRow(
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF94A3B8),
                    title = "Version & Architecture",
                    value = "v3.5.0 Pro (Obsidian Slate)",
                    onClick = {
                        Toast.makeText(context, "Valku Sarvaiya Super App • Built with Jetpack Compose & Gemini AI", Toast.LENGTH_LONG).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = ValkuTextSecondary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ValkuCardBorder, ValkuCardBorder)))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsRowWithSwitch(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = ValkuTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = ValkuTextMuted
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ValkuPrimary,
                uncheckedThumbColor = ValkuTextMuted,
                uncheckedTrackColor = ValkuCardBorder
            )
        )
    }
}

@Composable
fun SettingsClickableRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = ValkuTextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 13.sp,
            color = ValkuPrimary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = ValkuTextMuted,
            modifier = Modifier.size(12.dp)
        )
    }
}
