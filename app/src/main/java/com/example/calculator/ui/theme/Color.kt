package com.example.calculator.ui.theme

import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFFFFD60A)
val LightSecondary = Color(0xFFFF9800)
val LightBackground = Color(0xFFFFFDF5)
val LightSurface = Color(0xFFFFFFFF)
val LightDisplay = Color(0xFFFFF7D6)
val LightBorder = Color(0xFF111111)
val LightShadow = Color.Black
val LightTextPrimary = Color(0xFF111111)
val LightTextSecondary = Color(0xFF666666)
val LightError = Color(0xFFFF5A5F)

val LightNumberButton = Color(0xFFFFFFFF)
val LightFunctionButton = Color(0xFFF1F1F1)
val LightOperatorButton = LightPrimary
val LightEqualButton = LightSecondary

val DarkPrimary = Color(0xFFFFD60A)
val DarkSecondary = Color(0xFFFF9800)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF2C2C2C)
val DarkDisplay = Color(0xFF3A3A3A)
val DarkBorder = Color(0xFFFFFFFF)
val DarkShadow = Color.White
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFBDBDBD)
val DarkError = Color(0xFFFF6B6B)

val DarkNumberButton = Color(0xFF3A3A3A)
val DarkFunctionButton = Color(0xFF5A5A5A)
val DarkOperatorButton = DarkPrimary
val DarkEqualButton = DarkSecondary

data class CalculatorColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val display: Color,
    val border: Color,
    val shadow: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val error: Color,
    val numberButton: Color,
    val functionButton: Color,
    val operatorButton: Color,
    val equalButton: Color
)

val LightCalculatorColors = CalculatorColors(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    display = LightDisplay,
    border = LightBorder,
    shadow = LightShadow,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    error = LightError,
    numberButton = LightNumberButton,
    functionButton = LightFunctionButton,
    operatorButton = LightOperatorButton,
    equalButton = LightEqualButton
)

val DarkCalculatorColors = CalculatorColors(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    display = DarkDisplay,
    border = DarkBorder,
    shadow = DarkShadow,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    error = DarkError,
    numberButton = DarkNumberButton,
    functionButton = DarkFunctionButton,
    operatorButton = DarkOperatorButton,
    equalButton = DarkEqualButton
)