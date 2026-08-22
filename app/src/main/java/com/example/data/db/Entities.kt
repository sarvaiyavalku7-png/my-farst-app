package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean,
    val messageType: String,
    val voiceDurationSeconds: Int,
    val mediaUrl: String,
    val reaction: String,
    val isRead: Boolean
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int,
    val isOnline: Boolean,
    val userBio: String,
    val phoneNumber: String,
    val hasStory: Boolean
)

@Entity(tableName = "social_posts")
data class SocialPostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val content: String,
    val mediaUrl: String,
    val mediaType: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val timestamp: Long,
    val location: String
)

@Entity(tableName = "status_stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val mediaUrl: String,
    val caption: String,
    val timestamp: Long,
    val isViewed: Boolean,
    val backgroundColorHex: String
)

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey val gameId: String,
    val gameName: String,
    val highscore: Int,
    val gamesPlayed: Int,
    val lastPlayed: Long
)

@Entity(tableName = "ai_conversations")
data class AiMessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val isAi: Boolean,
    val timestamp: Long,
    val category: String,
    val generatedMediaPrompt: String?
)
