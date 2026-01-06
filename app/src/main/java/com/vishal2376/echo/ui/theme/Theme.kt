package com.vishal2376.echo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CatppuccinMochaDarkScheme = darkColorScheme(
    primary = CatppuccinMocha.Mauve,
    onPrimary = CatppuccinMocha.Base,
    primaryContainer = CatppuccinMocha.Mauve,
    onPrimaryContainer = CatppuccinMocha.Base,

    secondary = CatppuccinMocha.Pink,
    onSecondary = CatppuccinMocha.Base,
    secondaryContainer = CatppuccinMocha.Surface0,
    onSecondaryContainer = CatppuccinMocha.Pink,

    tertiary = CatppuccinMocha.Teal,
    onTertiary = CatppuccinMocha.Base,
    tertiaryContainer = CatppuccinMocha.Surface0,
    onTertiaryContainer = CatppuccinMocha.Teal,

    error = CatppuccinMocha.Red,
    onError = CatppuccinMocha.Base,
    errorContainer = CatppuccinMocha.Red.copy(alpha = 0.2f),
    onErrorContainer = CatppuccinMocha.Red,

    background = CatppuccinMocha.Base,
    onBackground = CatppuccinMocha.Text,

    surface = CatppuccinMocha.Surface0,
    onSurface = CatppuccinMocha.Text,
    surfaceVariant = CatppuccinMocha.Surface1,
    onSurfaceVariant = CatppuccinMocha.Subtext0,

    outline = CatppuccinMocha.Overlay0,
    outlineVariant = CatppuccinMocha.Surface2,

    inverseSurface = CatppuccinMocha.Text,
    inverseOnSurface = CatppuccinMocha.Base,
    inversePrimary = CatppuccinMocha.Mauve,

    surfaceTint = CatppuccinMocha.Mauve,
    scrim = CatppuccinMocha.Crust
)

@Composable
fun EchoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = CatppuccinMochaDarkScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CatppuccinMocha.Base.toArgb()
            window.navigationBarColor = CatppuccinMocha.Base.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}