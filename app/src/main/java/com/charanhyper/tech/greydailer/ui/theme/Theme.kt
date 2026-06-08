package com.charanhyper.tech.greydailer.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val WhiteColorScheme = lightColorScheme(
    primary = Pink,
    onPrimary = White,
    primaryContainer = PinkLight,
    onPrimaryContainer = NearBlack,
    secondary = PinkDark,
    onSecondary = White,
    secondaryContainer = PinkSoft,
    onSecondaryContainer = NearBlack,
    tertiary = AccentGreen,
    onTertiary = White,
    tertiaryContainer = LightGreen,
    onTertiaryContainer = NearBlack,
    background = White,
    onBackground = NearBlack,
    surface = White,
    onSurface = NearBlack,
    surfaceVariant = OffWhite,
    onSurfaceVariant = DarkGray,
    error = AccentRed,
    onError = White,
    errorContainer = LightRed,
    onErrorContainer = AccentRed,
    outline = LightGray,
    outlineVariant = LightGray,
    surfaceContainerLowest = White,
    surfaceContainerLow = OffWhite,
    surfaceContainer = OffWhite,
    surfaceContainerHigh = LightGray,
    surfaceContainerHighest = LightGray,
)

@Composable
fun GreydailerTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                val window = context.window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = WhiteColorScheme,
        typography = Typography,
        content = content
    )
}