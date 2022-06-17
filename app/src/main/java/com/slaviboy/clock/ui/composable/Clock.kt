package com.slaviboy.clock.ui.composable

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.PointF
import android.graphics.RadialGradient
import android.view.View
import android.widget.Space
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.slaviboy.clock.R
import com.slaviboy.clock.ui.theme.Colors
import com.slaviboy.clock.ui.theme.LocalColors
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import java.util.*

lateinit var linearGradient1: LinearGradient
lateinit var linearGradient2: LinearGradient
lateinit var radialGradient: RadialGradient
lateinit var blur: BlurMaskFilter
lateinit var paint: NativePaint

@Composable
fun AnimatedImage(
    modifier: Modifier, state: Boolean, width: Dp,
    contentScale: ContentScale = ContentScale.FillWidth,
    srcStateTrue: Int = R.drawable.ic_sun,
    srcStateFalse: Int = R.drawable.ic_moon,
    iconScale: Float = 0.6f,
    animationDuration: Int = 1000
) {

    val colors = LocalColors.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_light_button), contentDescription = "",
            colorFilter = ColorFilter.tint(colors.background, BlendMode.Modulate),
            modifier = Modifier
                .width(width),
            contentScale = contentScale
        )

        Crossfade(
            state,
            animationSpec = tween(animationDuration)
        ) { targetState ->
            Image(
                painter = painterResource(if (targetState) srcStateTrue else srcStateFalse), contentDescription = null,
                colorFilter = ColorFilter.tint(colors.secondsArrow),
                modifier = Modifier
                    .width(width.times(iconScale)),
                contentScale = contentScale
            )
        }
    }
}

