package com.example.calculator.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat
import org.json.JSONArray
import org.json.JSONObject

enum class CalculatorState {
    INPUT,
    RESULT_SHOWN
}

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String = "",
    val resultText: String = "0",
    val state: CalculatorState = CalculatorState.INPUT
)

data class HistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CalculatorViewModel(
    private val context: Context
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _isHistoryVisible = MutableStateFlow(false)
    val isHistoryVisible: StateFlow<Boolean> = _isHistoryVisible.asStateFlow()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkTheme.value = prefs.getBoolean("dark_theme", false)
        loadHistoryFromPrefs()
    }

    fun toggleTheme() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean("dark_theme", newValue).apply()
    }

    fun toggleHistory() {
        _isHistoryVisible.value = !_isHistoryVisible.value
    }

    fun clearHistory() {
        _history.value = emptyList()
        saveHistoryToPrefs()
    }

    fun appendExpression(expr: String) {
        val cleanExpr = expr.replace(" =", "").trim()
        if (cleanExpr.isNotEmpty()) {
            _uiState.value = CalculatorUiState(
                expression = cleanExpr,
                previewResult = "",
                resultText = "0",
                state = CalculatorState.INPUT
            )
            updatePreview(cleanExpr)
        }
    }

    fun onDigitClick(digit: String) {
        val current = _uiState.value
        if (current.state == CalculatorState.RESULT_SHOWN) {
            _uiState.value = CalculatorUiState(
                expression = digit,
                previewResult = "",
                state = CalculatorState.INPUT
            )
            updatePreview(digit)
            return
        }
        val newExpression = current.expression + digit
        _uiState.value = current.copy(
            expression = newExpression,
            state = CalculatorState.INPUT
        )
        updatePreview(newExpression)
    }

    fun onOperatorClick(operator: String) {
        val current = _uiState.value
        if (current.state == CalculatorState.RESULT_SHOWN) {
            val result = current.resultText
            val newExpression = "$result $operator "
            _uiState.value = CalculatorUiState(
                expression = newExpression,
                previewResult = "",
                state = CalculatorState.INPUT
            )
            return
        }

        val currentExpr = current.expression
        if (currentExpr.isNotEmpty()) {
            val trimmedExpr = currentExpr.trim()
            if (trimmedExpr.isNotEmpty() && trimmedExpr.last() in setOf('+', '-', '×', '÷')) {
                val base = trimmedExpr.dropLast(1).trimEnd()
                val newExpr = if (base.isNotEmpty()) "$base $operator " else "$operator "
                _uiState.value = current.copy(
                    expression = newExpr,
                    previewResult = "",
                    state = CalculatorState.INPUT
                )
                return
            }
        }

        val newExpression = if (currentExpr.isNotEmpty() && currentExpr.last().isDigit()) {
            "$currentExpr $operator "
        } else {
            "$currentExpr$operator "
        }
        _uiState.value = current.copy(
            expression = newExpression,
            previewResult = "",
            state = CalculatorState.INPUT
        )
    }

    fun onEqualClick() {
        val current = _uiState.value
        val expression = current.expression
        if (expression.isEmpty()) return
        try {
            val result = evaluateExpression(expression)
            val formatted = formatNumber(result)
            addToHistory(expression, formatted)
            _uiState.value = CalculatorUiState(
                expression = formatted,
                previewResult = "",
                resultText = formatted,
                state = CalculatorState.RESULT_SHOWN
            )
        } catch (e: Exception) {
            _uiState.value = current.copy(
                resultText = "Error",
                previewResult = ""
            )
        }
    }

    fun onDeleteClick() {
        val current = _uiState.value
        if (current.state == CalculatorState.RESULT_SHOWN) {
            val currentResult = current.expression
            if (currentResult.length > 1) {
                val newResult = currentResult.dropLast(1)
                _uiState.value = current.copy(
                    expression = newResult,
                    resultText = newResult,
                    state = CalculatorState.RESULT_SHOWN
                )
            } else {
                _uiState.value = CalculatorUiState(
                    expression = "0",
                    resultText = "0",
                    state = CalculatorState.RESULT_SHOWN
                )
            }
            return
        }

        val currentExpr = current.expression
        if (currentExpr.isNotEmpty()) {
            val newExpr = currentExpr.dropLast(1)
            _uiState.value = current.copy(
                expression = newExpr,
                state = CalculatorState.INPUT
            )
            updatePreview(newExpr)
        }
    }

    fun onClearClick() {
        _uiState.value = CalculatorUiState(
            expression = "",
            previewResult = "",
            resultText = "0",
            state = CalculatorState.INPUT
        )
    }

    fun onDecimalClick() {
        val current = _uiState.value
        if (current.state == CalculatorState.RESULT_SHOWN) {
            _uiState.value = CalculatorUiState(
                expression = "0.",
                previewResult = "",
                state = CalculatorState.INPUT
            )
            updatePreview("0.")
            return
        }

        val currentExpr = current.expression
        val lastNumber = currentExpr.split(Regex("[+\\-×÷()]")).lastOrNull() ?: ""
        if (lastNumber.contains(".")) return

        val newExpr = if (currentExpr.isEmpty() || currentExpr.last() in setOf('+', '-', '×', '÷', '(')) {
            currentExpr + "0."
        } else {
            currentExpr + "."
        }

        _uiState.value = current.copy(
            expression = newExpr,
            state = CalculatorState.INPUT
        )
        updatePreview(newExpr)
    }

    fun onParenthesesClick() {
        val current = _uiState.value
        if (current.state == CalculatorState.RESULT_SHOWN) return

        val currentExpr = current.expression
        val openCount = currentExpr.count { it == '(' }
        val closeCount = currentExpr.count { it == ')' }

        val newExpr = if (openCount > closeCount) {
            currentExpr + ")"
        } else {
            currentExpr + "("
        }

        _uiState.value = current.copy(
            expression = newExpr,
            state = CalculatorState.INPUT
        )
        updatePreview(newExpr)
    }

    fun onPercentClick() {
        val current = _uiState.value
        if (current.state == CalculatorState.RESULT_SHOWN) {
            val value = current.resultText.toDoubleOrNull()
            if (value != null) {
                val percent = value / 100.0
                val formatted = formatNumber(percent)
                _uiState.value = CalculatorUiState(
                    expression = formatted,
                    resultText = formatted,
                    state = CalculatorState.RESULT_SHOWN
                )
            }
            return
        }

        val currentExpr = current.expression
        if (currentExpr.isNotEmpty() && currentExpr.last().isDigit()) {
            val newExpr = currentExpr + "%"
            _uiState.value = current.copy(
                expression = newExpr,
                state = CalculatorState.INPUT
            )
            updatePreview(newExpr)
        }
    }

    private fun updatePreview(expression: String) {
        if (expression.isEmpty()) {
            _uiState.value = _uiState.value.copy(previewResult = "")
            return
        }

        val trimmed = expression.trim()
        if (trimmed.isEmpty() ||
            trimmed.last() in setOf('+', '-', '×', '÷', '%', '(') ||
            trimmed.count { it == '(' } > trimmed.count { it == ')' }) {
            _uiState.value = _uiState.value.copy(previewResult = "")
            return
        }

        try {
            val result = evaluateExpression(expression)
            _uiState.value = _uiState.value.copy(previewResult = formatNumber(result))
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(previewResult = "")
        }
    }

    private fun addToHistory(expression: String, result: String) {
        val item = HistoryItem(expression, result)
        _history.value = listOf(item) + _history.value.take(19)
        saveHistoryToPrefs()
    }

    private fun loadHistoryFromPrefs() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return
        try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<HistoryItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val expression = obj.getString("expression")
                val result = obj.getString("result")
                val timestamp = obj.getLong("timestamp")
                list.add(HistoryItem(expression, result, timestamp))
            }
            _history.value = list
        } catch (e: Exception) {
        }
    }

    private fun saveHistoryToPrefs() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        _history.value.forEach {
            val obj = JSONObject()
            obj.put("expression", it.expression)
            obj.put("result", it.result)
            obj.put("timestamp", it.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    private fun evaluateExpression(expr: String): Double {
        val clean = expr.replace("×", "*").replace("÷", "/").replace(" ", "")
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
                ch in "+-*/()%" -> {
                    if (ch == '%') {
                        if (tokens.isNotEmpty() && tokens.last().toDoubleOrNull() != null) {
                            val num = tokens.removeAt(tokens.size - 1)
                            val percent = num.toDouble() / 100.0
                            tokens.add(percent.toString())
                        }
                    } else {
                        tokens.add(ch.toString())
                    }
                }
                else -> {}
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
                        precedence[stack.last()]!! >= precedence[token]!!) {
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
            DecimalFormat("#.##########").format(value)
        }
    }

    companion object {
        private const val PREFS_NAME = "calculator_prefs"
        private const val KEY_HISTORY = "history_json"
    }
}