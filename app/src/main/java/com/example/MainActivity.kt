package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScreen
import com.example.ui.history.HistoryScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: MainViewModel = viewModel()
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val historyList by viewModel.historyList.collectAsState()

                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.DASHBOARD -> {
                                MainScreen(viewModel = viewModel)
                            }
                            AppScreen.HISTORY -> {
                                HistoryScreen(
                                    measurements = historyList,
                                    onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                                    onDeleteMeasurement = { entity ->
                                        viewModel.deleteMeasurement(entity)
                                    },
                                    onClearAll = {
                                        viewModel.clearAllMeasurements()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
