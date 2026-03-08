package uk.ac.tees.mad.dealzone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange500     = Color(0xFFFF6B00)
val Orange600     = Color(0xFFE55A00)
val Orange100     = Color(0xFFFFE0CC)
val DeepCharcoal  = Color(0xFF1A1A2E)
val SurfaceDark   = Color(0xFF16213E)
val CardDark      = Color(0xFF0F3460)
val White         = Color(0xFFFFFFFF)
val OffWhite      = Color(0xFFF8F4F0)
val TextSecondary = Color(0xFF9E9E9E)
val ErrorRed      = Color(0xFFE53935)
val SuccessGreen  = Color(0xFF43A047)

private val DarkColors = darkColorScheme(
    primary          = Orange500,
    onPrimary        = White,
    primaryContainer = Orange600,
    secondary        = Orange100,
    onSecondary      = DeepCharcoal,
    background       = DeepCharcoal,
    onBackground     = White,
    surface          = SurfaceDark,
    onSurface        = White,
    surfaceVariant   = CardDark,
    error            = ErrorRed,
    onError          = White
)

private val LightColors = lightColorScheme(
    primary          = Orange500,
    onPrimary        = White,
    primaryContainer = Orange100,
    secondary        = Orange600,
    onSecondary      = White,
    background       = OffWhite,
    onBackground     = DeepCharcoal,
    surface          = White,
    onSurface        = DeepCharcoal,
    surfaceVariant   = Color(0xFFF5F5F5),
    error            = ErrorRed,
    onError          = White
)

@Composable
fun DealZoneTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = Typography,
        content     = content
    )
}