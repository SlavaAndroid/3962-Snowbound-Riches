package jp.co.tai.screens.loading.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

data class SnowflakeModel(
    val id: Int,
    val x: Float,
    val animY: Animatable<Float, AnimationVector1D>,
    val rotation: Float = 0f,
    var isVisible: Boolean = true
)
