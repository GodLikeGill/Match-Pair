package com.godlike.cardpair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.godlike.cardpair.screens.GameView
import com.godlike.cardpair.screens.MainMenu
import com.godlike.cardpair.ui.theme.CardPairTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            CardPairTheme {
                var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
                var restartKey by remember { mutableIntStateOf(0) }
                when (currentScreen) {
                    Screen.MAIN_MENU -> MainMenu(onPlayClicked = { currentScreen = Screen.GAME_VIEW })
                    Screen.GAME_VIEW -> GameView(
                        key = restartKey,
                        onGameOverMainMenu = { currentScreen = Screen.MAIN_MENU },
                        onRestart = { restartKey++ }
                    )
                }
            }
        }
    }
}

enum class Screen {
    MAIN_MENU,
    GAME_VIEW
}