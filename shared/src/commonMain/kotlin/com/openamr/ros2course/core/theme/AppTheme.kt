package com.openamr.ros2course.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = DeepBlack,
    secondary = AmberSecondary,
    onSecondary = DeepBlack,
    tertiary = AmberTertiary,
    onTertiary = DeepBlack,
    background = DeepBlack,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextGrey,
    error = ErrorRed,
    onError = DeepBlack,
)

@Composable
fun CourseAppTheme(content: @Composable () -> Unit) {
    // Force dark theme as per requested screenshots
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
