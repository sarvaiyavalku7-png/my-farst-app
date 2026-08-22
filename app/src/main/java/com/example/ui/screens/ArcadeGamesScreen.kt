package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MiniGameItem
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
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ArcadeGamesScreen(
    viewModel: ValkuViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    var selectedGameId by remember { mutableStateOf("g1") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ValkuBackground)
            .padding(bottom = 80.dp)
            .testTag("arcade_games_screen")
    ) {
        // Arcade Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ValkuSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(ValkuSecondary, ValkuTertiary))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Arcade",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Valku Arcade Arena",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ValkuTextPrimary
                    )
                    Text(
                        text = "Play games & beat high scores!",
                        fontSize = 11.sp,
                        color = ValkuTextMuted
                    )
                }
            }
        }

        // Game Selector Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(ValkuSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(games) { game ->
                val isSel = game.id == selectedGameId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSel) Brush.horizontalGradient(listOf(ValkuSecondary, ValkuPrimary))
                            else Brush.linearGradient(listOf(ValkuSurfaceVariant, ValkuSurfaceVariant))
                        )
                        .clickable { selectedGameId = game.id }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(game.iconEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = game.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color(0xFF090814) else ValkuTextPrimary
                            )
                            Text(
                                text = "Best: ${game.highScore}",
                                fontSize = 10.sp,
                                color = if (isSel) Color(0xFF090814) else ValkuTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Active Game Arena
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            when (selectedGameId) {
                "g1" -> CyberReflexGame(
                    onNewHighScore = { score -> viewModel.updateGameScore("g1", score) }
                )
                "g2" -> MemoryCardGame(
                    onNewHighScore = { score -> viewModel.updateGameScore("g2", score) }
                )
                "g3" -> TicTacToeGame(
                    onNewHighScore = { score -> viewModel.updateGameScore("g3", score) }
                )
                "g4" -> NumberFusion2048Game(
                    onNewHighScore = { score -> viewModel.updateGameScore("g4", score) }
                )
            }
        }
    }
}

/* ==========================================================================
   GAME 1: Cyber Reflex Rush
   ========================================================================== */
@Composable
fun CyberReflexGame(
    onNewHighScore: (Int) -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(10) }
    var activeTargetIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            score = 0
            streak = 0
            timeLeft = 10
            activeTargetIndex = Random.nextInt(9)
            while (timeLeft > 0 && isPlaying) {
                delay(1000)
                timeLeft--
            }
            isPlaying = false
            onNewHighScore(score)
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ValkuSecondary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Score Board
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = "Score", tint = ValkuPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ValkuPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = "Time", tint = ValkuTertiary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Time: ${timeLeft}s", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ValkuTertiary)
                }
            }

            if (!isPlaying) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚡ Cyber Reflex Rush", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = ValkuPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap the glowing neon nodes as fast as possible before time runs out!", fontSize = 13.sp, color = ValkuTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { isPlaying = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ValkuPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text("Start Speed Rush 🚀", color = Color(0xFF090814), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            } else {
                // 3x3 Target Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.size(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(9) { index ->
                        val isTarget = index == activeTargetIndex
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isTarget) Brush.radialGradient(listOf(Color(0xFF00E676), Color(0xFF009688)))
                                    else Brush.linearGradient(listOf(Color(0xFF1E1B38), Color(0xFF141228)))
                                )
                                .border(
                                    width = if (isTarget) 2.dp else 1.dp,
                                    color = if (isTarget) Color(0xFF00E676) else ValkuCardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(enabled = isPlaying) {
                                    if (isTarget) {
                                        streak++
                                        score += (10 * (1 + streak / 3))
                                        var next = Random.nextInt(9)
                                        while (next == activeTargetIndex) {
                                            next = Random.nextInt(9)
                                        }
                                        activeTargetIndex = next
                                    } else {
                                        streak = 0
                                        if (score > 5) score -= 5
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isTarget) {
                                Text("⚡", fontSize = 28.sp)
                            }
                        }
                    }
                }
            }

            Text("Streak Multiplier: x${1 + streak / 3}", color = ValkuTextMuted, fontSize = 12.sp)
        }
    }
}

/* ==========================================================================
   GAME 2: Memory Card Match
   ========================================================================== */
@Composable
fun MemoryCardGame(
    onNewHighScore: (Int) -> Unit
) {
    val emojis = listOf("🤖", "🚀", "💎", "⚡", "🎮", "🌟")
    var cards by remember {
        mutableStateOf((emojis + emojis).shuffled())
    }
    var revealedIndices = remember { mutableStateListOf<Int>() }
    var matchedIndices = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }
    var isChecking by remember { mutableStateOf(false) }

    fun restartGame() {
        cards = (emojis + emojis).shuffled()
        revealedIndices.clear()
        matchedIndices.clear()
        moves = 0
        isChecking = false
    }

    LaunchedEffect(revealedIndices.size) {
        if (revealedIndices.size == 2) {
            isChecking = true
            moves++
            val first = revealedIndices[0]
            val second = revealedIndices[1]
            if (cards[first] == cards[second]) {
                matchedIndices.add(first)
                matchedIndices.add(second)
                revealedIndices.clear()
                isChecking = false
                if (matchedIndices.size == cards.size) {
                    val score = (1000 - moves * 20).coerceAtLeast(100)
                    onNewHighScore(score)
                }
            } else {
                delay(800)
                revealedIndices.clear()
                isChecking = false
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ValkuPrimary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Moves: $moves", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ValkuPrimary)
                Text("Matched: ${matchedIndices.size / 2}/6", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                IconButton(onClick = { restartGame() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = ValkuTextMuted)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards.size) { index ->
                    val isRevealed = revealedIndices.contains(index) || matchedIndices.contains(index)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isRevealed) Brush.radialGradient(listOf(Color(0xFF321A63), Color(0xFF1E103E)))
                                else Brush.linearGradient(listOf(Color(0xFF1B1736), Color(0xFF120F26)))
                            )
                            .border(
                                1.5.dp,
                                if (matchedIndices.contains(index)) Color(0xFF00E676) else if (isRevealed) ValkuPrimary else ValkuCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isChecking && !isRevealed) {
                                if (revealedIndices.size < 2) {
                                    revealedIndices.add(index)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRevealed) {
                            Text(cards[index], fontSize = 26.sp)
                        } else {
                            Text("❓", fontSize = 20.sp, color = ValkuTextMuted)
                        }
                    }
                }
            }

            if (matchedIndices.size == cards.size) {
                Text("🎉 You Won! Score saved to Profile!", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
            } else {
                Text("Match all pairs to win!", color = ValkuTextMuted, fontSize = 12.sp)
            }
        }
    }
}

