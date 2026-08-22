package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessageEntity::class,
        ChatThreadEntity::class,
        SocialPostEntity::class,
        StoryEntity::class,
        GameScoreEntity::class,
        AiMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ValkuDatabase : RoomDatabase() {
    abstract fun valkuDao(): ValkuDao

    companion object {
        @Volatile
        private var INSTANCE: ValkuDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ValkuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ValkuDatabase::class.java,
                    "valku_sarvaiya_superapp_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.valkuDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: ValkuDao) {
            val now = System.currentTimeMillis()

            // 1. Initial Chat Threads
            val initialThreads = listOf(
                ChatThreadEntity(
                    id = "thread_1",
                    name = "Valku Sarvaiya (Official)",
                    handle = "@valkusarvaiya",
                    avatarUrl = "",
                    lastMessage = "Welcome to Valku Sarvaiya! Chat, share reels, call & explore Gemini AI 🚀",
                    lastTimestamp = now,
                    unreadCount = 1,
                    isOnline = true,
                    userBio = "Creator of Valku Sarvaiya Super App 🌟 Building futuristic tech for everyone.",
                    phoneNumber = "+91 99887 76655",
                    hasStory = true
                ),
                ChatThreadEntity(
                    id = "thread_2",
                    name = "Priya Sharma",
                    handle = "@priyadesigns",
                    avatarUrl = "",
                    lastMessage = "Did you check the new video call filter in the app? Looks amazing! 😍",
                    lastTimestamp = now - 1000 * 60 * 15,
                    unreadCount = 2,
                    isOnline = true,
                    userBio = "UI/UX Designer & Content Creator ✨",
                    phoneNumber = "+91 98223 44556",
                    hasStory = true
                ),
                ChatThreadEntity(
                    id = "thread_3",
                    name = "Rahul Varma (Tech Lead)",
                    handle = "@rahultech",
                    avatarUrl = "",
                    lastMessage = "Voice message (0:24) 🎙️",
                    lastTimestamp = now - 1000 * 60 * 45,
                    unreadCount = 0,
                    isOnline = false,
                    userBio = "Android & AI Engineer | Coffee Enthusiast ☕",
                    phoneNumber = "+91 98111 22334",
                    hasStory = false
                ),
                ChatThreadEntity(
                    id = "thread_4",
                    name = "Valku Super App Developers Group",
                    handle = "@valkudevelopers",
                    avatarUrl = "",
                    lastMessage = "Amit: Released new mini games & Gemini 3.5 AI problem solver!",
                    lastTimestamp = now - 1000 * 60 * 120,
                    unreadCount = 4,
                    isOnline = true,
                    userBio = "Community group for Valku Sarvaiya super-app members",
                    phoneNumber = "+91 98000 11122",
                    hasStory = true
                ),
                ChatThreadEntity(
                    id = "thread_5",
                    name = "Sneha Patel",
                    handle = "@snehapatel",
                    avatarUrl = "",
                    lastMessage = "Shared a new Reel: 'Cyber City Night Walk 🌃'",
                    lastTimestamp = now - 1000 * 60 * 360,
                    unreadCount = 0,
                    isOnline = false,
                    userBio = "Traveler & Reel Vlogger 📸 Gujarat, India",
                    phoneNumber = "+91 97777 88899",
                    hasStory = true
                )
            )
            dao.insertThreads(initialThreads)

            // Initial Messages for thread 1
            val messagesThread1 = listOf(
                ChatMessageEntity(
                    id = "msg_1",
                    threadId = "thread_1",
                    senderName = "Valku Sarvaiya",
                    text = "Namaste! Welcome to Valku Sarvaiya Super App. Yahan WhatsApp, Instagram, Facebook, Gemini AI, Games aur Calling sab ek hi jagah hai!",
                    timestamp = now - 1000 * 60 * 10,
                    isMe = false,
                    messageType = "text",
                    voiceDurationSeconds = 0,
                    mediaUrl = "",
                    reaction = "🔥",
                    isRead = true
                ),
                ChatMessageEntity(
                    id = "msg_2",
                    threadId = "thread_1",
                    senderName = "You",
                    text = "Thank you Valku Bhai! The app design and features look super stylish and fast! 🚀",
                    timestamp = now - 1000 * 60 * 5,
                    isMe = true,
                    messageType = "text",
                    voiceDurationSeconds = 0,
                    mediaUrl = "",
                    reaction = "❤️",
                    isRead = true
                ),
                ChatMessageEntity(
                    id = "msg_3",
                    threadId = "thread_1",
                    senderName = "Valku Sarvaiya",
                    text = "Try calling, playing arcade mini-games or asking Gemini AI to generate video scripts or solve any problem for you!",
                    timestamp = now - 1000 * 60 * 1,
                    isMe = false,
                    messageType = "text",
                    voiceDurationSeconds = 0,
                    mediaUrl = "",
                    reaction = "⚡",
                    isRead = true
                )
            )
            dao.insertMessages(messagesThread1)

            // 2. Initial Stories
            val stories = listOf(
                StoryEntity(
                    id = "story_1",
                    userId = "user_valku",
                    userName = "Valku Sarvaiya",
                    userAvatar = "",
                    mediaUrl = "",
                    caption = "✨ Live testing new Valku Sarvaiya Super App! Hope you love the neon UI!",
                    timestamp = now - 1000 * 60 * 30,
                    isViewed = false,
                    backgroundColorHex = "#1F0A3D"
                ),
                StoryEntity(
                    id = "story_2",
                    userId = "user_priya",
                    userName = "Priya Sharma",
                    userAvatar = "",
                    mediaUrl = "",
                    caption = "Sunday creative vibes with Gemini AI 🎨✨",
                    timestamp = now - 1000 * 60 * 90,
                    isViewed = false,
                    backgroundColorHex = "#0C2340"
                ),
                StoryEntity(
                    id = "story_3",
                    userId = "user_sneha",
                    userName = "Sneha Patel",
                    userAvatar = "",
                    mediaUrl = "",
                    caption = "Sunset at Diu Beach 🌅 Beautiful Gujarat!",
                    timestamp = now - 1000 * 60 * 200,
                    isViewed = false,
                    backgroundColorHex = "#3D1A00"
                )
            )
            dao.insertStories(stories)

            // 3. Initial Social Posts
            val posts = listOf(
                SocialPostEntity(
                    id = "post_1",
                    authorName = "Valku Sarvaiya",
                    authorHandle = "@valkusarvaiya",
                    authorAvatar = "",
                    content = "🎉 Welcome everyone to the all-in-one Valku Sarvaiya Super App! Sabhi features jaise Chatting, High-Definition Audio/Video Calls, Stories, Reels, Long Videos, Mini Games aur Google Gemini AI ab ek jagah uplabdh hain. Apne dosto ko invite karein aur enjoy karein! 🚀🇮🇳",
                    mediaUrl = "banner",
                    mediaType = "image",
                    likesCount = 1420,
                    commentsCount = 286,
                    sharesCount = 95,
                    isLiked = true,
                    isSaved = true,
                    timestamp = now - 1000 * 60 * 60 * 2,
                    location = "Ahmedabad, Gujarat"
                ),
                SocialPostEntity(
                    id = "post_2",
                    authorName = "Tech World India",
                    authorHandle = "@techworld_in",
                    authorAvatar = "",
                    content = "💡 Gemini 3.5 AI Assistant integrated directly inside Valku Sarvaiya app! Ask questions in Hindi or English, create viral short video scripts, and generate photo art ideas instantly. Super smooth experience!",
                    mediaUrl = "ai_avatar",
                    mediaType = "image",
                    likesCount = 890,
                    commentsCount = 114,
                    sharesCount = 42,
                    isLiked = false,
                    isSaved = false,
                    timestamp = now - 1000 * 60 * 60 * 5,
                    location = "Bengaluru, India"
                ),
                SocialPostEntity(
                    id = "post_3",
                    authorName = "Rohan Mehta",
                    authorHandle = "@rohan_gamer",
                    authorAvatar = "",
                    content = "🎮 Just scored 4,200 points in the Valku Arcade 'Reflex Speed' game! Can anyone beat my score? Play directly in the Arcade tab without installing extra games! 🔥🕹️",
                    mediaUrl = "",
                    mediaType = "text",
                    likesCount = 530,
                    commentsCount = 68,
                    sharesCount = 19,
                    isLiked = true,
                    isSaved = false,
                    timestamp = now - 1000 * 60 * 60 * 12,
                    location = "Mumbai, India"
                )
            )
            dao.insertPosts(posts)

            // 4. Initial AI Chat
            val initialAi = AiMessageEntity(
                id = "ai_msg_welcome",
                content = "Namaste! Main hoon **Valku AI** (Powered by Gemini AI). Main aapki problem solve kar sakta hoon, study/code mein help kar sakta hoon, stylish photo concept prompts bana sakta hoon, aur viral short video / reel scripts generate kar sakta hoon. Aap mujhse Hindi, Gujarati ya English mein baat kar sakte hain. Aap aaj kya banana ya poochhna chahenge? ✨",
                isAi = true,
                timestamp = now - 1000 * 60 * 5,
                category = "General",
                generatedMediaPrompt = null
            )
            dao.insertAiMessage(initialAi)

            // 5. Initial Game Scores
            val initialScores = listOf(
                GameScoreEntity("game_reflex", "Cyber Reflex Rush", 2450, 8, now),
                GameScoreEntity("game_memory", "Memory Card Match", 180, 5, now),
                GameScoreEntity("game_tictactoe", "Valku Tic-Tac-Toe", 12, 15, now),
                GameScoreEntity("game_2048", "Number Fusion 2048", 1024, 4, now)
            )
            for (score in initialScores) {
                dao.insertOrUpdateGameScore(score)
            }
        }
    }
}
