package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LongVideoItem
import com.example.data.model.ReelItem
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuCardBackground
import com.example.ui.theme.ValkuCardBorder
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSecondary
import com.example.ui.theme.ValkuSurface
import com.example.ui.theme.ValkuSurfaceVariant
import com.example.ui.theme.ValkuTertiary
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.theme.ValkuTextPrimary
import com.example.ui.theme.ValkuTextSecondary
import com.example.ui.viewmodel.ValkuViewModel

@Composable
fun ReelsAndWatchScreen(
    viewModel: ValkuViewModel,
    modifier: Modifier = Modifier
) {
    val reels by viewModel.reels.collectAsState()
    val longVideos by viewModel.longVideos.collectAsState()
    val context = LocalContext.current

    var selectedModeIndex by remember { mutableIntStateOf(0) }
    val modes = listOf("⚡ Reels & Shorts", "📺 Long Video Watch")
    var currentReelIndex by remember { mutableIntStateOf(0) }
    var showCreateReelModal by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(ValkuBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Mode Tabs (Reels vs Long Video)
            TabRow(
                selectedTabIndex = selectedModeIndex,
                containerColor = ValkuSurface,
                contentColor = ValkuTertiary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedModeIndex]),
                        color = ValkuTertiary,
                        height = 2.5.dp
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                modes.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedModeIndex == index,
                        onClick = { selectedModeIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedModeIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedModeIndex == index) ValkuTertiary else ValkuTextSecondary
                            )
                        }
                    )
                }
            }

            if (selectedModeIndex == 0) {
                // Full Screen Reels View
                if (reels.isNotEmpty()) {
                    val safeIndex = currentReelIndex.coerceIn(0, reels.size - 1)
                    val currentReel = reels[safeIndex]

                    ReelPlayerView(
                        reel = currentReel,
                        currentIndex = safeIndex,
                        totalCount = reels.size,
                        onSwipeNext = {
                            if (currentReelIndex < reels.size - 1) {
                                currentReelIndex++
                            } else {
                                currentReelIndex = 0 // loop
                            }
                        },
                        onSwipePrev = {
                            if (currentReelIndex > 0) currentReelIndex--
                        },
                        onLike = { viewModel.toggleLikeReel(currentReel.id) },
                        onFollow = { viewModel.toggleFollowReel(currentReel.id) },
                        onShare = { viewModel.shareInviteLink(context) },
                        onCreateReel = { showCreateReelModal = true }
                    )
                }
            } else {
                // Long Videos Hub
                LongVideosHubView(
                    videos = longVideos,
                    onVideoClick = { /* Watch long video */ },
                    onShare = { viewModel.shareInviteLink(context) },
                    onCreateVideo = { showCreateReelModal = true }
                )
            }
        }

        // Create Reel / Short Video Modal
        if (showCreateReelModal) {
            CreateReelModal(
                onDismiss = { showCreateReelModal = false },
                onCreate = { title, desc, audio ->
                    viewModel.createReel(title, desc, audio)
                    showCreateReelModal = false
                }
            )
        }
    }
}

@Composable
fun ReelPlayerView(
    reel: ReelItem,
    currentIndex: Int,
    totalCount: Int,
    onSwipeNext: () -> Unit,
    onSwipePrev: () -> Unit,
    onLike: () -> Unit,
    onFollow: () -> Unit,
    onShare: () -> Unit,
    onCreateReel: () -> Unit
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "music_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_spin"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                state = rememberDraggableState { delta ->
                    offsetY += delta
                },
                orientation = Orientation.Vertical,
                onDragStopped = {
                    if (offsetY < -100f) {
                        onSwipeNext()
                    } else if (offsetY > 100f) {
                        onSwipePrev()
                    }
                    offsetY = 0f
                }
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(reel.gradientColors.getOrElse(0) { 0xFF2B1055 }),
                        Color(reel.gradientColors.getOrElse(1) { 0xFF750035 }),
                        Color(0xFF090814)
                    )
                )
            )
            .testTag("reel_player_view")
    ) {
        // Video Visual Atmosphere with glowing pulse
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Playing Reel",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "▶ 4K Reel Video Playing",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Swipe UP for Next Reel (${currentIndex + 1}/$totalCount)",
                    color = ValkuPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Top Controls: Create Reel Button + Live indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REEL LIVE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Button(
                onClick = onCreateReel,
                colors = ButtonDefaults.buttonColors(containerColor = ValkuTertiary),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Reel", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Reel", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Floating Right Action Buttons (Like, Comment, Share, Audio Disc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Reel",
                        tint = if (reel.isLiked) Color(0xFFFF007F) else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "${reel.likesCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${reel.commentsCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Reel",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${reel.sharesCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Spinning Music Disc
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, ValkuTertiary, CircleShape)
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio",
                    tint = ValkuTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bottom Left Creator & Sound Info Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 90.dp)
        ) {
            // Creator Handle & Follow button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E173E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.authorName.take(2).uppercase(),
                        color = ValkuPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = reel.authorHandle,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onFollow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reel.isFollowed) Color.White.copy(alpha = 0.2f) else ValkuPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (reel.isFollowed) "Following" else "Follow",
                        fontSize = 11.sp,
                        color = if (reel.isFollowed) Color.White else Color(0xFF090814),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = reel.title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = reel.description,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio track",
                    tint = ValkuPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reel.audioTrackName,
                    color = ValkuPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun LongVideosHubView(
    videos: List<LongVideoItem>,
    onVideoClick: (LongVideoItem) -> Unit,
    onShare: () -> Unit,
    onCreateVideo: () -> Unit
) {
    val categories = listOf("All", "Technology", "AI & Gemini", "Gaming", "Vlogs", "Podcasts")
    var selectedCategory by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Upload Action Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎬 Watch Long Videos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ValkuTextPrimary
                )
                Button(
                    onClick = onCreateVideo,
                    colors = ButtonDefaults.buttonColors(containerColor = ValkuSecondary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload Video", fontSize = 12.sp)
                }
            }
        }

        // Category Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSel = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) ValkuTertiary else ValkuSurfaceVariant)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else ValkuTextSecondary
                        )
                    }
                }
            }
        }

        // Videos List
        items(videos, key = { it.id }) { video ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onVideoClick(video) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
            ) {
                Column {
                    // Video Thumbnail with Duration badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E103E), Color(0xFF0C243B), Color(0xFF2C0B4E))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Play Video",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(54.dp)
                        )

                        // Duration Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = video.duration,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Video Info Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2B2154)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(video.channelName.take(2).uppercase(), color = ValkuPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ValkuTextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${video.channelName} • ${video.views} • ${video.uploadTime}",
                                fontSize = 11.sp,
                                color = ValkuTextMuted
                            )
                        }

                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = ValkuTextMuted, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateReelModal(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var audioTrack by remember { mutableStateOf("Valku Super Cyber Beat 🎵") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Text("Create New Reel / Short Video", fontWeight = FontWeight.Bold, color = ValkuTextPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reel Title / Hook") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Caption & Hashtags") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = audioTrack,
                    onValueChange = { audioTrack = it },
                    label = { Text("Audio Track Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title, description, audioTrack) },
                colors = ButtonDefaults.buttonColors(containerColor = ValkuTertiary)
            ) {
                Text("Publish Reel 🚀", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}
