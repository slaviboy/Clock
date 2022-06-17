package com.slaviboy.clock

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.slaviboy.clock.ui.composable.ClockWidget
import com.slaviboy.clock.ui.theme.Colors
import com.slaviboy.clock.ui.theme.DarkColorPalette
import com.slaviboy.clock.ui.theme.LightColorPalette
import com.slaviboy.clock.ui.theme.LocalColors
import com.slaviboy.composeunits.initSize
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSize()
        installSplashScreen()
        setContent {

            var isLightTheme by remember {
                mutableStateOf(true)
            }

            var useTickAnimation by remember {
                mutableStateOf(true)
            }

            // use CompositionLocalProvider to pass the colors to all descendants
            val animatedColors = (if (isLightTheme) LightColorPalette else DarkColorPalette).switch()
            CompositionLocalProvider(LocalColors provides animatedColors) {
                ClockWidget(isLightTheme, useTickAnimation,
                    onChangeTickAnimation = {
                        useTickAnimation = !useTickAnimation
                    }, onChangeTheme = {
                        isLightTheme = !isLightTheme
                    })
            }
        }
        hideSystemBars()
    }

    private fun hideSystemBars() {

        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}
