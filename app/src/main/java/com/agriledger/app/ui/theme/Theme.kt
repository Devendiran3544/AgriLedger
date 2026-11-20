package com.agriledger.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryForest,
    onPrimary = SurfaceLight,
    primaryContainer = AccentGreenLight,
    onPrimaryContainer = PrimaryForestDark,
    secondary = PrimaryForestVariant,
    onSecondary = SurfaceLight,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = PrimaryForest,
    tertiary = AccentGreen,
    onTertiary = SurfaceLight,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorCrimson,
    onError = SurfaceLight,
    errorContainer = ErrorCrimsonBg,
    onErrorContainer = ErrorCrimson,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = PrimaryForestDark,
    primaryContainer = PrimaryForestVariant,
    onPrimaryContainer = TextLight,
    secondary = AccentGreenDark,
    onSecondary = TextLight,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = AccentGreenLight,
    tertiary = WarningGold,
    onTertiary = TextDark,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorCrimson,
    onError = SurfaceLight,
    errorContainer = ErrorCrimsonDarkBg,
    onErrorContainer = ErrorCrimson,
    outline = BorderDark
)

@Composable
fun AgriLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
