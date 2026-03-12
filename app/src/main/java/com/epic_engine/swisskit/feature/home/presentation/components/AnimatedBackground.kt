package com.epic_engine.swisskit.feature.home.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import com.epic_engine.swisskit.ui.theme.blueAnimation
import com.epic_engine.swisskit.ui.theme.greenAnimation
import com.epic_engine.swisskit.ui.theme.orangeAnimation
import com.epic_engine.swisskit.ui.theme.purpleAnimation
import com.epic_engine.swisskit.ui.theme.redAnimation
import com.epic_engine.swisskit.ui.theme.yellowAnimation
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedBackground(modifier: Modifier = Modifier) {

    val animationColors = listOf(
        redAnimation, orangeAnimation, yellowAnimation,
        greenAnimation, blueAnimation, purpleAnimation
    )

    val infiniteTransition = rememberInfiniteTransition(label = "home_bg")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_angle"
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = animationColors.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_shift"
    )

    val fraction = colorShift - colorShift.toLong()
    val idx1 = colorShift.toInt() % animationColors.size
    val idx2 = (colorShift.toInt() + 1) % animationColors.size
    val idx3 = (colorShift.toInt() + 2) % animationColors.size
    val idx4 = (colorShift.toInt() + 3) % animationColors.size

    val startColor = lerp(animationColors[idx1], animationColors[idx2], fraction)
    val endColor   = lerp(animationColors[idx3], animationColors[idx4], fraction)

    Canvas(modifier = modifier) {
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    startColor.copy(alpha = 0.75f),
                    endColor.copy(alpha = 0.90f)
                ),
                start = Offset(
                    x = center.x * (1f + cosA),
                    y = center.y * (1f + sinA)
                ),
                end = Offset(
                    x = center.x * (1f - cosA),
                    y = center.y * (1f - sinA)
                )
            ),
            size = size
        )
    }
}
