package org.freedu.locatiosharingappjpc.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = TextDark,
    primaryContainer = GreenPrimaryDark,
    onPrimaryContainer = GreenLight,
    secondary = GreenPrimary,
    onSecondary = White,
    secondaryContainer = GreenPrimaryDark,
    onSecondaryContainer = GreenLight,
    tertiary = GreenAccent,
    onTertiary = TextDark,
    tertiaryContainer = GreenPrimaryDark,
    onTertiaryContainer = GreenLight,
    background = TextDark,
    onBackground = GreenLight,
    surface = GreenPrimaryDark,
    onSurface = GreenLight,
    surfaceVariant = GreenSurfaceVariant,
    onSurfaceVariant = TextDark,
    error = GreenError,
    onError = White,
    errorContainer = GreenErrorContainer,
    onErrorContainer = GreenError,
    outline = GreenAccent,
    outlineVariant = GreenPrimary
)

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = GreenAccent,
    onSecondary = White,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenPrimaryDark,
    tertiary = GreenPrimary,
    onTertiary = White,
    tertiaryContainer = GreenLight,
    onTertiaryContainer = GreenPrimaryDark,
    background = White,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    surfaceVariant = GreenLight,
    onSurfaceVariant = GreenPrimaryDark,
    error = GreenError,
    onError = White,
    errorContainer = GreenErrorContainer,
    onErrorContainer = GreenError,
    outline = GreenPrimary,
    outlineVariant = GreenLight,
    inverseSurface = TextDark,
    inverseOnSurface = GreenLight,
    inversePrimary = GreenAccent
)

@Composable
fun LocationSharingAppJPCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to use custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}