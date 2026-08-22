package com.example.data.model

data class UserProfile(
    val id: String = "u_valku_user",
    val name: String = "Valku Sarvaiya",
    val handle: String = "@valkusarvaiya",
    val phoneNumber: String = "+91 98765 43210",
    val email: String = "valku.creator@gmail.com",
    val bio: String = "Official Super App Creator & Explorer 🚀",
    val avatarUrl: String = "",
    val isLoggedIn: Boolean = true,
    val loginMethod: String = "Phone" // "Phone", "Google", "Email", "Guest"
)

data class ChatMessage(
    val id: String,
    val threadId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean,
    val messageType: String = "text", // "text", "voice", "image", "call_log", "document", "audio", "video", "contact", "location"
    val voiceDurationSeconds: Int = 0,
    val mediaUrl: String = "",
    val reaction: String = "",
    val isRead: Boolean = true,
    val attachmentName: String = "",
    val attachmentSize: String = "",
    val contactName: String = "",
    val contactPhone: String = ""
)

data class ChatThread(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val userBio: String = "",
    val phoneNumber: String = "+91 98765 43210",
    val hasStory: Boolean = false
)

data class SocialPost(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val content: String,
    val mediaUrl: String = "",
    val mediaType: String = "image", // "image", "video", "text"
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "India",
    val comments: List<PostComment> = emptyList()
)

data class PostComment(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timeAgo: String = "Just now",
    val likes: Int = 0
)

data class StoryItem(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val mediaUrl: String,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val backgroundColorHex: String = "#1A153A"
)

data class ReelItem(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val title: String,
    val description: String,
    val audioTrackName: String = "Original Sound - Valku Beats 🎵",
    val videoUrl: String = "",
    val gradientColors: List<Long> = listOf(0xFF2B1055, 0xFF750035),
    val likesCount: Int = 12400,
    val commentsCount: Int = 890,
    val sharesCount: Int = 430,
    val isLiked: Boolean = false,
    val isFollowed: Boolean = false
)

data class LongVideoItem(
    val id: String,
    val title: String,
    val channelName: String,
    val channelAvatar: String,
    val views: String = "120K views",
    val duration: String = "14:25",
    val uploadTime: String = "2 hours ago",
    val category: String = "Technology & AI",
    val description: String = "",
    val likesCount: Int = 8500,
    val isLiked: Boolean = false
)

data class AiMessage(
    val id: String,
    val content: String,
    val isAi: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "General", // "Chat", "Code/Problem", "Art/Photo Prompt", "Short Video Script", "Audio"
    val generatedMediaPrompt: String? = null
)

data class FriendContact(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val mutualFriends: Int = 0,
    val isFriend: Boolean = false,
    val isPendingRequest: Boolean = false,
    val statusText: String = "Hey there! Using Valku Sarvaiya super app 🚀"
)

data class MiniGameItem(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val category: String,
    val highScore: Int = 0
)

data class GameScore(
    val gameId: String,
    val gameName: String,
    val highscore: Int,
    val gamesPlayed: Int,
    val lastPlayed: Long = System.currentTimeMillis()
)
