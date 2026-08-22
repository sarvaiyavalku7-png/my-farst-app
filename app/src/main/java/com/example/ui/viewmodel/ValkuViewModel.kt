package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.ai.GeminiRepository
import com.example.data.db.ChatMessageEntity
import com.example.data.db.ChatThreadEntity
import com.example.data.db.GameScoreEntity
import com.example.data.db.SocialPostEntity
import com.example.data.db.StoryEntity
import com.example.data.db.ValkuDatabase
import com.example.data.model.AiMessage
import com.example.data.model.ChatMessage
import com.example.data.model.ChatThread
import com.example.data.model.FriendContact
import com.example.data.model.LongVideoItem
import com.example.data.model.MiniGameItem
import com.example.data.model.PostComment
import com.example.data.model.ReelItem
import com.example.data.model.SocialPost
import com.example.data.model.StoryItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab {
    CHATS, FEED, REELS, AI, ARCADE, WEB_PORTAL
}

enum class CallType {
    AUDIO, VIDEO
}

data class AppSettings(
    val darkTheme: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val callRingtone: String = "Valku Cyber Wave",
    val chatWallpaper: String = "Obsidian Slate Glow",
    val autoDownloadMedia: Boolean = true,
    val geminiModel: String = "Gemini 3.5 Pro",
    val language: String = "English", // "English", "Hindi (हिंदी)", "Gujarati (ગુજરાતી)"
    val appLockEnabled: Boolean = false,
    val readReceipts: Boolean = true,
    val activeSoundEffects: Boolean = true
)

data class ActiveCallState(
    val isActive: Boolean = false,
    val contactName: String = "",
    val contactHandle: String = "",
    val callType: CallType = CallType.VIDEO,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isFrontCamera: Boolean = true
)

data class ActiveStoryState(
    val isOpen: Boolean = false,
    val currentIndex: Int = 0,
    val stories: List<StoryItem> = emptyList()
)

class ValkuViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ValkuDatabase.getDatabase(application, viewModelScope)
    private val dao = database.valkuDao()
    private val geminiRepo = GeminiRepository()

    // Current User Profile & Authentication State
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "u_valku_user",
            name = "Valku Sarvaiya",
            handle = "@valkusarvaiya",
            phoneNumber = "+91 98765 43210",
            email = "valku.creator@gmail.com",
            bio = "Creator & Explorer on Valku Sarvaiya Super App 🚀",
            avatarUrl = "",
            isLoggedIn = true,
            loginMethod = "Phone"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Settings Modal State
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Login & Profile Modal State
    private val _isAuthModalOpen = MutableStateFlow(false)
    val isAuthModalOpen: StateFlow<Boolean> = _isAuthModalOpen.asStateFlow()

    // App Preferences
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun openSettings() { _isSettingsOpen.value = true }
    fun closeSettings() { _isSettingsOpen.value = false }

    fun openAuthModal() { _isAuthModalOpen.value = true }
    fun closeAuthModal() { _isAuthModalOpen.value = false }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        _settings.value = transform(_settings.value)
    }

    fun loginWithPhone(phone: String, name: String = "Valku User") {
        _currentUser.value = _currentUser.value.copy(
            phoneNumber = phone,
            name = if (name.isNotBlank()) name else "Valku User",
            handle = "@" + (if (name.isNotBlank()) name.lowercase().replace(" ", "") else "user" + (1000..9999).random()),
            isLoggedIn = true,
            loginMethod = "Phone"
        )
        _isAuthModalOpen.value = false
    }

    fun loginWithGoogle(email: String, name: String) {
        _currentUser.value = _currentUser.value.copy(
            email = email,
            name = name,
            handle = "@" + name.lowercase().replace(" ", "").replace(".", ""),
            isLoggedIn = true,
            loginMethod = "Google"
        )
        _isAuthModalOpen.value = false
    }

    fun loginWithEmail(email: String, name: String) {
        _currentUser.value = _currentUser.value.copy(
            email = email,
            name = if (name.isNotBlank()) name else email.substringBefore("@"),
            handle = "@" + (if (name.isNotBlank()) name.lowercase().replace(" ", "") else email.substringBefore("@")),
            isLoggedIn = true,
            loginMethod = "Email"
        )
        _isAuthModalOpen.value = false
    }

    fun updateProfile(name: String, handle: String, bio: String, phone: String, email: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            handle = if (handle.startsWith("@")) handle else "@$handle",
            bio = bio,
            phoneNumber = phone,
            email = email
        )
    }

    fun logout() {
        _currentUser.value = _currentUser.value.copy(
            isLoggedIn = false,
            name = "Guest User",
            handle = "@guest",
            phoneNumber = "",
            email = "",
            loginMethod = "Guest"
        )
    }

    // Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.CHATS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun switchTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectTab(tab: AppTab) = switchTab(tab)

    // Active Chat Screen State
    private val _activeThread = MutableStateFlow<ChatThread?>(null)
    val activeThread: StateFlow<ChatThread?> = _activeThread.asStateFlow()

    val chatThreads: StateFlow<List<ChatThread>> = dao.getAllThreads().map { entities ->
        entities.map {
            ChatThread(
                id = it.id,
                name = it.name,
                handle = it.handle,
                avatarUrl = it.avatarUrl,
                lastMessage = it.lastMessage,
                lastTimestamp = it.lastTimestamp,
                unreadCount = it.unreadCount,
                isOnline = it.isOnline,
                userBio = it.userBio,
                phoneNumber = it.phoneNumber,
                hasStory = it.hasStory
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessage>> = _currentMessages.asStateFlow()

    // Social Feed
    val posts: StateFlow<List<SocialPost>> = dao.getAllPosts().map { entities ->
        entities.map {
            SocialPost(
                id = it.id,
                authorName = it.authorName,
                authorHandle = it.authorHandle,
                authorAvatar = it.authorAvatar,
                content = it.content,
                mediaUrl = it.mediaUrl,
                mediaType = it.mediaType,
                likesCount = it.likesCount,
                commentsCount = it.commentsCount,
                sharesCount = it.sharesCount,
                isLiked = it.isLiked,
                isSaved = it.isSaved,
                timestamp = it.timestamp,
                location = it.location,
                comments = listOf(
                    PostComment("c1", "Priya Sharma", "", "Awesome super-app! Design is so cool 🔥", "5m ago"),
                    PostComment("c2", "Rahul Varma", "", "Gemini AI response speed is mindblowing ⚡", "12m ago")
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stories
    val stories: StateFlow<List<StoryItem>> = dao.getAllStories().map { entities ->
        entities.map {
            StoryItem(
                id = it.id,
                userId = it.userId,
                userName = it.userName,
                userAvatar = it.userAvatar,
                mediaUrl = it.mediaUrl,
                caption = it.caption,
                timestamp = it.timestamp,
                isViewed = it.isViewed,
                backgroundColorHex = it.backgroundColorHex
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Story Viewer State
    private val _storyState = MutableStateFlow(ActiveStoryState())
    val storyState: StateFlow<ActiveStoryState> = _storyState.asStateFlow()
    val activeStoryState: StateFlow<ActiveStoryState> = _storyState.asStateFlow()

    // Active Call State
    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()
    val callState: StateFlow<ActiveCallState> = _activeCall.map { it ?: ActiveCallState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActiveCallState())

    // Reels Feed
    private val _reels = MutableStateFlow<List<ReelItem>>(
        listOf(
            ReelItem(
                id = "reel_1",
                authorName = "Valku Sarvaiya",
                authorHandle = "@valkusarvaiya",
                authorAvatar = "",
                title = "Valku Sarvaiya Super App 🚀",
                description = "All social media in one futuristic place! Chat, HD Calls, Reels, Games & Gemini AI ⚡ #ValkuApp #TechRevolution #NextGen",
                audioTrackName = "Valku Cyber Anthem (Bass Boosted) 🎵",
                gradientColors = listOf(0xFF1F0A3D, 0xFF003852),
                likesCount = 24500,
                commentsCount = 1420,
                sharesCount = 890,
                isLiked = true,
                isFollowed = true
            ),
            ReelItem(
                id = "reel_2",
                authorName = "Aman Tech Guide",
                authorHandle = "@amantech",
                authorAvatar = "",
                title = "Gemini 3.5 Inside Android! 🤖",
                description = "How to solve any coding, math or content problem in 2 seconds with Valku AI assistant! Watch till end 💡🔥 #AI #CodingLife #SmartIndia",
                audioTrackName = "Lofi Vibes India - Chill Beats 🎧",
                gradientColors = listOf(0xFF380036, 0xFF0CBABA),
                likesCount = 18900,
                commentsCount = 630,
                sharesCount = 420
            ),
            ReelItem(
                id = "reel_3",
                authorName = "Sneha Dance Studio",
                authorHandle = "@snehapatel",
                authorAvatar = "",
                title = "Neon Street Dance 💃✨",
                description = "Tried the new street choreography under cyber lights! Drop a ❤️ if you loved the transition! #DanceReels #Explore #ValkuCreators",
                audioTrackName = "Trending Gujarati Fusion EDM 🎶",
                gradientColors = listOf(0xFF4A00E0, 0xFF8E2DE2),
                likesCount = 31200,
                commentsCount = 2100,
                sharesCount = 1350
            ),
            ReelItem(
                id = "reel_4",
                authorName = "Gamers Arena Hindi",
                authorHandle = "@gaming_guru",
                authorAvatar = "",
                title = "Top 5 High Scores in Valku Arcade 🎮",
                description = "Speed tapping masterclass! Can you score over 3000 in Reflex Rush? Challenge your friends! 🕹️⚡ #Gaming #MiniGames #Arcade",
                audioTrackName = "Arcade Retro Wave 8-Bit 👾",
                gradientColors = listOf(0xFF0F2027, 0xFF2C5364),
                likesCount = 14800,
                commentsCount = 980,
                sharesCount = 310
            )
        )
    )
    val reels: StateFlow<List<ReelItem>> = _reels.asStateFlow()

    // Long Videos
    private val _longVideos = MutableStateFlow<List<LongVideoItem>>(
        listOf(
            LongVideoItem(
                id = "lv_1",
                title = "The Making of Valku Sarvaiya: Building India's Most Complete Social & AI Super App",
                channelName = "Valku Sarvaiya Official",
                channelAvatar = "",
                views = "284K views",
                duration = "18:42",
                uploadTime = "1 day ago",
                category = "Technology",
                description = "A deep dive into combining WhatsApp chatting, Instagram reels, Facebook community posts and Google Gemini AI in one ultra-fast Android application.",
                likesCount = 19500,
                isLiked = true
            ),
            LongVideoItem(
                id = "lv_2",
                title = "Full Tutorial: How to Use Gemini AI for Content Creation, Reel Scripts & Study Notes",
                channelName = "Tech World Hindi",
                channelAvatar = "",
                views = "142K views",
                duration = "22:15",
                uploadTime = "3 days ago",
                category = "Education & AI",
                description = "Learn how to prompt Valku AI to write viral short video scripts, solve coding equations, and design photography concepts.",
                likesCount = 11200
            ),
            LongVideoItem(
                id = "lv_3",
                title = "Arcade Tournament Highlights & Speedrun Gameplay Live Stream",
                channelName = "Valku Esports",
                channelAvatar = "",
                views = "98K views",
                duration = "34:10",
                uploadTime = "5 days ago",
                category = "Gaming",
                description = "Exciting matches across Cyber Reflex Rush and Number Fusion 2048 mini-games with community players!",
                likesCount = 7600
            )
        )
    )
    val longVideos: StateFlow<List<LongVideoItem>> = _longVideos.asStateFlow()

    // AI Messages
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                id = "msg_welcome",
                content = "Namaste! Main hoon **Valku AI** (Powered by Gemini AI). Main aapki problem solve kar sakta hoon, study/code me help kar sakta hoon, stylish photo concept prompts bana sakta hoon, aur viral short video / reel scripts generate kar sakta hoon. Aap mujhse Hindi, Gujarati ya English kisi me bhi poochh sakte hain. Aap aaj kya banana ya poochhna chahenge? ✨",
                isAi = true,
                category = "General"
            )
        )
    )
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()
    val isAiGenerating = MutableStateFlow(false)

    // Friends & Contacts
    private val _friends = MutableStateFlow<List<FriendContact>>(
        listOf(
            FriendContact("f1", "Valku Sarvaiya", "@valkusarvaiya", "", 48, isFriend = true, statusText = "Official Super App Creator 🌟"),
            FriendContact("f2", "Priya Sharma", "@priyadesigns", "", 12, isFriend = true, statusText = "Designing cool vibes ✨"),
            FriendContact("f3", "Rahul Varma", "@rahultech", "", 9, isFriend = true, statusText = "Coding the future 💻"),
            FriendContact("f4", "Aakash Patel", "@aakash_p", "", 24, isFriend = false, isPendingRequest = false, statusText = "Music & Tech lover 🎧"),
            FriendContact("f5", "Divya Nair", "@divyanair", "", 7, isFriend = false, isPendingRequest = true, statusText = "Exploring new places ✈️"),
            FriendContact("f6", "Vikram Singh", "@vikram_s", "", 18, isFriend = false, isPendingRequest = false, statusText = "Fitness and gaming enthusiast 🏋️‍♂️")
        )
    )
    val friends: StateFlow<List<FriendContact>> = _friends.asStateFlow()

    // Mini Games Flow with dynamic high scores
    private val initialGames = listOf(
        MiniGameItem("g1", "Reflex Rush", "⚡", "Speed Tap", 1250),
        MiniGameItem("g2", "Memory Match", "🤖", "Memory Puzzle", 820),
        MiniGameItem("g3", "Tic-Tac-Toe AI", "❌", "Strategy", 400),
        MiniGameItem("g4", "2048 Fusion", "🔢", "Numbers", 340)
    )
    private val _gamesState = MutableStateFlow(initialGames)

    val games: StateFlow<List<MiniGameItem>> = dao.getAllGameScores().map { scores ->
        val scoreMap = scores.associate { it.gameId to it.highscore }
        initialGames.map { g ->
            val hs = scoreMap[g.id] ?: g.highScore
            g.copy(highScore = hs)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialGames)

    val gameScores: StateFlow<List<GameScoreEntity>> = dao.getAllGameScores().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ValkuDatabase.populateInitialData(dao)
        }
    }

    // --- Chat Functions ---
    fun openChat(thread: ChatThread) {
        _activeThread.value = thread
        viewModelScope.launch {
            dao.getMessagesForThread(thread.id).collect { entities ->
                _currentMessages.value = entities.map {
                    var attName = ""
                    var attSize = ""
                    var cName = ""
                    var cPhone = ""
                    var mUrl = it.mediaUrl
                    if (it.mediaUrl.contains("|")) {
                        val parts = it.mediaUrl.split("|")
                        if (parts.size >= 5) {
                            attName = parts[1]
                            attSize = parts[2]
                            cName = parts[3]
                            cPhone = parts[4]
                            mUrl = parts.getOrNull(5) ?: ""
                        }
                    }
                    ChatMessage(
                        id = it.id,
                        threadId = it.threadId,
                        senderName = it.senderName,
                        text = it.text,
                        timestamp = it.timestamp,
                        isMe = it.isMe,
                        messageType = it.messageType,
                        voiceDurationSeconds = it.voiceDurationSeconds,
                        mediaUrl = mUrl,
                        reaction = it.reaction,
                        isRead = it.isRead,
                        attachmentName = attName,
                        attachmentSize = attSize,
                        contactName = cName,
                        contactPhone = cPhone
                    )
                }
            }
        }
    }

    fun closeChat() {
        _activeThread.value = null
    }

    fun sendAttachment(
        type: String, // "document", "image", "audio", "video", "contact", "location"
        text: String = "",
        attachmentName: String = "",
        attachmentSize: String = "",
        contactName: String = "",
        contactPhone: String = "",
        mediaUrl: String = ""
    ) {
        val thread = _activeThread.value ?: return
        val msgId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val packedMediaMeta = "$type|$attachmentName|$attachmentSize|$contactName|$contactPhone|$mediaUrl"
        val summaryText = when (type) {
            "document" -> "📄 $attachmentName ($attachmentSize)"
            "image" -> if (text.isNotBlank()) text else "📷 Photo ($attachmentName)"
            "audio" -> "🎵 $attachmentName ($attachmentSize)"
            "video" -> if (text.isNotBlank()) text else "🎥 Video ($attachmentName)"
            "contact" -> "👤 Contact: $contactName • $contactPhone"
            "location" -> "📍 Location: $attachmentName"
            else -> text
        }

        val userMsg = ChatMessageEntity(
            id = msgId,
            threadId = thread.id,
            senderName = "You",
            text = summaryText,
            timestamp = now,
            isMe = true,
            messageType = type,
            voiceDurationSeconds = 0,
            mediaUrl = packedMediaMeta,
            reaction = "",
            isRead = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMessage(userMsg)
            dao.updateThread(
                ChatThreadEntity(
                    id = thread.id,
                    name = thread.name,
                    handle = thread.handle,
                    avatarUrl = thread.avatarUrl,
                    lastMessage = summaryText,
                    lastTimestamp = now,
                    unreadCount = 0,
                    isOnline = thread.isOnline,
                    userBio = thread.userBio,
                    phoneNumber = thread.phoneNumber,
                    hasStory = thread.hasStory
                )
            )

            delay(1200)
            val replyText = when (type) {
                "document" -> "Received the document '$attachmentName' ($attachmentSize)! Checking details 📄✅"
                "image" -> "Awesome photo! Captured so crisp & clear 📸✨"
                "audio" -> "Loved this audio track '$attachmentName'! Adding to playlist 🎧🎵"
                "video" -> "Video received! Superb quality 🎥🍿"
                "contact" -> "Saved contact for $contactName ($contactPhone) to my address book 👤📲"
                "location" -> "Got your live location at $attachmentName! See you there 📍🚗"
                else -> "Received attachment! 👍"
            }

            val replyMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                threadId = thread.id,
                senderName = thread.name,
                text = replyText,
                timestamp = System.currentTimeMillis(),
                isMe = false,
                messageType = "text",
                voiceDurationSeconds = 0,
                mediaUrl = "",
                reaction = "❤️",
                isRead = true
            )
            dao.insertMessage(replyMsg)
        }
    }

    fun sendMessage(text: String, messageType: String = "text", voiceDuration: Int = 0) {
        val thread = _activeThread.value ?: return
        if (text.isBlank() && messageType == "text") return

        val msgId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val userMsg = ChatMessageEntity(
            id = msgId,
            threadId = thread.id,
            senderName = "You",
            text = text,
            timestamp = now,
            isMe = true,
            messageType = messageType,
            voiceDurationSeconds = voiceDuration,
            mediaUrl = "",
            reaction = "",
            isRead = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMessage(userMsg)
            dao.updateThread(
                ChatThreadEntity(
                    id = thread.id,
                    name = thread.name,
                    handle = thread.handle,
                    avatarUrl = thread.avatarUrl,
                    lastMessage = if (messageType == "voice") "Voice message (${voiceDuration}s) 🎙️" else text,
                    lastTimestamp = now,
                    unreadCount = 0,
                    isOnline = thread.isOnline,
                    userBio = thread.userBio,
                    phoneNumber = thread.phoneNumber,
                    hasStory = thread.hasStory
                )
            )

            delay(1200)
            val replyText = when {
                messageType == "voice" -> "Nice voice note! Main sun raha hoon 🎧"
                text.contains("call", ignoreCase = true) -> "Haan bilkul, aap video call start kar sakte hain! 📹"
                text.contains("hi", ignoreCase = true) || text.contains("hello", ignoreCase = true) -> "Hello! Kaise hain aap? Valku Sarvaiya app kaisa lag raha hai? 🌟"
                text.contains("reel", ignoreCase = true) || text.contains("video", ignoreCase = true) -> "Haan Reels tab me bohot trending short videos hain! Check karo! 🔥"
                else -> "Got your message! '${text}'. Main jaldi reply karta hoon 👍"
            }
            val replyMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                threadId = thread.id,
                senderName = thread.name,
                text = replyText,
                timestamp = System.currentTimeMillis(),
                isMe = false,
                messageType = "text",
                voiceDurationSeconds = 0,
                mediaUrl = "",
                reaction = "👍",
                isRead = true
            )
            dao.insertMessage(replyMsg)
        }
    }

    fun reactToMessage(messageId: String, emoji: String) {
        val current = _currentMessages.value
        _currentMessages.value = current.map {
            if (it.id == messageId) it.copy(reaction = if (it.reaction == emoji) "" else emoji) else it
        }
    }

    // --- Call Functions ---
    fun startCall(contactName: String, contactHandle: String, callType: CallType) {
        _activeCall.value = ActiveCallState(
            isActive = true,
            contactName = contactName,
            contactHandle = contactHandle,
            callType = callType,
            durationSeconds = 0,
            isMuted = false,
            isCameraOff = false,
            isSpeakerOn = true,
            isFrontCamera = true
        )
        startCallTimer()
    }

    private fun startCallTimer() {
        viewModelScope.launch {
            while (_activeCall.value != null && _activeCall.value!!.isActive) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let {
                    it.copy(durationSeconds = it.durationSeconds + 1)
                }
            }
        }
    }

    fun toggleCallMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isCameraOff = !it.isCameraOff) }
    }

    fun toggleCallSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun flipCallCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleMute() = toggleCallMute()
    fun toggleCamera() = toggleCallCamera()
    fun toggleSpeaker() = toggleCallSpeaker()
    fun flipCamera() = flipCallCamera()

    fun endCall() {
        val current = _activeCall.value
        val thread = _activeThread.value
        if (current != null && current.durationSeconds > 0) {
            val callTypeStr = if (current.callType == CallType.VIDEO) "Video call" else "Audio call"
            val mins = current.durationSeconds / 60
            val secs = current.durationSeconds % 60
            val durStr = String.format("%02d:%02d", mins, secs)
            if (thread != null) {
                sendMessage("$callTypeStr ended ($durStr) 📞", messageType = "call_log")
            }
        }
        _activeCall.value = null
    }

    // --- Stories Functions ---
    fun openStoryViewer(index: Int) {
        val currentStories = stories.value
        if (currentStories.isNotEmpty()) {
            _storyState.value = ActiveStoryState(
                isOpen = true,
                currentIndex = index.coerceIn(0, currentStories.size - 1),
                stories = currentStories
            )
        }
    }

    fun closeStoryViewer() {
        _storyState.value = ActiveStoryState(isOpen = false)
    }

    fun nextStory() {
        val current = _storyState.value
        if (current.currentIndex < current.stories.size - 1) {
            _storyState.value = current.copy(currentIndex = current.currentIndex + 1)
        } else {
            closeStoryViewer()
        }
    }

    fun previousStory() {
        val current = _storyState.value
        if (current.currentIndex > 0) {
            _storyState.value = current.copy(currentIndex = current.currentIndex - 1)
        }
    }

    fun prevStory() = previousStory()

    fun replyToStory(reply: String) {
        val current = _storyState.value
        if (current.stories.isNotEmpty()) {
            val story = current.stories[current.currentIndex]
            val thread = chatThreads.value.find { it.name.contains(story.userName, ignoreCase = true) }
            if (thread != null) {
                openChat(thread)
                sendMessage("Replied to your status: $reply 💬")
            }
        }
        closeStoryViewer()
    }

    fun createStory(caption: String, bgHex: String = "#1A153A") {
        if (caption.isBlank()) return
        val newStory = StoryEntity(
            id = "story_${System.currentTimeMillis()}",
            userId = "user_me",
            userName = "You",
            userAvatar = "",
            mediaUrl = "",
            caption = caption,
            timestamp = System.currentTimeMillis(),
            isViewed = false,
            backgroundColorHex = bgHex
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertStory(newStory)
        }
    }

    // --- Post Functions ---
    fun toggleLikePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPost = posts.value.find { it.id == postId } ?: return@launch
            val updated = SocialPostEntity(
                id = currentPost.id,
                authorName = currentPost.authorName,
                authorHandle = currentPost.authorHandle,
                authorAvatar = currentPost.authorAvatar,
                content = currentPost.content,
                mediaUrl = currentPost.mediaUrl,
                mediaType = currentPost.mediaType,
                likesCount = if (currentPost.isLiked) currentPost.likesCount - 1 else currentPost.likesCount + 1,
                commentsCount = currentPost.commentsCount,
                sharesCount = currentPost.sharesCount,
                isLiked = !currentPost.isLiked,
                isSaved = currentPost.isSaved,
                timestamp = currentPost.timestamp,
                location = currentPost.location
            )
            dao.updatePost(updated)
        }
    }

    fun toggleSavePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPost = posts.value.find { it.id == postId } ?: return@launch
            val updated = SocialPostEntity(
                id = currentPost.id,
                authorName = currentPost.authorName,
                authorHandle = currentPost.authorHandle,
                authorAvatar = currentPost.authorAvatar,
                content = currentPost.content,
                mediaUrl = currentPost.mediaUrl,
                mediaType = currentPost.mediaType,
                likesCount = currentPost.likesCount,
                commentsCount = currentPost.commentsCount,
                sharesCount = currentPost.sharesCount,
                isLiked = currentPost.isLiked,
                isSaved = !currentPost.isSaved,
                timestamp = currentPost.timestamp,
                location = currentPost.location
            )
            dao.updatePost(updated)
        }
    }

    fun createPost(content: String, location: String = "Gujarat, India") {
        if (content.isBlank()) return
        val newPost = SocialPostEntity(
            id = "post_${System.currentTimeMillis()}",
            authorName = "You",
            authorHandle = "@valku_member",
            authorAvatar = "",
            content = content,
            mediaUrl = "",
            mediaType = "text",
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = true,
            isSaved = false,
            timestamp = System.currentTimeMillis(),
            location = location
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertPost(newPost)
        }
    }

    // --- Reels Functions ---
    fun toggleLikeReel(reelId: String) {
        _reels.value = _reels.value.map {
            if (it.id == reelId) {
                val newLiked = !it.isLiked
                it.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1
                )
            } else it
        }
    }

    fun toggleFollowReel(reelId: String) {
        _reels.value = _reels.value.map {
            if (it.id == reelId) it.copy(isFollowed = !it.isFollowed) else it
        }
    }

    fun createReel(title: String, description: String, audioName: String) {
        if (title.isBlank()) return
        val newReel = ReelItem(
            id = "reel_${System.currentTimeMillis()}",
            authorName = "You",
            authorHandle = "@valku_creator",
            authorAvatar = "",
            title = title,
            description = description,
            audioTrackName = if (audioName.isBlank()) "Original Sound - Valku Creator 🎵" else audioName,
            gradientColors = listOf(0xFF2C0B4E, 0xFF025373),
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = true,
            isFollowed = true
        )
        _reels.value = listOf(newReel) + _reels.value
    }

    // --- Friend Request Functions ---
    fun toggleFriendStatus(friendId: String) {
        _friends.value = _friends.value.map {
            if (it.id == friendId) {
                if (it.isFriend) {
                    it.copy(isFriend = false, isPendingRequest = false)
                } else if (it.isPendingRequest) {
                    it.copy(isFriend = true, isPendingRequest = false)
                } else {
                    it.copy(isPendingRequest = true)
                }
            } else it
        }
    }

    // --- Gemini AI Functions ---
    fun sendAiPrompt(prompt: String, category: String = "General", mode: String = "chat") {
        if (prompt.isBlank() || isAiGenerating.value) return

        val userMsg = AiMessage(
            id = UUID.randomUUID().toString(),
            content = prompt,
            isAi = false,
            category = category
        )
        _aiMessages.value = _aiMessages.value + userMsg
        isAiGenerating.value = true

        viewModelScope.launch {
            val result = geminiRepo.generateAiResponse(prompt, mode)
            val responseText = result.getOrElse { "Sorry, could not generate response at this time." }
            val aiMsg = AiMessage(
                id = UUID.randomUUID().toString(),
                content = responseText,
                isAi = true,
                category = category,
                generatedMediaPrompt = if (mode == "art_prompt") prompt else null
            )
            _aiMessages.value = _aiMessages.value + aiMsg
            isAiGenerating.value = false
        }
    }

    fun clearAiChat() {
        _aiMessages.value = listOf(
            AiMessage(
                id = "msg_welcome_${System.currentTimeMillis()}",
                content = "AI Chat cleared! How can Valku AI assist you today? 🌟",
                isAi = true,
                category = "General"
            )
        )
    }

    // --- Game Score Updates ---
    fun updateGameScore(gameId: String, score: Int) {
        val gameName = when (gameId) {
            "g1" -> "Reflex Rush"
            "g2" -> "Memory Match"
            "g3" -> "Tic-Tac-Toe AI"
            "g4" -> "2048 Fusion"
            else -> "Mini Game"
        }
        updateGameScore(gameId, gameName, score)
    }

    fun updateGameScore(gameId: String, gameName: String, score: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = dao.getScoreForGame(gameId)
            val highest = if (existing != null) maxOf(existing.highscore, score) else score
            val playedCount = (existing?.gamesPlayed ?: 0) + 1
            dao.insertOrUpdateGameScore(
                GameScoreEntity(
                    gameId = gameId,
                    gameName = gameName,
                    highscore = highest,
                    gamesPlayed = playedCount,
                    lastPlayed = System.currentTimeMillis()
                )
            )
        }
    }

    // --- Invite Contacts via Intent ---
    fun shareInviteLink(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Join me on Valku Sarvaiya Super App!"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "🔥 Hey! Download Valku Sarvaiya Super App now! It combines WhatsApp Chatting, HD Audio/Video Calls, Instagram Reels & Stories, Facebook Feed, Mini Games and Google Gemini AI in one futuristic app! 🚀 Download link: https://valkusarvaiya.app/download"
            )
        }
        val chooser = Intent.createChooser(shareIntent, "Invite friends to Valku Sarvaiya")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}

val ValkuViewModelFactory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        return ValkuViewModel(application) as T
    }
}
