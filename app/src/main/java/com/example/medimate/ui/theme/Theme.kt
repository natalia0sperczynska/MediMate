package com.example.medimate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val  LightColorScheme =lightColorScheme (
    primary = PurpleMain,
    onPrimary = White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = Grey2,

    secondary = PurpleGrey,
    onSecondary = White,
    secondaryContainer = PurpleGreyLight,
    onSecondaryContainer = Grey2,

    tertiary = PurpleGrey3,

    background = LightGrey,
    surface = White,
    onSurface = Grey2,

    inversePrimary = PurpleGrey2
)

private val DarkColorScheme = darkColorScheme(
    primary = PurpleLight,
    onPrimary = Black,
    primaryContainer = Purple,
    onPrimaryContainer = White,

    secondary = PurpleGreyLight,
    onSecondary = Grey2,
    secondaryContainer = PurpleGrey,
    onSecondaryContainer = White,

    tertiary = Purple2,

    background = Grey2,
    surface = PurpleGrey2,
    onSurface = White,

    inversePrimary = PurpleMain
)

@Composable
fun MediMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content,
        shapes = replyShapes
    )
}