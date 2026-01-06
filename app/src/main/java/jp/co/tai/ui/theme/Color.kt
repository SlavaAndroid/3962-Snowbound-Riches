package jp.co.tai.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BlueDark = Color(0xFF090D26)
val Orange = Color(0xFFFF8C00)
val OrangeGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.3f to Color(0xFFFF4D00),
        0.8f to Color(0xFFFFFF00)
    )
)
val YellowGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.3f to Color(0xFFFFFF00),
        0.8f to Color(0xFFFF4D00)
    )
)
val BlueGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.3f to Color(0xFF000F93),
        0.8f to Color(0xFF3649F3)
    )
)
val GrayGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFFC0C0C0),
        1.0f to Color(0xFF5B5B5B)
    )
)