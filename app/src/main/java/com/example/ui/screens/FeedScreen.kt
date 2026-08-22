package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.data.model.FriendContact
import com.example.data.model.PostComment
import com.example.data.model.SocialPost
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: ValkuViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val context = LocalContext.current

    var feedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("🌍 Feed", "👥 Friends & Requests")
    var showCreatePostModal by remember { mutableStateOf(false) }

    // Comments Sheet State
    var activeCommentPost by remember { mutableStateOf<SocialPost?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize().background(ValkuBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Feed Tabs (Feed vs Friends)
            TabRow(
                selectedTabIndex = feedTabIndex,
                containerColor = ValkuSurface,
                contentColor = ValkuPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[feedTabIndex]),
                        color = Color(0xFFFF6D00),
                        height = 2.5.dp
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = feedTabIndex == index,
                        onClick = { feedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (feedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (feedTabIndex == index) Color(0xFFFF6D00) else ValkuTextSecondary
                            )
                        }
                    )
                }
            }

            if (feedTabIndex == 0) {
                // Social Posts Feed
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Stories Tray
                    item {
                        StoriesPreviewRow(
                            stories = stories,
                            onStoryClick = { index -> viewModel.openStoryViewer(index) },
                            onAddStory = { viewModel.createStory("New feed update from Valku super-app ✨") }
                        )
                    }

                    // "What's on your mind" Box
                    item {
                        CreatePostTriggerCard(
                            onClick = { showCreatePostModal = true }
                        )
                    }

                    // Posts List
                    items(posts, key = { it.id }) { post ->
                        SocialPostCard(
                            post = post,
                            onLike = { viewModel.toggleLikePost(post.id) },
                            onSave = { viewModel.toggleSavePost(post.id) },
                            onComment = { activeCommentPost = post },
                            onShare = { viewModel.shareInviteLink(context) }
                        )
                    }
                }
            } else {
                // Friends and Requests Management
                FriendsAndRequestsView(
                    friends = friends,
                    onToggleFriend = { friendId -> viewModel.toggleFriendStatus(friendId) },
                    onInviteContacts = { viewModel.shareInviteLink(context) }
                )
            }
        }

        // Create Post Dialog
        if (showCreatePostModal) {
            CreatePostModal(
                onDismiss = { showCreatePostModal = false },
                onCreate = { content, loc ->
                    viewModel.createPost(content, loc)
                    showCreatePostModal = false
                }
            )
        }

        // Comments Bottom Sheet
        if (activeCommentPost != null) {
            ModalBottomSheet(
                onDismissRequest = { activeCommentPost = null },
                sheetState = sheetState,
                containerColor = ValkuSurface
            ) {
                CommentsBottomSheetContent(
                    post = activeCommentPost!!,
                    onAddComment = { text ->
                        // Post comment simulation
                    },
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            activeCommentPost = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CreatePostTriggerCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .testTag("create_post_trigger"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B2154)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("YOU", fontWeight = FontWeight.Bold, color = ValkuPrimary, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ValkuSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "What's on your mind? Share with Valku community...",
                        color = ValkuTextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Photo/Video",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Photo/Video", fontSize = 12.sp, color = ValkuTextSecondary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Check In", fontSize = 12.sp, color = ValkuTextSecondary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Spark", fontSize = 12.sp, color = ValkuPrimary)
                }
            }
        }
    }
}

