package com.epic_engine.swisskit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.epic_engine.swisskit.core.designsystem.components.SwissKitAnimatedBackgroundView
import com.epic_engine.swisskit.core.ui.theme.SwissKitTheme
import com.epic_engine.swisskit.navigation.SwissKitNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwissKitTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    SwissKitAnimatedBackgroundView()
                    SwissKitNavGraph()
                }
            }
        }
    }
}