/* ==========================================================================
   GAME 3: Valku Tic-Tac-Toe AI
   ========================================================================== */
@Composable
fun TicTacToeGame(
    onNewHighScore: (Int) -> Unit
) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) }
    var playerScore by remember { mutableIntStateOf(0) }
    var aiScore by remember { mutableIntStateOf(0) }

    fun checkWinner(b: List<String>): String? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (line in lines) {
            val (a, bIdx, c) = line
            if (b[a].isNotEmpty() && b[a] == b[bIdx] && b[a] == b[c]) {
                return b[a]
            }
        }
        if (b.none { it.isEmpty() }) return "DRAW"
        return null
    }

    fun makeAiMove(currentBoard: List<String>) {
        val emptyIndices = currentBoard.indices.filter { currentBoard[it].isEmpty() }
        if (emptyIndices.isNotEmpty() && winner == null) {
            val chosen = emptyIndices.random()
            val newB = currentBoard.toMutableList()
            newB[chosen] = "O"
            board = newB
            val w = checkWinner(newB)
            if (w != null) {
                winner = w
                if (w == "O") aiScore++
            } else {
                isPlayerTurn = true
            }
        }
    }

    fun restart() {
        board = List(9) { "" }
        winner = null
        isPlayerTurn = true
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ValkuTertiary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("You (X): $playerScore", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ValkuPrimary)
                Text("AI (O): $aiScore", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ValkuTertiary)
                IconButton(onClick = { restart() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = ValkuTextMuted)
                }
            }

            // 3x3 Board
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.size(260.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(9) { index ->
                    val mark = board[index]
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1B1736))
                            .border(1.dp, ValkuCardBorder, RoundedCornerShape(14.dp))
                            .clickable(enabled = mark.isEmpty() && isPlayerTurn && winner == null) {
                                val newB = board.toMutableList()
                                newB[index] = "X"
                                board = newB
                                val w = checkWinner(newB)
                                if (w != null) {
                                    winner = w
                                    if (w == "X") {
                                        playerScore++
                                        onNewHighScore(playerScore * 100)
                                    }
                                } else {
                                    isPlayerTurn = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mark,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (mark == "X") ValkuPrimary else ValkuTertiary
                        )
                    }
                }
            }

            LaunchedEffect(isPlayerTurn) {
                if (!isPlayerTurn && winner == null) {
                    delay(500)
                    makeAiMove(board)
                }
            }

            if (winner != null) {
                Text(
                    text = if (winner == "X") "🏆 You Won!" else if (winner == "O") "🤖 AI Won!" else "🤝 Match Draw!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (winner == "X") Color(0xFF00E676) else ValkuTertiary
                )
            } else {
                Text(
                    text = if (isPlayerTurn) "Your Turn (X)" else "AI is thinking (O)...",
                    color = ValkuTextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/* ==========================================================================
   GAME 4: Number Fusion 2048 mini
   ========================================================================== */
@Composable
fun NumberFusion2048Game(
    onNewHighScore: (Int) -> Unit
) {
    var grid by remember {
        mutableStateOf(
            listOf(
                2, 4, 8, 2,
                4, 8, 16, 4,
                2, 16, 32, 8,
                8, 32, 64, 128
            )
        )
    }
    var score by remember { mutableIntStateOf(340) }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ValkuCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ValkuSecondary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Number Fusion 2048", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ValkuSecondary)
                Text("Score: $score", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ValkuPrimary)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.size(240.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(grid.size) { index ->
                    val num = grid[index]
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (num) {
                                    2 -> Color(0xFF2E2452)
                                    4 -> Color(0xFF3F2B6E)
                                    8 -> Color(0xFF5B1A6B)
                                    16 -> Color(0xFF801569)
                                    32 -> Color(0xFFA51B5F)
                                    64 -> Color(0xFFC7284F)
                                    128 -> Color(0xFFE93A36)
                                    else -> Color(0xFFFF6D00)
                                }
                            )
                            .clickable {
                                val next = if (num >= 2048) 2 else num * 2
                                val newG = grid.toMutableList()
                                newG[index] = next
                                grid = newG
                                score += next
                                onNewHighScore(score)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Text("Tap adjacent tiles to merge into 2048!", color = ValkuTextMuted, fontSize = 12.sp)
        }
    }
}
