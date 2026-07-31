// File: ui/viewmodels/CalculatorViewModel.kt
package com.example.calculator.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat

data class CalculatorUiState(
    val expression: String = "",
    val resultText: String = "0"
)

// ========== DATA CLASS UNTUK HISTORY ==========
data class HistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CalculatorViewModel(
    private val context: Context
) : ViewModel() {

    // ========== STATE TEMA ==========
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        val prefs = context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)
        val savedTheme = prefs.getBoolean("dark_theme", false)
        _isDarkTheme.value = savedTheme
    }

    fun toggleTheme() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        val prefs = context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("dark_theme", newValue).apply()
    }

    // ========== STATE HISTORY ==========
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _isHistoryVisible = MutableStateFlow(false)
    val isHistoryVisible: StateFlow<Boolean> = _isHistoryVisible.asStateFlow()

    fun toggleHistory() {
        _isHistoryVisible.value = !_isHistoryVisible.value
    }

    fun clearHistory() {
        _history.value = emptyList()
        // Simpan ke SharedPreferences jika ingin persistensi (opsional)
        // saveHistoryToPrefs()
    }

    private fun addToHistory(expression: String, result: String) {
        val item = HistoryItem(expression, result)
        // Simpan maksimal 20 item, yang terbaru di atas
        _history.update { listOf(item) + it.take(19) }
        // Simpan ke SharedPreferences jika ingin persistensi
        // saveHistoryToPrefs()
    }

    // Untuk reuse ekspresi dari history
    fun appendExpression(expr: String) {
        // Bersihkan jika ada "=" di akhir
        val cleanExpr = expr.replace(" =", "").trim()
        if (cleanExpr.isNotEmpty()) {
            expression = cleanExpr
            currentInput = cleanExpr
            resultText = cleanExpr
            _uiState.value = CalculatorUiState(expression, resultText)
        }
    }

    // ========== STATE KALKULATOR ==========
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var currentInput = ""
    private var expression = ""
    private var resultText = "0"

    fun onButtonClick(tag: String) {
        when (tag) {
            in "0".."9", "." -> handleNumber(tag)
            "+", "-", "×", "÷" -> handleOperator(tag)
            "%" -> handlePercent()
            "AC" -> clearAll()
            "⌫" -> backspace()
            "=" -> calculateResult()
            "()" -> handleParentheses()
            else -> {}
        }
        _uiState.value = CalculatorUiState(expression, resultText)
    }

    // ---------- Handler Fungsi ----------
    private fun handleNumber(value: String) {
        if (currentInput == "Error") {
            currentInput = ""
        }
        if (value == "." && currentInput.contains(".")) return
        if (currentInput.length >= 15) return
        currentInput += value
        expression += value
        resultText = currentInput
    }

    private fun handleOperator(op: String) {
        if (currentInput.isEmpty() && expression.isNotEmpty()) {
            val lastChar = expression.last()
            if (lastChar in setOf('+', '-', '×', '÷')) {
                expression = expression.dropLast(1) + op
                return
            }
        }
        if (currentInput.isEmpty()) return
        currentInput = ""
        expression += op
        resultText = expression
    }

    private fun handlePercent() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                val percent = value / 100.0
                currentInput = percent.toString()
                expression = expression.replace(Regex("[0-9.]+$"), currentInput)
                resultText = currentInput
            }
        }
    }

    private fun clearAll() {
        currentInput = ""
        expression = ""
        resultText = "0"
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            expression = expression.dropLast(1)
            resultText = currentInput.ifEmpty { "0" }
        } else if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            resultText = expression
        }
    }

    private fun handleParentheses() {
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        if (openCount > closeCount) {
            currentInput += ")"
            expression += ")"
            resultText = expression
        } else {
            currentInput += "("
            expression += "("
            resultText = expression
        }
    }

    private fun calculateResult() {
        if (expression.isEmpty()) return
        try {
            val result = evaluateExpression(expression)
            val formatted = formatNumber(result)
            val exprCopy = expression  // simpan ekspresi sebelum diubah
            resultText = formatted
            expression += " ="
            currentInput = formatted

            // Tambahkan ke history
            addToHistory(exprCopy, formatted)
        } catch (e: Exception) {
            resultText = "Error"
            currentInput = "Error"
        }
    }

    // ---------- Evaluasi Ekspresi (Shunting‑Yard) ----------
    private fun evaluateExpression(expr: String): Double {
        val clean = expr.replace("×", "*").replace("÷", "/")
        return evaluate(clean)
    }

    private fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)
        val postfix = infixToPostfix(tokens)
        return evaluatePostfix(postfix)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val ch = expr[i]
            when {
                ch.isDigit() || ch == '.' -> {
                    var num = ""
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        num += expr[i]
                        i++
                    }
                    tokens.add(num)
                    continue
                }
                ch in "+-*/()" -> tokens.add(ch.toString())
                else -> { /* abaikan */ }
            }
            i++
        }
        return tokens
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = mutableListOf<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> stack.add(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        output.add(stack.removeAt(stack.size - 1))
                    }
                    if (stack.isNotEmpty()) stack.removeAt(stack.size - 1)
                }
                token in precedence -> {
                    while (stack.isNotEmpty() && stack.last() in precedence &&
                        precedence[stack.last()]!! >= precedence[token]!!
                    ) {
                        output.add(stack.removeAt(stack.size - 1))
                    }
                    stack.add(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            output.add(stack.removeAt(stack.size - 1))
        }
        return output
    }

    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = mutableListOf<Double>()
        for (token in postfix) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.add(num)
            } else {
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                val result = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) throw ArithmeticException("Division by zero")
                        a / b
                    }
                    else -> 0.0
                }
                stack.add(result)
            }
        }
        return stack.lastOrNull() ?: 0.0
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            val df = DecimalFormat("#.##########")
            df.format(value)
        }
    }
}