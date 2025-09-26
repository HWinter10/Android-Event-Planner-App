// File: ui/theme/Theme.kt
package com.example.eventplanner.ui.theme

import androidx.compose.material.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColors(
    primary = Blush,
    primaryVariant = MaroonPink,
    secondary = RoseGold,
    secondaryVariant = MaroonPink,
    background = BlushLight,
    surface = White,
    error = ErrorRed,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,
    onError = White
)

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun EventPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
