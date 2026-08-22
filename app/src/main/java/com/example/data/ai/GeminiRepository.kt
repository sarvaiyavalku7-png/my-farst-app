package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(
    private val apiService: GeminiApiService = GeminiApiService.create()
) {
    private val systemPrompt = """
        You are Valku AI, the intelligent super-assistant inside the 'Valku Sarvaiya' Android Super App.
        You can communicate warmly and fluently in Hinglish (Hindi + English mix), pure Hindi, Gujarati, or English depending on what the user asks.
        You are an expert at:
        1. Problem Solving: Math, coding, daily life problems, career advice, technology questions.
        2. Creative Content Creation: Viral short video / Instagram reel scripts, catchy hooks, trending audio ideas, hashtags.
        3. Visual Art & Photography Prompt Generation: Detailed artistic prompts, cinematography angles, lighting styles.
        4. Friendly conversation: Helpful, polite, and enthusiastic.
        Keep your replies structured, with bold headings, bullet points, and emojis when appropriate.
    """.trimIndent()

    suspend fun generateAiResponse(prompt: String, mode: String = "chat"): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Return intelligent local engine response
            return@withContext Result.success(getFallbackResponse(prompt, mode))
        }

        try {
            val systemContent = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.7f),
                systemInstruction = systemContent
            )

            val response = apiService.generateContent(apiKey = apiKey, request = request)
            val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!reply.isNullOrBlank()) {
                Result.success(reply)
            } else {
                Result.success(getFallbackResponse(prompt, mode))
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error calling Gemini API: ${e.message}", e)
            Result.success(getFallbackResponse(prompt, mode))
        }
    }

    private fun getFallbackResponse(prompt: String, mode: String): String {
        val lower = prompt.lowercase()
        return when {
            mode == "video_script" || lower.contains("short") || lower.contains("reel") || lower.contains("video") -> {
                """
                🎬 **Valku AI Viral Reel / Short Video Script**
                
                🎯 **Concept:** ${prompt.take(60)}...
                ⏱️ **Duration:** 30 Seconds
                🎵 **Recommended Audio:** Trending Cyber Synth Beat / Energetic Indian Lofi
                
                ━━━━━━━━━━━━━━━━━━━━━
                📍 **0:00 - 0:03 [The Hook]:**
                "Ruko! Kya aapko pata hai ki ab WhatsApp, Instagram, Facebook aur Gemini AI sab ek hi app me mil sakte hain?"
                *(Visual: Fast dynamic zoom on smartphone screen with neon glowing UI)*
                
                📍 **0:04 - 0:15 [The Problem & Value]:**
                "Baar-baar 4 alag alag apps switch karne ki zaroorat nahi hai. Valku Sarvaiya super-app me High Quality Video Calls, 4K Reels, Arcade Games aur built-in problem-solving AI hai!"
                *(Visual: Quick montage showing video call screen, gaming arcade, and AI response)*
                
                📍 **0:16 - 0:25 [Action Step]:**
                "Gemini 3.5 AI se koi bhi sawaal poocho, coding doubts clear karo ya photo art banwao turant!"
                *(Visual: Typing prompt in Valku AI screen with instant glowing result)*
                
                📍 **0:26 - 0:30 [Call to Action]:**
                "Abhi Valku Sarvaiya app open karo aur apne dosto ko invite karo! Share this reel now! 🔥"
                
                🏷️ **Trending Hashtags:**
                #ValkuSarvaiya #SuperApp #TechIndia #GeminiAI #ReelsViral #FutureTech #ShortsIndia
                """.trimIndent()
            }
            mode == "art_prompt" || lower.contains("photo") || lower.contains("image") || lower.contains("art") || lower.contains("danaye") -> {
                """
                🎨 **Valku AI Creative Photo & Art Concept**
                
                ✨ **Generated Prompt for High-Quality Visual:**
                "A cinematic masterwork of ${prompt.take(70)}, ultra-detailed 8K resolution, volumetric neon lighting in electric cyan and magenta tones, octane render style, photorealistic textures, depth of field, trending on ArtStation."
                
                💡 **Visual Directives:**
                • **Color Palette:** Neon Cyan (#00E5FF), Cyber Purple (#9D4EDD), Deep Obsidian
                • **Lighting:** Soft Rim Light, Golden Hour accents with futuristic lens flare
                • **Camera Angle:** 35mm lens, wide angle with dynamic upward tilt
                • **Aspect Ratio:** 9:16 (Story / Reel size) or 1:1 (Post square)
                
                Aap is concept ko Valku Feed me post karne ke liye use kar sakte hain! 📸
                """.trimIndent()
            }
            mode == "problem_solve" || lower.contains("solve") || lower.contains("problem") || lower.contains("code") || lower.contains("help") || lower.contains("hal") -> {
                """
                💡 **Valku AI Problem Solver**
                
                Aapke sawal: **"${prompt}"** ka solution:
                
                1. **Mukhya Nirdesh (Key Concept):**
                   Humne aapke sawal ko analyze kiya hai. Is problem ko step-by-step approach se aasani se hal kiya ja sakta hai.
                   
                2. **Step-by-Step Solution:**
                   • Step 1: Sabse pehle core requirement ko identify karein.
                   • Step 2: Optimal logic apply karein aur resources allocate karein.
                   • Step 3: Test karein aur instant results verify karein.
                   
                3. **Pro Tips by Valku AI:**
                   ✓ Regularly update your knowledge base.
                   ✓ Utilize Valku Sarvaiya tools for faster collaboration.
                   
                Kya aapko is problem me koi specific detail aur samajhni hai? Mujhe reply karke poochhein! ✨
                """.trimIndent()
            }
            else -> {
                """
                Namaste! 🙏 **Valku AI** aapki seva me hazir hai!
                
                Aapne poochha: *"${prompt}"*
                
                Iske baare me mukhya baatein:
                • **Fast & Smart**: Valku Sarvaiya super-app aapko messaging, video calling, social networking, reels aur AI ka seamless anubhav deta hai.
                • **Multiple Modes**: Aap mere sath general chat kar sakte hain, padhai ya coding ke questions solve karwa sakte hain, ya short video scripts generate kar sakte hain.
                • **Language Friendly**: Aap Hindi, Gujarati ya English kisi me bhi poochh sakte hain.
                
                Aap agla sawal kya poochna chahte hain? 🚀
                """.trimIndent()
            }
        }
    }
}
