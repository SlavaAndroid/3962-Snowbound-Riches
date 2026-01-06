package jp.co.tai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import jp.co.tai.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.font)),
        fontWeight = FontWeight.Normal,
        fontSize = 46.sp,
        color = Color.White
    ).copy(OrangeGradient),
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.font)),
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        color = Orange
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.font)),
        fontWeight = FontWeight.Normal,
        fontSize = 23.sp,
        color = Color.White
    ).copy(OrangeGradient),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.font)),
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        color = Color.White
    ).copy(OrangeGradient),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.font)),
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = Color.White
    ).copy(OrangeGradient),
    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.font)),
        fontWeight = FontWeight.Normal,
        fontSize = 19.sp,
        color = Orange
    ),
)