package com.kabaddiarena

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme()

private val LightColors = lightColorScheme()

@Composable
fun KabaddiArenaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors =
        if (darkTheme) DarkColors
        else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
