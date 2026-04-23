package uk.ac.tees.mad.dealzone.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uk.ac.tees.mad.dealzone.ui.theme.*

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit           = {},
    onNavigateToAuth: () -> Unit           = {},
    checkLoginState: suspend () -> Boolean = { false }
) {
    val logoScale by rememberInfiniteTransition(label = "pulse")
        .animateFloat(
            initialValue  = 0.95f,
            targetValue   = 1.05f,
            animationSpec = infiniteRepeatable(
                animation  = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "logoScale"
        )

    val logoAlpha = remember { Animatable(0f) }
    val tagAlpha  = remember { Animatable(0f) }
    val dotAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(600))
        delay(200)
        tagAlpha.animateTo(1f, tween(500))
        delay(300)
        dotAlpha.animateTo(1f, tween(400))
        delay(800)
        val isLoggedIn = checkLoginState()
        if (isLoggedIn) onNavigateToHome() else onNavigateToAuth()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = 100.dp, y = (-150).dp)
                .alpha(0.06f)
                .clip(CircleShape)
                .background(Orange500)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-80).dp, y = 200.dp)
                .alpha(0.05f)
                .clip(CircleShape)
                .background(Orange600)
        )

        Column(
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center,
            modifier              = Modifier.padding(32.dp)
        ) {
            Box(
                modifier         = Modifier
                    .alpha(logoAlpha.value)
                    .scale(logoScale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Orange500.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Orange500, Orange600),
                                start  = Offset(0f, 0f),
                                end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚡", fontSize = 46.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text     = "DealZone",
                style    = TextStyle(
                    fontSize     = 42.sp,
                    fontWeight   = FontWeight.ExtraBold,
                    color        = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-1).sp,
                    shadow       = Shadow(
                        color      = Orange500.copy(alpha = 0.6f),
                        offset     = Offset(0f, 4f),
                        blurRadius = 12f
                    )
                ),
                modifier = Modifier.alpha(logoAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "Discover · Save · Share",
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color         = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    letterSpacing = 2.sp,
                    fontWeight    = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(tagAlpha.value)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier              = Modifier.alpha(dotAlpha.value),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(3) { idx -> AnimatedDot(delayMs = idx * 180) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text     = "Loading your deals…",
                style    = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier.alpha(dotAlpha.value)
            )
        }

        Text(
            text     = "v1.0.0",
            style    = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun AnimatedDot(delayMs: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val offsetY by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(
            animation  = tween(500, delayMillis = delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotOffset"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue  = 0.4f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(500, delayMillis = delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .alpha(alpha)
            .size(8.dp)
            .clip(CircleShape)
            .background(Orange500)
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Splash Screen (Light)", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, showSystemUi = true, name = "Splash Screen (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SplashScreenPreview() {
    DealZoneTheme {
        SplashScreen()
    }
}