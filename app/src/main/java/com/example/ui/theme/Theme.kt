package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ValkuPrimary,
    onPrimary = Color(0xFF001F29),
    primaryContainer = Color(0xFF004D61),
    onPrimaryContainer = Color(0xFFBBE9FF),
    secondary = ValkuSecondary,
    onSecondary = Color(0xFF380066),
    secondaryContainer = Color(0xFF561689),
    onSecondaryContainer = Color(0xFFEDD6FF),
    tertiary = ValkuTertiary,
    onTertiary = Color(0xFF49001E),
    tertiaryContainer = Color(0xFF750035),
    onTertiaryContainer = Color(0xFFFFD9E2),
    background = ValkuBackground,
    onBackground = ValkuTextPrimary,
    surface = ValkuSurface,
    onSurface = ValkuTextPrimary,
    surfaceVariant = ValkuSurfaceVariant,
    onSurfaceVariant = ValkuTextSecondary,
    outline = ValkuCardBorder,
    error = ValkuError,
    onError = Color.White
)

@Composable
fun ValkuSarvaiyaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ValkuSarvaiyaTheme(content = content)
}
