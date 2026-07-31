// File: ui/theme/Theme.kt
package com.example.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// CompositionLocal untuk mengakses warna kustom di seluruh komponen
val LocalCalculatorColors = staticCompositionLocalOf { LightCalculatorColors }

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Pilih palet warna berdasarkan tema
    val colors = if (darkTheme) DarkCalculatorColors else LightCalculatorColors

    // Buat colorScheme Material3 dengan warna kustom
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = DarkPrimary,
            secondary = DarkSecondary,
            surface = DarkSurface,
            background = DarkBackground,
            onPrimary = DarkTextPrimary,
            onSecondary = DarkTextPrimary,
            onSurface = DarkTextPrimary,
            onBackground = DarkTextPrimary,
            error = DarkError
        )
    } else {
        lightColorScheme(
            primary = LightPrimary,
            secondary = LightSecondary,
            surface = LightSurface,
            background = LightBackground,
            onPrimary = LightTextPrimary,
            onSecondary = LightTextPrimary,
            onSurface = LightTextPrimary,
            onBackground = LightTextPrimary,
            error = LightError
        )
    }

    // Sediakan warna kustom melalui CompositionLocal dan MaterialTheme
    CompositionLocalProvider(LocalCalculatorColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CalculatorTypography  // dari Type.kt
        ) {
            content()
        }
    }
}