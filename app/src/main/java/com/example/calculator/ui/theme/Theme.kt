package com.example.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalCalculatorColors = staticCompositionLocalOf { LightCalculatorColors }

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkCalculatorColors else LightCalculatorColors

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

    CompositionLocalProvider(LocalCalculatorColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CalculatorTypography
        ) {
            content()
        }
    }
}