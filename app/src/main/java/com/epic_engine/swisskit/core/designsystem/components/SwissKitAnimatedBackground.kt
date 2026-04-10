package com.epic_engine.swisskit.core.designsystem.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.ui.theme.blueAnimation
import com.epic_engine.swisskit.core.ui.theme.greenAnimation
import com.epic_engine.swisskit.core.ui.theme.orangeAnimation
import com.epic_engine.swisskit.core.ui.theme.purpleAnimation
import com.epic_engine.swisskit.core.ui.theme.redAnimation
import com.epic_engine.swisskit.core.ui.theme.redBackground
import com.epic_engine.swisskit.core.ui.theme.redHome
import com.epic_engine.swisskit.core.ui.theme.yellowAnimation


@Composable
fun SwissKitAnimatedBackgroundView(
    modifier: Modifier = Modifier
) {
    var animateBackground by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withFrameMillis { }
        animateBackground = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Frame 0: gradiente estático — mismo rojo que el splash (#FB2A2A arriba)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(redHome, redBackground)
                )
            )
        }

        // Frame 1+: overlay con blur y rotación, entra después del primer paint
        if (animateBackground) {
            AnimatedOverlay()
        }
    }
}

@Composable
private fun AnimatedOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle_animation"
    )
    val blurRadius = with(LocalDensity.current) { 60.dp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = 0.25f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    renderEffect = RenderEffect
                        .createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                }
            }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val sweepGradient = Brush.sweepGradient(
            colors = listOf(
                redAnimation,
                orangeAnimation,
                yellowAnimation,
                greenAnimation,
                blueAnimation,
                purpleAnimation,
                redAnimation
            ),
            center = center
        )
        rotate(degrees = angle, pivot = center) {
            drawRect(brush = sweepGradient)
        }
    }
}
