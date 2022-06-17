package com.slaviboy.clock.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Stable
data class Colors(
    val background: Color = Color.Transparent,
    val line90Deg: Color = Color.Transparent,
    val hoursArrow: Color = Color.Transparent,
    val secondsArrow: Color = Color.Transparent,
    val base: Color = Color.Transparent,
    val lighter: Color = Color.Transparent,
    val darker: Color = Color.Transparent,
    val baseLessTransparent: Color = Color.Transparent,
    val baseSemiTransparent: Color = Color.Transparent,
    val baseTransparent: Color = Color.Transparent,
    val textTime: Color = Color.Transparent,
    val textDate: Color = Color.Transparent,
    val textLogo: Color = Color.Transparent
) {
    private val animationSpec: AnimationSpec<Color> = tween(durationMillis = 1500)

    @Composable
    private fun animateColor(
        targetValue: Color,
        finishedListener: ((Color) -> Unit)? = null
    ) = animateColorAsState(targetValue = targetValue, animationSpec = animationSpec).value

    @Composable
    fun switch() = copy(
        background = animateColor(background),
        line90Deg = animateColor(line90Deg),
        hoursArrow = animateColor(hoursArrow),
        secondsArrow = animateColor(secondsArrow),
        base = animateColor(base),
        lighter = animateColor(lighter),
        darker = animateColor(darker),
        baseLessTransparent = animateColor(baseLessTransparent),
        baseSemiTransparent = animateColor(baseSemiTransparent),
        textTime = animateColor(textTime),
        textDate = animateColor(textDate),
        textLogo = animateColor(textLogo)
    )
}

val LocalColors = compositionLocalOf { Colors() }

val LightColorPalette = Colors(
    background = Color(0xFFECECF3),
    line90Deg = Color(0xFF9B9BB0),
    hoursArrow = Color(0xFF4F4F64),
    secondsArrow = Color(0xFF3B3BBF),
    base = Color(0xFFECECF3),
    lighter = Color(0xFFF7F7FB),
    darker = Color(0xFFD7D7E9),
    baseLessTransparent = Color(0x99ECECF3),
    baseSemiTransparent = Color(0x44ECECF3),
    baseTransparent = Color(0x00ECECF3),
    textTime = Color(0xFF12123B),
    textDate = Color(0xFFB8B8C2),
    textLogo = Color(0xFF9B9BB0)
)

val DarkColorPalette = Colors(
    background = Color(0xFF25252D),
    line90Deg = Color(0xFF9B9BB0),
    hoursArrow = Color(0xFFB8B8C7),
    secondsArrow = Color(0xFF3B3BBF),
    base = Color(0xFF25252D),
    lighter = Color(0xFF2B2B33),
    darker = Color(0xFF1F1F24),
    baseLessTransparent = Color(0x9925252D),
    baseSemiTransparent = Color(0x4425252D),
    baseTransparent = Color(0x0025252D),
    textTime = Color(0xFFF1F1F4),
    textDate = Color(0xFF4F4F64),
    textLogo = Color(0xFF9B9CB9)
)