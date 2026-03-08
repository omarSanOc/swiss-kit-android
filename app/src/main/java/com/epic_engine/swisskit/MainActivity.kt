package com.epic_engine.swisskit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.epic_engine.swisskit.navigation.SwissKitNavGraph
import com.epic_engine.swisskit.ui.components.AnimatedBackgroundView
import com.epic_engine.swisskit.ui.theme.SwissKitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwissKitTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedBackgroundView()
                    SwissKitNavGraph()
                }
            }
        }
    }
}