@Composable
fun ClockWidget(isLightTheme: Boolean, useTickAnimation: Boolean, onChangeTickAnimation: () -> Unit, onChangeTheme: () -> Unit) {

    val colors = LocalColors.current
    val uriHandler = LocalUriHandler.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.wrapContentHeight().fillMaxWidth()
        ) {

            Clock(widthDp = 0.7.dw, useTickAnimation)

            Column(modifier = Modifier.wrapContentSize().align(Alignment.TopEnd).padding(end = 0.06.dw)) {
                AnimatedImage(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onChangeTheme.invoke()
                        },
                    width = 0.1.dw,
                    state = isLightTheme
                )
                Spacer(modifier = Modifier.height(0.03.dw))

                AnimatedImage(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onChangeTickAnimation.invoke()
                        },
                    width = 0.1.dw,
                    state = useTickAnimation,
                    srcStateTrue = R.drawable.ic_brifcase,
                    srcStateFalse = R.drawable.ic_crown,
                    iconScale = 0.5f
                )
            }

        }
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {

            val annotatedLinkString = buildAnnotatedString {
                pushStringAnnotation(tag = "url", annotation = "https://github.com/slaviboy")
                withStyle(
                    style = SpanStyle(
                        color = colors.textLogo,
                        fontSize = 0.04.sw,
                        fontFamily = PoppinsFont,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("slaviboy")
                }
                pop()
            }

            ClickableText(
                text = annotatedLinkString,
                modifier = Modifier.padding(bottom = 0.01.dw),
                onClick = {
                    annotatedLinkString
                        .getStringAnnotations("url", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
        }
    }
}


@Composable
fun SoftwareLayerComposable(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AndroidView(
        factory = { context ->
            ComposeView(context).apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
        },
        update = { composeView ->
            composeView.setContent(content)
        },
        modifier = modifier,
    )
}

fun NativeCanvas.drawClock(
    width: Float, paddingFact: Float, colors: Colors, useTickAnimation: Boolean,
    hours: Int, minutes: Int, seconds: Int, milliseconds: Int
) {

    val shrinkWidth = width * paddingFact
    val centerX = shrinkWidth / 2f
    val centerY = centerX
    val radius = width / 2f
    val offset = (shrinkWidth - width) / 2f

    // draw bottom gradient circle
    paint.maskFilter = blur
    paint.shader = linearGradient1
    drawCircle(centerX, centerY, radius, paint)

    // draw middle gradient circle
    paint.maskFilter = null
    paint.shader = linearGradient2
    drawCircle(centerX, centerY, radius * 0.85f, paint)

    // draw top gradient circle
    paint.maskFilter = null
    paint.shader = radialGradient
    drawCircle(centerX, centerY, radius * 0.8f, paint)

    // prepare for stroke drawing
    paint.apply {
        color = colors.line90Deg.toArgb()
        shader = null
        maskFilter = null
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = width * 0.005f
        strokeCap = android.graphics.Paint.Cap.ROUND
    }

    // draw the lines for 0h, 3h, 6h, 9h
    save()
    for (i in 0 until 4) {
        rotate(i * 90f, centerX, centerY)
        drawLine(centerX, width * 0.14f + offset, centerX, width * 0.21f + offset, paint)
    }
    restore()

    // draw hours arrow
    paint.apply {
        color = colors.hoursArrow.toArgb()
        strokeWidth = width * 0.018f
    }
    val initialAngleHours = (hours / 12f) * 360f + ((minutes / 60f) / 12f) * 360f
    save()
    rotate(initialAngleHours, centerX, centerY)
    drawLine(centerX, width * 0.29f + offset, centerX, centerY, paint)
    restore()

    // draw minutes arrow
    val initialAngleMinutes = (minutes / 60f) * 360f + ((seconds / 60f) / 60f) * 360f
    save()
    rotate(initialAngleMinutes, centerX, centerY)
    drawLine(centerX, width * 0.21f + offset, centerX, centerY, paint)
    restore()

    // draw seconds arrow
    paint.apply {
        color = colors.secondsArrow.toArgb()
        strokeWidth = width * 0.008f
    }
    val initialAngleSeconds = (seconds / 60f) * 360f + if (!useTickAnimation) {
        ((milliseconds / 1000f) / 60f) * 360f
    } else 0f
    save()
    rotate(initialAngleSeconds, centerX, centerY)
    drawLine(centerX, width * 0.21f + offset, centerX, width * 0.55f + offset, paint)
    restore()

    // draw the two circles in center
    paint.apply {
        color = colors.base.toArgb()
        style = android.graphics.Paint.Style.FILL
    }
    drawCircle(centerX, centerY, radius * 0.055f, paint)
    paint.color = colors.secondsArrow.toArgb()
    drawCircle(centerX, centerY, radius * 0.033f, paint)
}

@Composable
fun Clock(widthDp: Dp, useTickAnimation: Boolean) {

    val colors = LocalColors.current
    val density = LocalDensity.current
    val width = with(density) { widthDp.toPx() }
    val paddingFact = 1.1f
    val shrinkWidth = width * paddingFact
    val centerX = shrinkWidth / 2f
    val centerY = centerX
    val radius = width / 2f

    val infiniteTransition = rememberInfiniteTransition()
    val secondsAnimationState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000 * 60, easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val dx = centerX - 0.0
    val dy = centerY - 0.0
    val distance = radius / Math.sqrt(dx * dx + dy * dy).toFloat()
    val p1 = lerp(0f, 0f, centerX, centerY, distance)
    val p2 = lerp(width, width, centerX, centerY, distance)

    // change outside values with side effect
    SideEffect {
        linearGradient1 = LinearGradient(
            p2.x, p2.y, p1.x, p1.y,
            intArrayOf(colors.darker.toArgb(), colors.base.toArgb(), colors.lighter.toArgb()),
            floatArrayOf(0f, 0.5f, 1f), android.graphics.Shader.TileMode.CLAMP
        )
        linearGradient2 = LinearGradient(
            p1.x, p1.y, p2.x, p2.y,
            intArrayOf(colors.darker.toArgb(), colors.base.toArgb(), colors.lighter.toArgb()),
            floatArrayOf(0f, 0.5f, 1f), android.graphics.Shader.TileMode.CLAMP
        )
        radialGradient = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(colors.base.toArgb(), colors.baseLessTransparent.toArgb(), colors.baseSemiTransparent.toArgb(), colors.baseTransparent.toArgb()),
            floatArrayOf(0f, 0.75f, 0.8f, 1f), android.graphics.Shader.TileMode.CLAMP
        )
        blur = BlurMaskFilter(
            width * 0.04f,
            BlurMaskFilter.Blur.NORMAL
        )
        paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.FILL
        }
    }

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val pmam = if (c.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    val day = c.get(Calendar.DAY_OF_MONTH)
    var hours = c.get(Calendar.HOUR_OF_DAY)
    var minutes = c.get(Calendar.MINUTE)
    var seconds = c.get(Calendar.SECOND)
    var milliseconds = c.get(Calendar.MILLISECOND)
    if (secondsAnimationState.value > 0f) {

        // to keep range [0,12]h instead of [0,24]h
        hours = c.get(Calendar.HOUR_OF_DAY)
        if (hours > 12) {
            hours -= 12
        }
        minutes = c.get(Calendar.MINUTE)
        seconds = c.get(Calendar.SECOND)
        milliseconds = c.get(Calendar.MILLISECOND)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentSize()
    ) {

        Spacer(modifier = Modifier.height(0.14.dw))
        SoftwareLayerComposable(
            modifier = Modifier
                .size(widthDp.times(paddingFact), widthDp.times(paddingFact))
        ) {
            Box(
                modifier = Modifier
                    .size(widthDp, widthDp)
                    .align(Alignment.CenterHorizontally)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            if (secondsAnimationState.value > -1f) {
                                canvas.nativeCanvas.drawClock(
                                    width, paddingFact, colors,
                                    useTickAnimation, hours, minutes, seconds, milliseconds
                                )
                            }
                        }
                    }
            ) {}
        }

        //Spacer(modifier = Modifier.height(0.1.dw))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = pmam, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(bottom = 0.1.dw, start = 0.1.dw)
                    .wrapContentSize(),
                fontSize = 0.033.sw,
                fontFamily = PoppinsFont,
                color = colors.textTime
            )
            Text(
                text = "${if (hours < 10) "0" else ""}$hours:${if (minutes < 10) "0" else ""}$minutes",
                modifier = Modifier
                    .wrapContentSize(),
                fontSize = 0.15.sw,
                fontFamily = PoppinsFont,
                fontWeight = FontWeight.Medium,
                color = colors.textTime
            )
            Text(
                text = "$day $month $year", modifier = Modifier
                    .padding(top = 0.24.dw)
                    .wrapContentSize(),
                fontSize = 0.04.sw,
                fontFamily = PoppinsFont,
                fontWeight = FontWeight.Medium,
                color = colors.textDate
            )
        }
    }
}

fun lerp(x1: Float, y1: Float, x2: Float, y2: Float, dist: Float): PointF {
    return PointF(x1 + dist * (x2 - x1), y1 + dist * (y2 - y1))
}
