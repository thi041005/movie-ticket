package com.example.app02.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
private val DarkColorScheme = darkColorScheme(
    primary = bcl,
    secondary = headerColor,
    tertiary = bcl,

    background = bcl, // Đặt nền cho chế độ tối
    surface = bcl,

    onBackground = Color.White, // Màu chữ trên nền tối
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = bcl,
    secondary = headerColor,
    tertiary = bcl,

    background = bcl,
    surface = bcl,

    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun App02Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Tắt Dynamic Colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
