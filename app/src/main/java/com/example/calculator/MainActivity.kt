// File: MainActivity.kt
package com.example.calculator

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.ui.screens.CalculatorScreen
import com.example.calculator.ui.theme.CalculatorTheme
import com.example.calculator.ui.viewmodels.CalculatorViewModel
import com.example.calculator.ui.viewmodels.CalculatorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val factory = remember { CalculatorViewModelFactory(applicationContext) }
            val viewModel: CalculatorViewModel = viewModel(factory = factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            SetStatusBarStyle(isDarkTheme)

            CalculatorTheme(darkTheme = isDarkTheme) {
                CalculatorScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SetStatusBarStyle(isDarkTheme: Boolean) {
    val window = (LocalContext.current as? Activity)?.window
    SideEffect {
        window?.let {
            val insetsController = WindowCompat.getInsetsController(it, it.decorView)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
        }
    }
}