package com.epic_engine.swisskit.ui.components

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.ui.theme.blueAnimation
import com.epic_engine.swisskit.ui.theme.greenAnimation
import com.epic_engine.swisskit.ui.theme.orangeAnimation
import com.epic_engine.swisskit.ui.theme.purpleAnimation
import com.epic_engine.swisskit.ui.theme.redAnimation
import com.epic_engine.swisskit.ui.theme.redBackground
import com.epic_engine.swisskit.ui.theme.redHome
import com.epic_engine.swisskit.ui.theme.yellowAnimation

@Composable
fun AnimatedBackgroundView(
    animateBackground: Boolean = true,
    modifier: Modifier = Modifier
) {
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

    val density = LocalDensity.current
    val blurRadius = with(density) { 60.dp.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        // Primer gradiente: Linear de redHome a redBackground
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(redHome, redBackground)
                )
            )
        }

        // Segundo gradiente: Angular animado con opacidad y blur
        if (animateBackground) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.25f
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            renderEffect = RenderEffect
                                .createBlurEffect(
                                    blurRadius,
                                    blurRadius,
                                    Shader.TileMode.CLAMP
                                )
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

                rotate(
                    degrees = angle,
                    pivot = center
                ) {
                    drawRect(brush = sweepGradient)
                }
            }
        }
    }
}
