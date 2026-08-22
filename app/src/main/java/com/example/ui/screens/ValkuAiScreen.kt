package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AiMessage
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuCardBackground
import com.example.ui.theme.ValkuCardBorder
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSecondary
import com.example.ui.theme.ValkuSurface
import com.example.ui.theme.ValkuSurfaceVariant
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.theme.ValkuTextPrimary
import com.example.ui.theme.ValkuTextSecondary
import com.example.ui.viewmodel.ValkuViewModel

enum class AiMode(val title: String, val promptType: String) {
    CHAT("💬 Valku Chat", "chat"),
    PROBLEM_SOLVER("💡 Problem Hal", "problem_solve"),
    VIDEO_SCRIPT("🎬 Short Video Script", "video_script"),
    PHOTO_ART("🎨 Photo Art Generator", "art_prompt")
}

@Composable
fun ValkuAiScreen(
    viewModel: ValkuViewModel,
    modifier: Modifier = Modifier
) {
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()

    var selectedMode by remember { mutableStateOf(AiMode.CHAT) }
    var promptInput by remember { mutableStateOf("") }

    val quickPrompts = when (selectedMode) {
        AiMode.PROBLEM_SOLVER -> listOf(
            "Solve: 3x + 15 = 45 step by step",
            "Android me internet permission kaise add karein?",
            "How to fix battery draining issue in phone?",
            "Python me palindrome checker code likhein"
        )
        AiMode.VIDEO_SCRIPT -> listOf(
            "Viral 30-sec reel script on Valku Sarvaiya App",
            "Comedy Short video idea on Indian college life",
            "Tech trend reel hook on Gemini 3.5 AI",
            "Fitness motivation reel with Hindi voiceover"
        )
        AiMode.PHOTO_ART -> listOf(
            "Cyberpunk Ahmedabad city in neon cyan & violet 8K",
            "Futuristic humanoid robot enjoying Indian chai",
            "Neon portrait of a digital creator with glowing headphones",
            "Surreal flying car over Gujarat landscape"
        )
        AiMode.CHAT -> listOf(
            "Valku Sarvaiya app ke sabhi features explain karo",
            "Mujhe coding aur daily life me motivate karo",
            "Namaste! Tell me a smart joke in Hindi",
            "Top 5 tech innovations of this year"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ValkuBackground)
            .testTag("valku_ai_screen")
    ) {
        // Immersive AI Hero Banner Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0x801E1B4B), Color(0x80172554))
                        )
                    )
                    .border(1.dp, Color(0x4D60A5FA), RoundedCornerShape(24.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF3B82F6), Color(0xFF6366F1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Robot",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VALKU GEMINI AI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA),
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x3338BDF8))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("3.5 PRO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                        }
                        Text(
                            text = "How can I help you today?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ValkuTextPrimary
                        )
                        Text(
                            text = "Solve problems, write scripts, or generate art prompts.",
                            fontSize = 11.sp,
                            color = ValkuTextMuted
                        )
                    }

                    IconButton(
                        onClick = { viewModel.clearAiChat() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x801E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Chat",
                            tint = ValkuTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Feature Mode Switcher Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AiMode.values()) { mode ->
                    val isSelected = mode == selectedMode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF6366F1)))
                                } else {
                                    Brush.linearGradient(listOf(Color(0x801E293B), Color(0x801E293B)))
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF60A5FA) else Color(0x80334155),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedMode = mode }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mode.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else ValkuTextSecondary
                        )
                    }
                }
            }
        }

        // AI Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentPadding = PaddingValues(bottom = 10.dp)
        ) {
            items(aiMessages, key = { it.id }) { msg ->
                AiMessageCard(message = msg)
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Valku Gemini AI is crafting response...",
                            color = Color(0xFF60A5FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips & Input Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ValkuSurface.copy(alpha = 0.95f))
                .border(
                    width = 1.dp,
                    color = ValkuCardBorder.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .imePadding()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickPrompts) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x801E293B))
                            .border(1.dp, Color(0x80334155), RoundedCornerShape(14.dp))
                            .clickable {
                                promptInput = prompt
                                viewModel.sendAiPrompt(prompt, selectedMode.title, selectedMode.promptType)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "✨ $prompt",
                            fontSize = 11.sp,
                            color = ValkuTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = {
                        Text(
                            text = when (selectedMode) {
                                AiMode.PROBLEM_SOLVER -> "Ask any problem / question to solve..."
                                AiMode.VIDEO_SCRIPT -> "Describe reel topic for viral script..."
                                AiMode.PHOTO_ART -> "Describe photo / art concept you want..."
                                AiMode.CHAT -> "Ask Valku AI anything..."
                            },
                            color = ValkuTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_prompt_input"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ValkuSurfaceVariant,
                        unfocusedContainerColor = ValkuSurfaceVariant,
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary,
                        focusedBorderColor = Color(0xFF60A5FA),
                        unfocusedBorderColor = Color(0x80334155)
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            val p = promptInput
                            promptInput = ""
                            viewModel.sendAiPrompt(p, selectedMode.title, selectedMode.promptType)
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF6366F1)))
                        )
                        .testTag("ai_send_prompt_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Prompt",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiMessageCard(message: AiMessage) {
    val isAi = message.isAi

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(if (isAi) 0.95f else 0.85f)
        ) {
            if (isAi) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(Color(0xFF60A5FA), Color(0xFFA855F7), Color(0xFFEC4899), Color(0xFF60A5FA))
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Card(
                shape = RoundedCornerShape(
                    topStart = if (isAi) 4.dp else 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = if (isAi) 16.dp else 4.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAi) Color(0xFF0F1420) else Color(0xFF1E3A8A).copy(alpha = 0.8f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAi) Color(0xFF1E293B) else Color(0x6660A5FA)
                ),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (isAi) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Valku AI Assistant",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA)
                            )
                            Text(
                                text = message.category,
                                fontSize = 10.sp,
                                color = ValkuTextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = message.content,
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 19.sp
                    )

                    // If Photo Art prompt mode, show creative visual sample card
                    if (message.generatedMediaPrompt != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF311042))
                                    )
                                )
                                .border(1.dp, Color(0x3360A5FA), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "AI Art",
                                    tint = Color(0xFF60A5FA),
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "✨ 8K Visual Concept Ready",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Directly shareable to Valku Feed & Reels",
                                    color = ValkuTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

