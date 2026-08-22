package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuCardBorder
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSurface
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.viewmodel.AppTab

@Composable
fun AppBottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ValkuSurface.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = ValkuCardBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .testTag("app_bottom_nav")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Chats
            BottomNavItem(
                title = "Chats",
                selectedIcon = Icons.Filled.ChatBubble,
                unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                isSelected = selectedTab == AppTab.CHATS,
                badgeCount = 3,
                activeColor = Color(0xFF60A5FA),
                onClick = { onTabSelected(AppTab.CHATS) }
            )

            // Tab 2: Feed
            BottomNavItem(
                title = "Feed",
                selectedIcon = Icons.Filled.DynamicFeed,
                unselectedIcon = Icons.Outlined.DynamicFeed,
                isSelected = selectedTab == AppTab.FEED,
                activeColor = Color(0xFF60A5FA),
                onClick = { onTabSelected(AppTab.FEED) }
            )

            // Center Prominent Floating Action: Valku AI
            CentralAiNavItem(
                isSelected = selectedTab == AppTab.AI,
                onClick = { onTabSelected(AppTab.AI) }
            )

            // Tab 4: Reels
            BottomNavItem(
                title = "Reels",
                selectedIcon = Icons.Filled.PlayCircle,
                unselectedIcon = Icons.Outlined.PlayCircleOutline,
                isSelected = selectedTab == AppTab.REELS,
                activeColor = Color(0xFF60A5FA),
                onClick = { onTabSelected(AppTab.REELS) }
            )

            // Tab 5: Games / Arcade
            BottomNavItem(
                title = "Games",
                selectedIcon = Icons.Filled.SportsEsports,
                unselectedIcon = Icons.Outlined.SportsEsports,
                isSelected = selectedTab == AppTab.ARCADE,
                activeColor = Color(0xFF60A5FA),
                onClick = { onTabSelected(AppTab.ARCADE) }
            )

            // Tab 6: Portal
            BottomNavItem(
                title = "Portal",
                selectedIcon = Icons.Filled.Language,
                unselectedIcon = Icons.Outlined.Language,
                isSelected = selectedTab == AppTab.WEB_PORTAL,
                activeColor = Color(0xFF60A5FA),
                onClick = { onTabSelected(AppTab.WEB_PORTAL) }
            )
        }
    }
}

@Composable
private fun CentralAiNavItem(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .offset(y = (-14).dp)
            .clickable(onClick = onClick)
            .testTag("nav_item_ai"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF3B82F6),
                    spotColor = Color(0xFF4F46E5)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = if (isSelected) {
                            listOf(Color(0xFF60A5FA), Color(0xFF4F46E5))
                        } else {
                            listOf(Color(0xFF3B82F6), Color(0xFF312E81))
                        }
                    )
                )
                .border(3.dp, ValkuBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Valku AI Assistant",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "AI Hub",
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF60A5FA) else ValkuTextMuted
        )
    }
}

@Composable
private fun BottomNavItem(
    title: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val animatedTint by animateColorAsState(
        targetValue = if (isSelected) activeColor else ValkuTextMuted,
        label = "nav_tint"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0x263B82F6) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = title,
                tint = animatedTint,
                modifier = Modifier.size(22.dp)
            )

            if (badgeCount > 0 && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8))
                        .align(Alignment.TopEnd)
                )
            }
        }

        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = animatedTint,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

