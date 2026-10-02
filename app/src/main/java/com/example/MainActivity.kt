package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.physics.LiquidViewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LiquidLabScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.widgets.GlassWidgetFlowTicker

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import com.example.ui.theme.ThemeManager

enum class AppScreen {
    DASHBOARD,
    LIQUID_LAB
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        GlassWidgetFlowTicker.startFlowTicker(this)
        setContent {
            MyApplicationTheme {
                val currentPreset by ThemeManager.currentPreset.collectAsStateWithLifecycle()
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(currentPreset.backgroundGradient)),
                    color = Color.Transparent
                ) {
                    GlassVibeApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        GlassWidgetFlowTicker.startFlowTicker(this)
    }
}

@Composable
fun GlassVibeApp(liquidViewModel: LiquidViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    val physicsState by liquidViewModel.physicsState.collectAsStateWithLifecycle()

    when (currentScreen) {
        AppScreen.DASHBOARD -> {
            DashboardScreen(
                physicsState = physicsState,
                onOpenLiquidLab = { currentScreen = AppScreen.LIQUID_LAB }
            )
        }
        AppScreen.LIQUID_LAB -> {
            BackHandler {
                currentScreen = AppScreen.DASHBOARD
            }
            LiquidLabScreen(
                physicsState = physicsState,
                onBack = { currentScreen = AppScreen.DASHBOARD }
            )
        }
    }
}
