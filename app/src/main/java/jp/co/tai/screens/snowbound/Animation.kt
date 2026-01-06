package jp.co.tai.screens.snowbound

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

suspend fun animateFallOfExisting(
    dropSteps: IntArray,
    randomizedElements: List<Int?>,
    cellHeightPx: Float,
    offsets: List<Animatable<Float, AnimationVector1D>>,
    coroutineScope: CoroutineScope
) {
    if (cellHeightPx <= 0f) return

    val jobs = mutableListOf<Job>()
    val duration = 250

    for (i in dropSteps.indices) {
        val steps = dropSteps[i]
        if (steps > 0 && randomizedElements[i] != null) {
            val distance = cellHeightPx * steps
            jobs += coroutineScope.launch {
                offsets[i].snapTo(0f)
                offsets[i].animateTo(
                    targetValue = distance,
                    animationSpec = tween(duration, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    jobs.joinAll()
}

suspend fun animateFallOfNew(
    newIndices: List<Int>,
    cellHeightPx: Float,
    offsets: List<Animatable<Float, AnimationVector1D>>,
    coroutineScope: CoroutineScope
) {
    if (cellHeightPx <= 0f) return

    val jobs = mutableListOf<Job>()
    val duration = 250

    newIndices.forEach { idx ->
        jobs += coroutineScope.launch {
            offsets[idx].snapTo(-cellHeightPx)
            offsets[idx].animateTo(
                targetValue = 0f,
                animationSpec = tween(duration, easing = FastOutSlowInEasing)
            )
        }
    }

    jobs.joinAll()
}