@Composable
fun SocialPostCard(
    post: SocialPost,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit
) {
    val heartScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart_bounce"
    )

    val dateFormat = SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(post.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, ValkuCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Post Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF38105B), Color(0xFF0F3B59)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(2).uppercase(),
                            color = ValkuPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ValkuTextPrimary
                            )
                            if (post.authorHandle.contains("valku", ignoreCase = true)) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ValkuPrimary)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("VERIFIED", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                }
                            }
                        }
                        Text(
                            text = "${post.authorHandle} • ${post.location} • $dateStr",
                            fontSize = 11.sp,
                            color = ValkuTextMuted
                        )
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = ValkuTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text Content
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = ValkuTextPrimary,
                lineHeight = 20.sp
            )

            // Post Media Preview (Hero Banner or AI Graphic)
            if (post.mediaUrl == "banner") {
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.valku_hero_banner),
                    contentDescription = "Post Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (post.mediaUrl == "ai_avatar") {
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.valku_ai_avatar),
                    contentDescription = "AI Art",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Count Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "❤️ ${post.likesCount} likes",
                    fontSize = 12.sp,
                    color = ValkuTextSecondary
                )
                Text(
                    text = "${post.commentsCount} comments • ${post.sharesCount} shares",
                    fontSize = 12.sp,
                    color = ValkuTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ValkuCardBorder)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons (Like, Comment, Share, Save)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onLike)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFFF3366) else ValkuTextMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(heartScale)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Like",
                        fontSize = 13.sp,
                        fontWeight = if (post.isLiked) FontWeight.Bold else FontWeight.Normal,
                        color = if (post.isLiked) Color(0xFFFF3366) else ValkuTextSecondary
                    )
                }

                // Comment Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onComment)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = ValkuTextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Comment", fontSize = 13.sp, color = ValkuTextSecondary)
                }

                // Share Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onShare)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = ValkuTextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 13.sp, color = ValkuTextSecondary)
                }

                // Save Bookmark
                IconButton(onClick = onSave, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) ValkuPrimary else ValkuTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FriendsAndRequestsView(
    friends: List<FriendContact>,
    onToggleFriend: (String) -> Unit,
    onInviteContacts: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            // Invite Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1A3A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ValkuPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✨ Grow your Valku network!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ValkuPrimary
                        )
                        Text(
                            text = "Invite friends to chat, video call and share reels together.",
                            fontSize = 12.sp,
                            color = ValkuTextSecondary
                        )
                    }
                    Button(
                        onClick = onInviteContacts,
                        colors = ButtonDefaults.buttonColors(containerColor = ValkuPrimary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Invite", color = Color(0xFF090814), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "People You May Know & Friends",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        items(friends) { friend ->
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF28234D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = friend.name.take(2).uppercase(),
                            color = ValkuPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = friend.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ValkuTextPrimary
                        )
                        Text(
                            text = "${friend.handle} • ${friend.mutualFriends} mutual friends",
                            fontSize = 11.sp,
                            color = ValkuTextMuted
                        )
                        Text(
                            text = friend.statusText,
                            fontSize = 11.sp,
                            color = ValkuTextSecondary,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (friend.isFriend) {
                        Button(
                            onClick = { onToggleFriend(friend.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252147)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Friends ✓", fontSize = 12.sp, color = Color(0xFF00E676))
                        }
                    } else if (friend.isPendingRequest) {
                        Button(
                            onClick = { onToggleFriend(friend.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Confirm", fontSize = 12.sp, color = Color(0xFF090814), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onToggleFriend(friend.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = ValkuPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Add Friend", fontSize = 12.sp, color = Color(0xFF090814), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePostModal(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var postText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Ahmedabad, Gujarat") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Text("Create Community Post", fontWeight = FontWeight.Bold, color = ValkuTextPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { Text("What do you want to share?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
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
                onClick = { onCreate(postText, location) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
            ) {
                Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}

@Composable
fun CommentsBottomSheetContent(
    post: SocialPost,
    onAddComment: (String) -> Unit,
    onClose: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    var localComments by remember { mutableStateOf(post.comments) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments (${localComments.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = ValkuTextMuted)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Existing comments
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            localComments.forEach { comment ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF28234D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(comment.authorName.take(2).uppercase(), color = ValkuPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ValkuSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(comment.authorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ValkuTextPrimary)
                            Text(comment.timeAgo, fontSize = 10.sp, color = ValkuTextMuted)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(comment.text, fontSize = 13.sp, color = ValkuTextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Comment Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("Write a comment...", color = ValkuTextMuted) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ValkuTextPrimary,
                    unfocusedTextColor = ValkuTextPrimary,
                    focusedContainerColor = ValkuSurfaceVariant,
                    unfocusedContainerColor = ValkuSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        val newC = PostComment(
                            id = "c_${System.currentTimeMillis()}",
                            authorName = "You",
                            authorAvatar = "",
                            text = newCommentText,
                            timeAgo = "Just now"
                        )
                        localComments = localComments + newC
                        onAddComment(newCommentText)
                        newCommentText = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6D00))
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Comment", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
