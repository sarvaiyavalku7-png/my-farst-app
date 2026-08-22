package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuCardBackground
import com.example.ui.theme.ValkuCardBorder
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSecondary
import com.example.ui.theme.ValkuSurface
import com.example.ui.theme.ValkuTertiary
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.theme.ValkuTextPrimary
import com.example.ui.theme.ValkuTextSecondary
import com.example.ui.viewmodel.ValkuViewModel

@Composable
fun WebPortalAndInviteScreen(
    viewModel: ValkuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🌐 Web Portal", "👥 Invite Contacts", "🛡️ Safe & Secure")

    // Privacy settings states
    var isEncryptionEnabled by remember { mutableStateOf(true) }
    var isBiometricLockEnabled by remember { mutableStateOf(true) }
    var isAntiSpamEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ValkuBackground)
            .padding(bottom = 80.dp)
            .testTag("web_portal_and_invite_screen")
    ) {
        // Top Switcher Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ValkuSurface,
            contentColor = Color(0xFF1877F2),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF1877F2),
                    height = 2.5.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFF1877F2) else ValkuTextSecondary
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> WebPortalHubView(
                onOpenBrowser = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://valku-sarvaiya.app"))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback
                    }
                },
                onShare = { viewModel.shareInviteLink(context) }
            )
            1 -> InviteContactsHubView(
                onShareGeneral = { viewModel.shareInviteLink(context) },
                onInviteWhatsApp = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey! Join me on Valku Sarvaiya Super App 🌟 - Chat, Video Calls, Reels, Gemini AI & Games in one place! Download now: https://valku-sarvaiya.app"
                        )
                        setPackage("com.whatsapp")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        viewModel.shareInviteLink(context)
                    }
                }
            )
            2 -> SafeAndSecureHubView(
                isEncryptionEnabled = isEncryptionEnabled,
                onToggleEncryption = { isEncryptionEnabled = !isEncryptionEnabled },
                isBiometricEnabled = isBiometricLockEnabled,
                onToggleBiometric = { isBiometricLockEnabled = !isBiometricLockEnabled },
                isAntiSpamEnabled = isAntiSpamEnabled,
                onToggleAntiSpam = { isAntiSpamEnabled = !isAntiSpamEnabled }
            )
        }
    }
}

@Composable
fun WebPortalHubView(
    onOpenBrowser: () -> Unit,
    onShare: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Hero Web Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.valku_hero_banner),
                        contentDescription = "Valku Web Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "valku-sarvaiya.app",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ValkuPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF00E676))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF090814))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Official Web Ecosystem of Valku Sarvaiya",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ValkuTextPrimary
                        )

                        Text(
                            text = "Access your chats, social feed, reels, and Gemini AI directly from any desktop or mobile browser.",
                            fontSize = 12.sp,
                            color = ValkuTextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onOpenBrowser,
                                colors = ButtonDefaults.buttonColors(containerColor = ValkuPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = "Open", tint = Color(0xFF090814))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Website", color = Color(0xFF090814), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = onShare,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28224C)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Live Ecosystem Statistics
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "⚡ Real-Time Ecosystem Metrics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPillCard(
                    title = "Active Users",
                    value = "100K+",
                    accentColor = Color(0xFF00E676),
                    modifier = Modifier.weight(1f)
                )
                StatPillCard(
                    title = "Uptime",
                    value = "99.9%",
                    accentColor = ValkuPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatPillCard(
                    title = "AI Latency",
                    value = "0.2s",
                    accentColor = ValkuTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Feature Highlights
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "✨ Genuine Features in 1 App",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val features = listOf(
                "💬 WhatsApp Style Chat & HD Audio/Video Calls",
                "📸 Instagram & Facebook Community Feed & Stories",
                "⚡ 4K Short Video Reels & Long Video Watch",
                "🤖 Gemini 3.5 AI Problem Solver & Content Studio",
                "🎮 4 Playable Arcade Mini-Games",
                "🛡️ 100% Safe End-to-End Encrypted Cloud & Local Vault"
            )

            features.forEach { feat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Feature",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feat, fontSize = 13.sp, color = ValkuTextSecondary)
                }
            }
        }
    }
}

@Composable
fun StatPillCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 11.sp, color = ValkuTextMuted)
        }
    }
}

@Composable
fun InviteContactsHubView(
    onShareGeneral: () -> Unit,
    onInviteWhatsApp: () -> Unit
) {
    val sampleContacts = listOf(
        Pair("Rahul Sharma", "+91 98765 43210"),
        Pair("Pooja Patel", "+91 91234 56789"),
        Pair("Amit Varma", "+91 99887 76655"),
        Pair("Sneha Joshi", "+91 94455 66778"),
        Pair("Karan Desai", "+91 93322 11009")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Invite Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E173E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ValkuPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Invite Friends to Valku Sarvaiya",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ValkuPrimary
                    )
                    Text(
                        text = "Get connected with friends, chat in real-time, share reels and challenge them in arcade games!",
                        fontSize = 12.sp,
                        color = ValkuTextSecondary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onInviteWhatsApp,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Invite on WhatsApp", color = Color(0xFF090814), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onShareGeneral,
                            colors = ButtonDefaults.buttonColors(containerColor = ValkuSecondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Phone Contacts",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(sampleContacts.size) { index ->
            val (name, phone) = sampleContacts[index]
            var isInvited by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF28234D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(name.take(2).uppercase(), color = ValkuPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ValkuTextPrimary)
                            Text(text = phone, fontSize = 11.sp, color = ValkuTextMuted)
                        }
                    }

                    Button(
                        onClick = {
                            isInvited = true
                            onShareGeneral()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInvited) Color(0xFF252147) else ValkuPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isInvited) "Invited ✓" else "Invite",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInvited) Color(0xFF00E676) else Color(0xFF090814)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SafeAndSecureHubView(
    isEncryptionEnabled: Boolean,
    onToggleEncryption: () -> Unit,
    isBiometricEnabled: Boolean,
    onToggleBiometric: () -> Unit,
    isAntiSpamEnabled: Boolean,
    onToggleAntiSpam: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13231F)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "🛡️ 100% Safe & Protected",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )
                        Text(
                            text = "Valku Sarvaiya uses AES-256 military grade encryption for your chats, calls & posts.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Privacy Controls",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Toggles
        item {
            PrivacyToggleCard(
                icon = Icons.Default.Lock,
                title = "End-to-End Chat Encryption",
                desc = "Encrypt all text messages, audio recordings, and video streams.",
                isChecked = isEncryptionEnabled,
                onCheckedChange = onToggleEncryption
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            PrivacyToggleCard(
                icon = Icons.Default.Fingerprint,
                title = "Biometric & App Lock",
                desc = "Require fingerprint/PIN to access Valku Sarvaiya.",
                isChecked = isBiometricEnabled,
                onCheckedChange = onToggleBiometric
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            PrivacyToggleCard(
                icon = Icons.Default.Speed,
                title = "AI Anti-Spam Shield",
                desc = "Automatically filter out spam messages and fraudulent links.",
                isChecked = isAntiSpamEnabled,
                onCheckedChange = onToggleAntiSpam
            )
        }
    }
}

@Composable
fun PrivacyToggleCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = ValkuPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ValkuTextPrimary)
                    Text(text = desc, fontSize = 11.sp, color = ValkuTextMuted)
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = { onCheckedChange() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00E676),
                    checkedTrackColor = Color(0xFF005C4B)
                )
            )
        }
    }
}
