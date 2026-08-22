package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.ChatThread
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
import com.example.ui.viewmodel.CallType
import com.example.ui.viewmodel.ValkuViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatsScreen(
    viewModel: ValkuViewModel,
    modifier: Modifier = Modifier
) {
    val threads by viewModel.chatThreads.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val activeThread by viewModel.activeThread.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()

    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf("All Chats", "Unread", "Calls", "Groups")
    var showNewChatDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(ValkuBackground)) {
        if (activeThread != null) {
            // Full 1-on-1 Chat Screen
            ChatConversationView(
                thread = activeThread!!,
                messages = messages,
                onBack = { viewModel.closeChat() },
                onSendMessage = { text, type, duration ->
                    viewModel.sendMessage(text, type, duration)
                },
                onSendAttachment = { type, text, name, size, cName, cPhone, mediaUrl ->
                    viewModel.sendAttachment(type, text, name, size, cName, cPhone, mediaUrl)
                },
                onAudioCall = {
                    viewModel.startCall(activeThread!!.name, activeThread!!.handle, CallType.AUDIO)
                },
                onVideoCall = {
                    viewModel.startCall(activeThread!!.name, activeThread!!.handle, CallType.VIDEO)
                },
                onReact = { msgId, emoji ->
                    viewModel.reactToMessage(msgId, emoji)
                }
            )
        } else {
            // Chat List & Stories View
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Stories Row
                StoriesPreviewRow(
                    stories = stories,
                    onStoryClick = { index -> viewModel.openStoryViewer(index) },
                    onAddStory = { viewModel.createStory("New daily update from Valku Sarvaiya ✨") }
                )

                // WhatsApp Filter Tabs
                TabRow(
                    selectedTabIndex = selectedFilterIndex,
                    containerColor = ValkuSurface,
                    contentColor = ValkuPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedFilterIndex]),
                            color = ValkuPrimary,
                            height = 2.5.dp
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filters.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFilterIndex == index) ValkuPrimary else ValkuTextSecondary
                                )
                            }
                        )
                    }
                }

                // Chat Threads List
                val filteredThreads = when (selectedFilterIndex) {
                    1 -> threads.filter { it.unreadCount > 0 }
                    3 -> threads.filter { it.name.contains("Group", ignoreCase = true) }
                    else -> threads
                }

                if (selectedFilterIndex == 2) {
                    // Call Logs View
                    CallLogsView(
                        threads = threads,
                        onAudioCall = { name, handle -> viewModel.startCall(name, handle, CallType.AUDIO) },
                        onVideoCall = { name, handle -> viewModel.startCall(name, handle, CallType.VIDEO) }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredThreads, key = { it.id }) { thread ->
                            ChatThreadItem(
                                thread = thread,
                                onClick = { viewModel.openChat(thread) },
                                onStoryClick = { viewModel.openStoryViewer(0) }
                            )
                        }
                    }
                }
            }

            // Floating Action Button to start new message
            FloatingActionButton(
                onClick = { showNewChatDialog = true },
                containerColor = Color(0xFF00E676),
                contentColor = Color(0xFF090814),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 80.dp)
                    .testTag("new_chat_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Chat")
            }
        }

        // New Chat Dialog
        if (showNewChatDialog) {
            NewChatModal(
                onDismiss = { showNewChatDialog = false },
                onStartChat = { name, message ->
                    showNewChatDialog = false
                    val thread = threads.find { it.name.contains(name, ignoreCase = true) } ?: threads.firstOrNull()
                    if (thread != null) {
                        viewModel.openChat(thread)
                        if (message.isNotBlank()) {
                            viewModel.sendMessage(message)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun StoriesPreviewRow(
    stories: List<com.example.data.model.StoryItem>,
    onStoryClick: (Int) -> Unit,
    onAddStory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ValkuSurface)
            .padding(vertical = 12.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Add Your Story Item with Sunset Gradient Ring
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(onClick = onAddStory)
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFACC15), Color(0xFFEF4444), Color(0xFFA855F7))
                                )
                            )
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF05070A))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Status",
                                    tint = Color(0xFF60A5FA),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Status",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = ValkuTextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Friend Stories with Radiant Cyan / Blue Ring
            itemsIndexed(stories) { index, story ->
                val storyGradient = Brush.linearGradient(
                    listOf(Color(0xFF60A5FA), Color(0xFF22D3EE), Color(0xFFA855F7))
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onStoryClick(index) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(storyGradient)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF05070A))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.userName.take(2).uppercase(),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF60A5FA)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = story.userName.split(" ").firstOrNull() ?: story.userName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = ValkuTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(62.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ChatThreadItem(
    thread: ChatThread,
    onClick: () -> Unit,
    onStoryClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeStr = timeFormat.format(Date(thread.lastTimestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Story ring if available
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (thread.hasStory) Brush.sweepGradient(
                            listOf(ValkuPrimary, ValkuSecondary, ValkuTertiary, ValkuPrimary)
                        ) else Brush.linearGradient(listOf(Color(0xFF28234D), Color(0xFF1B1736)))
                    )
                    .clickable(onClick = { if (thread.hasStory) onStoryClick() else onClick() })
                    .padding(if (thread.hasStory) 2.dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF1B1833)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = thread.name.take(2).uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ValkuPrimary
                    )
                }
            }

            // Online green badge
            if (thread.isOnline) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                        .border(2.dp, ValkuBackground, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and Last Message
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = thread.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ValkuTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timeStr,
                    fontSize = 11.sp,
                    color = if (thread.unreadCount > 0) Color(0xFF00E676) else ValkuTextMuted
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Read",
                        tint = if (thread.unreadCount == 0) ValkuPrimary else ValkuTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = thread.lastMessage,
                        fontSize = 13.sp,
                        color = ValkuTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (thread.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thread.unreadCount.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF090814)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CallLogsView(
    threads: List<ChatThread>,
    onAudioCall: (String, String) -> Unit,
    onVideoCall: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = "Recent Calls",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ValkuTextMuted,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(threads) { thread ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF221E42)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = thread.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = ValkuPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = thread.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ValkuTextPrimary
                    )
                    Text(
                        text = "Incoming • Today, 3:15 PM",
                        fontSize = 12.sp,
                        color = Color(0xFF00E676)
                    )
                }

                IconButton(onClick = { onAudioCall(thread.name, thread.handle) }) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { onVideoCall(thread.name, thread.handle) }) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = ValkuPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationView(
    thread: ChatThread,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String, String, Int) -> Unit,
    onSendAttachment: (type: String, text: String, name: String, size: String, contactName: String, contactPhone: String, mediaUrl: String) -> Unit,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit,
    onReact: (String, String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceSeconds by remember { mutableIntStateOf(0) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var activeAttachmentDialog by remember { mutableStateOf<String?>(null) } // "document", "photo", "audio", "video", "contact", "location"
    val context = LocalContext.current

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            voiceSeconds = 0
            while (isRecordingVoice) {
                delay(1000)
                voiceSeconds++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ValkuBackground)
            .testTag("chat_conversation_view")
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ValkuSurface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ValkuTextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF221E42)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = thread.name.take(2).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ValkuPrimary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thread.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ValkuTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (thread.isOnline) "online" else "last seen recently",
                    fontSize = 11.sp,
                    color = if (thread.isOnline) Color(0xFF00E676) else ValkuTextMuted
                )
            }

            // Video Call & Audio Call
            IconButton(onClick = onVideoCall, modifier = Modifier.testTag("chat_video_call_btn")) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Call",
                    tint = ValkuPrimary
                )
            }

            IconButton(onClick = onAudioCall, modifier = Modifier.testTag("chat_audio_call_btn")) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Audio Call",
                    tint = Color(0xFF00E676)
                )
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            reverseLayout = false
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    onReact = { emoji -> onReact(message.id, emoji) },
                    onCallContact = { phone ->
                        onAudioCall()
                    }
                )
            }
        }

        // Voice Recording Indicator Bar
        AnimatedVisibility(visible = isRecordingVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2C0B4E))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recording Voice Note... 0:${String.format("%02d", voiceSeconds)}",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
                TextButton(onClick = { isRecordingVoice = false }) {
                    Text("Cancel", color = Color(0xFFFF5252))
                }
            }
        }

        // Input Bottom Bar with Attachments
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ValkuSurface)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment Button
            IconButton(
                onClick = { showAttachmentSheet = true },
                modifier = Modifier.testTag("chat_attach_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach File",
                    tint = ValkuPrimary
                )
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Message...", color = ValkuTextMuted, fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_message_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ValkuSurfaceVariant,
                    unfocusedContainerColor = ValkuSurfaceVariant,
                    focusedBorderColor = ValkuPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = ValkuTextPrimary,
                    unfocusedTextColor = ValkuTextPrimary
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(6.dp))

            if (textInput.isNotBlank()) {
                IconButton(
                    onClick = {
                        onSendMessage(textInput, "text", 0)
                        textInput = ""
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF090814),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (!isRecordingVoice) {
                            isRecordingVoice = true
                        } else {
                            isRecordingVoice = false
                            onSendMessage("Voice note (${voiceSeconds}s) 🎙️", "voice", voiceSeconds)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isRecordingVoice) Color.Red else Color(0xFF00E676))
                        .testTag("chat_voice_record_button")
                ) {
                    Icon(
                        imageVector = if (isRecordingVoice) Icons.Default.Check else Icons.Default.Mic,
                        contentDescription = "Record Voice",
                        tint = if (isRecordingVoice) Color.White else Color(0xFF090814),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Attachment Options Bottom Sheet (documents, photo, audio, video, contact number, share/location)
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false },
            containerColor = ValkuSurface,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Share & Attach Content / शेयर और अटैचमेंट",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ValkuTextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 2 Rows of 3 tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentTile(
                        icon = Icons.Default.Description,
                        label = "Document\nदस्तावेज़",
                        gradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                        onClick = {
                            showAttachmentSheet = false
                            activeAttachmentDialog = "document"
                        }
                    )
                    AttachmentTile(
                        icon = Icons.Default.Image,
                        label = "Photo\nफ़ोटो / गैलरी",
                        gradient = listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
                        onClick = {
                            showAttachmentSheet = false
                            activeAttachmentDialog = "photo"
                        }
                    )
                    AttachmentTile(
                        icon = Icons.Default.Audiotrack,
                        label = "Audio\nऑडियो / संगीत",
                        gradient = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
                        onClick = {
                            showAttachmentSheet = false
                            activeAttachmentDialog = "audio"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentTile(
                        icon = Icons.Default.Videocam,
                        label = "Video\nवीडियो",
                        gradient = listOf(Color(0xFFF97316), Color(0xFFEA580C)),
                        onClick = {
                            showAttachmentSheet = false
                            activeAttachmentDialog = "video"
                        }
                    )
                    AttachmentTile(
                        icon = Icons.Default.ContactPhone,
                        label = "Contact\nनंबर शेयर",
                        gradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                        onClick = {
                            showAttachmentSheet = false
                            activeAttachmentDialog = "contact"
                        }
                    )
                    AttachmentTile(
                        icon = Icons.Default.LocationOn,
                        label = "Location\nलाइव लोकेशन",
                        gradient = listOf(Color(0xFF10B981), Color(0xFF059669)),
                        onClick = {
                            showAttachmentSheet = false
                            activeAttachmentDialog = "location"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Document Picker Dialog
    if (activeAttachmentDialog == "document") {
        AttachmentDocumentDialog(
            onDismiss = { activeAttachmentDialog = null },
            onSend = { docName, size ->
                activeAttachmentDialog = null
                onSendAttachment("document", "", docName, size, "", "", "")
            }
        )
    }

    // Photo Picker Dialog
    if (activeAttachmentDialog == "photo") {
        AttachmentPhotoDialog(
            onDismiss = { activeAttachmentDialog = null },
            onSend = { photoTitle, caption ->
                activeAttachmentDialog = null
                onSendAttachment("image", caption, photoTitle, "1.8 MB", "", "", "")
            }
        )
    }

    // Audio Picker Dialog
    if (activeAttachmentDialog == "audio") {
        AttachmentAudioDialog(
            onDismiss = { activeAttachmentDialog = null },
            onSend = { trackName, size ->
                activeAttachmentDialog = null
                onSendAttachment("audio", "", trackName, size, "", "", "")
            }
        )
    }

    // Video Picker Dialog
    if (activeAttachmentDialog == "video") {
        AttachmentVideoDialog(
            onDismiss = { activeAttachmentDialog = null },
            onSend = { videoName, size ->
                activeAttachmentDialog = null
                onSendAttachment("video", "", videoName, size, "", "", "")
            }
        )
    }

    // Contact Number Share Dialog
    if (activeAttachmentDialog == "contact") {
        AttachmentContactDialog(
            onDismiss = { activeAttachmentDialog = null },
            onSend = { name, phone ->
                activeAttachmentDialog = null
                onSendAttachment("contact", "", "", "", name, phone, "")
            }
        )
    }

    // Location Share Dialog
    if (activeAttachmentDialog == "location") {
        AttachmentLocationDialog(
            onDismiss = { activeAttachmentDialog = null },
            onSend = { locationName ->
                activeAttachmentDialog = null
                onSendAttachment("location", "", locationName, "GPS", "", "", "")
            }
        )
    }
}

@Composable
fun AttachmentTile(
    icon: ImageVector,
    label: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ValkuTextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

// 📄 Document Picker Dialog
@Composable
fun AttachmentDocumentDialog(
    onDismiss: () -> Unit,
    onSend: (name: String, size: String) -> Unit
) {
    val sampleDocs = listOf(
        Pair("Valku_Sarvaiya_Project_Brief.pdf", "2.8 MB"),
        Pair("Quarterly_Growth_Report_2026.docx", "1.4 MB"),
        Pair("SuperApp_Architecture_Diagram.pdf", "4.1 MB"),
        Pair("Social_Hub_Financial_Budget.xlsx", "860 KB")
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    var customDocName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF8B5CF6))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Document / दस्तावेज़", color = ValkuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                Text("Select a document to send:", fontSize = 12.sp, color = ValkuTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                sampleDocs.forEachIndexed { index, doc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedIndex = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedIndex == index) Color(0xFF2E1065) else ValkuSurfaceVariant
                        ),
                        border = if (selectedIndex == index) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFC084FC)))) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.first, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ValkuTextPrimary, maxLines = 1)
                                Text(doc.second, fontSize = 10.sp, color = ValkuTextMuted)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val doc = sampleDocs[selectedIndex]
                    onSend(doc.first, doc.second)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
            ) {
                Text("Send Document", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}

// 📸 Photo Picker Dialog
@Composable
fun AttachmentPhotoDialog(
    onDismiss: () -> Unit,
    onSend: (title: String, caption: String) -> Unit
) {
    val samplePhotos = listOf(
        "Sunset_Mumbai_Skyline_4K.jpg",
        "Studio_Portrait_Shoot_HD.jpg",
        "Valku_Sarvaiya_Event_2026.png",
        "Cyber_Neon_Aesthetic.jpg"
    )
    var selectedPhoto by remember { mutableIntStateOf(0) }
    var captionText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFEC4899))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Photo / फ़ोटो", color = ValkuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                Text("Choose photo from Gallery:", fontSize = 12.sp, color = ValkuTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                samplePhotos.forEachIndexed { index, photo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedPhoto = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPhoto == index) Color(0xFF500724) else ValkuSurfaceVariant
                        ),
                        border = if (selectedPhoto == index) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFF472B6)))) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFEC4899), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(photo, fontSize = 12.sp, color = ValkuTextPrimary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Add a caption (optional)") },
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
                onClick = {
                    onSend(samplePhotos[selectedPhoto], captionText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
            ) {
                Text("Send Photo", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}

// 🎵 Audio Picker Dialog
@Composable
fun AttachmentAudioDialog(
    onDismiss: () -> Unit,
    onSend: (name: String, size: String) -> Unit
) {
    val sampleAudio = listOf(
        Pair("Valku_Cyber_Melody.mp3", "3:45 • 5.2 MB"),
        Pair("LoFi_Midnight_Chill_Beat.mp3", "2:30 • 3.4 MB"),
        Pair("Studio_Podcast_Episode_12.mp3", "14:10 • 19.8 MB"),
        Pair("Ambient_Synthesizer_Loop.mp3", "1:55 • 2.8 MB")
    )
    var selectedAudio by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFF06B6D4))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Audio Track / ऑडियो", color = ValkuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                Text("Select an audio track to share:", fontSize = 12.sp, color = ValkuTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                sampleAudio.forEachIndexed { index, audio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedAudio = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedAudio == index) Color(0xFF083344) else ValkuSurfaceVariant
                        ),
                        border = if (selectedAudio == index) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF67E8F9)))) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(audio.first, fontSize = 12.sp, color = ValkuTextPrimary, fontWeight = FontWeight.Medium)
                                Text(audio.second, fontSize = 10.sp, color = ValkuTextMuted)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val a = sampleAudio[selectedAudio]
                    onSend(a.first, a.second)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
            ) {
                Text("Send Audio", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}

// 🎥 Video Picker Dialog
@Composable
fun AttachmentVideoDialog(
    onDismiss: () -> Unit,
    onSend: (name: String, size: String) -> Unit
) {
    val sampleVideos = listOf(
        Pair("Valku_Sarvaiya_Trailer_4K.mp4", "01:30 • 18.4 MB"),
        Pair("Drone_City_Tour_Mumbai.mp4", "02:15 • 29.1 MB"),
        Pair("AI_SuperApp_Feature_Demo.mp4", "00:45 • 9.6 MB")
    )
    var selectedVideo by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFF97316))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Video / वीडियो", color = ValkuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                Text("Select video to attach:", fontSize = 12.sp, color = ValkuTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                sampleVideos.forEachIndexed { index, vid ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedVideo = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedVideo == index) Color(0xFF431407) else ValkuSurfaceVariant
                        ),
                        border = if (selectedVideo == index) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFFDBA74)))) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(vid.first, fontSize = 12.sp, color = ValkuTextPrimary, fontWeight = FontWeight.Medium)
                                Text(vid.second, fontSize = 10.sp, color = ValkuTextMuted)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = sampleVideos[selectedVideo]
                    onSend(v.first, v.second)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
            ) {
                Text("Send Video", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}

// 👤 Contact Number Share Dialog
@Composable
fun AttachmentContactDialog(
    onDismiss: () -> Unit,
    onSend: (name: String, phone: String) -> Unit
) {
    val quickContacts = listOf(
        Pair("Shailesh Sarvaiya", "+91 98765 43210"),
        Pair("Valku Sarvaiya (Official)", "+91 99887 76655"),
        Pair("Neha Sharma", "+91 98234 56789"),
        Pair("Rahul Verma", "+91 97123 45678")
    )
    var selectedQuick by remember { mutableIntStateOf(0) }
    var contactName by remember { mutableStateOf(quickContacts[0].first) }
    var contactPhone by remember { mutableStateOf(quickContacts[0].second) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ContactPhone, contentDescription = null, tint = Color(0xFF3B82F6))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Contact Number / नंबर शेयर", color = ValkuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                Text("Choose from Address Book or enter custom:", fontSize = 12.sp, color = ValkuTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickContacts.forEachIndexed { index, c ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedQuick == index) Color(0xFF1E3A8A) else ValkuSurfaceVariant)
                                .clickable {
                                    selectedQuick = index
                                    contactName = c.first
                                    contactPhone = c.second
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c.first.substringBefore(" "), fontSize = 11.sp, color = if (selectedQuick == index) Color.White else ValkuTextSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Contact Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text("Phone Number") },
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
                onClick = {
                    onSend(contactName, contactPhone)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text("Share Contact", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}

// 📍 Location Share Dialog
@Composable
fun AttachmentLocationDialog(
    onDismiss: () -> Unit,
    onSend: (location: String) -> Unit
) {
    val sampleLocations = listOf(
        "Current GPS Location (Bandra Kurla Complex, Mumbai 📍)",
        "Valku Sarvaiya Headquarters (Cyber Hub, Gujarat 🏢)",
        "Nariman Point, Marine Drive, Mumbai 🌊",
        "Connaught Place, New Delhi 🏛️"
    )
    var selectedLocation by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF10B981))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Location / लोकेशन शेयर", color = ValkuTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                Text("Share your live location or landmark:", fontSize = 12.sp, color = ValkuTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                sampleLocations.forEachIndexed { index, loc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedLocation = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedLocation == index) Color(0xFF064E3B) else ValkuSurfaceVariant
                        ),
                        border = if (selectedLocation == index) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF6EE7B7)))) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(loc, fontSize = 12.sp, color = ValkuTextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSend(sampleLocations[selectedLocation])
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Share Live Location", color = Color(0xFF090814), fontWeight = FontWeight.Bold)
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
fun MessageBubble(
    message: ChatMessage,
    onReact: (String) -> Unit,
    onCallContact: (phone: String) -> Unit = {}
) {
    val isMe = message.isMe
    var showReactionMenu by remember { mutableStateOf(false) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val bubbleColor = if (isMe) {
        Color(0xFF005C4B) // WhatsApp dark green
    } else {
        Color(0xFF201E38)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .clickable { showReactionMenu = !showReactionMenu }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                when (message.messageType) {
                    "document" -> {
                        // 📄 Document Card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF131126))
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF8B5CF6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = "Document", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.width(160.dp)) {
                                Text(
                                    text = if (message.attachmentName.isNotBlank()) message.attachmentName else message.text.substringAfter("📄 "),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (message.attachmentSize.isNotBlank()) message.attachmentSize else "PDF Document",
                                    fontSize = 11.sp,
                                    color = Color(0xFFA78BFA)
                                )
                            }
                            IconButton(
                                onClick = {
                                    android.widget.Toast.makeText(context, "Opening document...", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Download", tint = ValkuPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    "image" -> {
                        // 📸 Photo Card
                        Column {
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Image, contentDescription = "Photo", tint = Color.White, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (message.attachmentName.isNotBlank()) message.attachmentName else "Photo Attachment",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            if (message.text.isNotBlank() && !message.text.startsWith("📷 Photo")) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = message.text, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }

                    "audio" -> {
                        // 🎵 Audio Track Card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(8.dp)
                        ) {
                            IconButton(
                                onClick = { isAudioPlaying = !isAudioPlaying },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF06B6D4))
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Audio",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.width(150.dp)) {
                                Text(
                                    text = if (message.attachmentName.isNotBlank()) message.attachmentName else message.text.substringAfter("🎵 "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isAudioPlaying) "Playing... 🎵 01:24" else (if (message.attachmentSize.isNotBlank()) message.attachmentSize else "Audio Track"),
                                    fontSize = 10.sp,
                                    color = Color(0xFF67E8F9)
                                )
                            }
                        }
                    }

                    "video" -> {
                        // 🎥 Video Card
                        Column {
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEA580C), Color(0xFF7C2D12))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x80000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Video", tint = Color.White, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (message.attachmentName.isNotBlank()) message.attachmentName else "Video Clip",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            if (message.text.isNotBlank() && !message.text.startsWith("🎥 Video")) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = message.text, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }

                    "contact" -> {
                        // 👤 Contact Card with Call & Message actions
                        val cName = if (message.contactName.isNotBlank()) message.contactName else "Contact"
                        val cPhone = if (message.contactPhone.isNotBlank()) message.contactPhone else message.text.substringAfter("• ")
                        Card(
                            modifier = Modifier.width(210.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF172554)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3B82F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(cName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(cName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                        Text(cPhone, fontSize = 11.sp, color = Color(0xFF93C5FD))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color(0xFF1E3A8A))
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(
                                        onClick = {
                                            android.widget.Toast.makeText(context, "Messaging $cName...", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Message", fontSize = 11.sp, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onCallContact(cPhone) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Call", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "location" -> {
                        // 📍 Location Card
                        Column(modifier = Modifier.width(200.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Location", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.text.replace("📍 Location: ", ""),
                                fontSize = 12.sp,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }

                    "voice" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ValkuPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play voice note",
                                    tint = Color(0xFF090814),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Voice Message",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "0:${String.format("%02d", message.voiceDurationSeconds)} • 🎙️",
                                    fontSize = 11.sp,
                                    color = ValkuTextSecondary
                                )
                            }
                        }
                    }

                    "call_log" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Log",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = ValkuTextPrimary
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                if (message.reaction.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF141226))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = message.reaction, fontSize = 12.sp)
                    }
                }
            }
        }

        // Quick Reaction Toolbar
        if (showReactionMenu) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1A1733))
                    .border(1.dp, ValkuCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("❤️", "🔥", "👍", "😂", "😮", "⚡").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                onReact(emoji)
                                showReactionMenu = false
                            }
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewChatModal(
    onDismiss: () -> Unit,
    onStartChat: (String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var initialMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ValkuSurface,
        title = {
            Text(
                text = "Start New Chat",
                fontWeight = FontWeight.Bold,
                color = ValkuTextPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Contact Name or Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ValkuTextPrimary,
                        unfocusedTextColor = ValkuTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = initialMsg,
                    onValueChange = { initialMsg = it },
                    label = { Text("Initial Message (Optional)") },
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
                onClick = { onStartChat(nameInput, initialMsg) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
            ) {
                Text("Chat", color = Color(0xFF090814), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ValkuTextMuted)
            }
        }
    )
}
