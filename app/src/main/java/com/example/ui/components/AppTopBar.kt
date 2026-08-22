package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSecondary
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.theme.ValkuTextPrimary

@Composable
fun AppTopBar(
    currentUser: UserProfile? = null,
    onSearchClick: () -> Unit = {},
    onAiSparkClick: () -> Unit = {},
    onInviteClick: () -> Unit = {},
    onQuickCallClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val brandGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF60A5FA), Color(0xFFA855F7))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ValkuBackground)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("app_top_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Identity with Gradient Title & Immersive Tagline
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1E1B4B), Color(0xFF172554))
                        )
                    )
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_valku_logo),
                    contentDescription = "Valku Sarvaiya Logo",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Valku Sarvaiya",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    style = TextStyle(
                        brush = brandGradient
                    )
                )
                Text(
                    text = if (currentUser?.isLoggedIn == true) "● LOGGED IN • ${currentUser.name}" else "ULTIMATE SOCIAL HUB",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = if (currentUser?.isLoggedIn == true) Color(0xFF10B981) else ValkuTextMuted,
                    maxLines = 1
                )
            }
        }

        // Action Icons styled as frosted circular controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Valku AI Glowing Spark Control
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x333B82F6))
                    .border(1.dp, Color(0x4D60A5FA), CircleShape)
                    .clickable { onAiSparkClick() }
                    .testTag("ai_spark_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Valku AI",
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(17.dp)
                )
            }

            // Quick Search Control
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x801E293B))
                    .border(1.dp, Color(0x80334155), CircleShape)
                    .clickable { onSearchClick() }
                    .testTag("search_top_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(17.dp)
                )
            }

            // Settings Control (Setting ka option)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x801E293B))
                    .border(1.dp, Color(0x80334155), CircleShape)
                    .clickable { onSettingsClick() }
                    .testTag("settings_top_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFFE2E8F0),
                    modifier = Modifier.size(17.dp)
                )
            }

            // Login / User Profile Control (Login ka option)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentUser?.isLoggedIn == true)
                            Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF9333EA)))
                        else
                            Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                    )
                    .border(1.dp, if (currentUser?.isLoggedIn == true) Color(0xFF38BDF8) else Color(0x8064748B), CircleShape)
                    .clickable { onProfileClick() }
                    .testTag("login_profile_top_button"),
                contentAlignment = Alignment.Center
            ) {
                if (currentUser?.isLoggedIn == true && currentUser.name.isNotBlank()) {
                    Text(
                        text = currentUser.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Login / Profile",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

