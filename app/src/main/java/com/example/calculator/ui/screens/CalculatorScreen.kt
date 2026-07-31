package com.example.calculator.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.ui.theme.CalculatorTheme
import com.example.calculator.ui.theme.LocalCalculatorColors
import com.example.calculator.ui.viewmodels.CalculatorViewModel
import com.example.calculator.ui.viewmodels.HistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalCalculatorColors.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isHistoryVisible by viewModel.isHistoryVisible.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calculator Neo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        ),
                        color = colors.textPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = colors.textPrimary
                        )
                    }
                    // Tombol History sudah dipindahkan ke dalam display, jadi di sini kita hapus
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                    titleContentColor = colors.textPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp)
            ) {
                // ---- Display dengan tombol History di dalamnya ----
                NeoDisplay(
                    expression = uiState.expression,
                    result = uiState.resultText,
                    colors = colors,
                    onHistoryClick = { viewModel.toggleHistory() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ---- Keypad ----
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeoButton("AC", "AC", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton(
                            text = "",
                            tag = "⌫",
                            icon = Icons.AutoMirrored.Rounded.Backspace,
                            colors = colors,
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        NeoButton("%", "%", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("÷", "÷", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeoButton("7", "7", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("8", "8", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("9", "9", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("×", "×", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeoButton("4", "4", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("5", "5", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("6", "6", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("-", "-", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeoButton("1", "1", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("2", "2", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("3", "3", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("+", "+", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                    }
                    // Row 5: tombol "=" diperbesar
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeoButton("0", "0", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton(".", ".", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton("()", "()", colors, viewModel, isDarkTheme, Modifier.weight(1f))
                        NeoButton(
                            text = "=",
                            tag = "=",
                            colors = colors,
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier
                                .weight(1f)
                                .height(86.dp) // 20% lebih tinggi (72 * 1.2 = 86.4)
                        )
                    }
                }
            }

            if (isHistoryVisible) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.toggleHistory() },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = colors.surface,
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                ) {
                    HistoryPanel(
                        history = history,
                        colors = colors,
                        onClearHistory = { viewModel.clearHistory() },
                        onItemClick = { expr ->
                            viewModel.appendExpression(expr)
                            viewModel.toggleHistory()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NeoDisplay(
    expression: String,
    result: String,
    colors: com.example.calculator.ui.theme.CalculatorColors,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(start = 6.dp, bottom = 6.dp) // ruang untuk shadow (kanan-bawah)
    ) {
        // Layer shadow (kanan-bawah, solid)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 6.dp, y = 6.dp)
                .background(
                    color = colors.shadow,
                    shape = RoundedCornerShape(14.dp)
                )
        )

        // Layer utama display
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 3.dp,
                    color = colors.border,
                    shape = RoundedCornerShape(14.dp)
                ),
            shape = RoundedCornerShape(14.dp),
            color = colors.display,
            shadowElevation = 0.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Tombol History di pojok kiri atas
                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = "History",
                        tint = colors.textSecondary
                    )
                }

                // Ekspresi dan hasil di kanan bawah
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 56.sp
                        ),
                        color = colors.textPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun NeoButton(
    text: String,
    tag: String,
    colors: com.example.calculator.ui.theme.CalculatorColors,
    viewModel: CalculatorViewModel,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    val bgColor = when {
        tag in listOf("÷", "×", "-", "+") -> colors.operatorButton
        tag == "=" -> colors.equalButton
        tag in listOf("AC", "⌫", "%") -> colors.functionButton
        else -> colors.numberButton
    }

    val textColor = when {
        tag in listOf("÷", "×", "-", "+", "=") -> {
            if (isDarkTheme && tag != "=") Color.Black else colors.textPrimary
        }
        else -> colors.textPrimary
    }

    Box(
        modifier = modifier
            .height(72.dp)
            .padding(start = 6.dp, bottom = 6.dp)
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 6.dp, y = 6.dp)
                .background(
                    color = colors.shadow,
                    shape = RoundedCornerShape(12.dp)
                )
                .scale(scale)
        )

        // Button layer
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 3.dp,
                    color = colors.border,
                    shape = RoundedCornerShape(12.dp)
                )
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current   // <-- PERUBAHAN DI SINI
                ) {
                    viewModel.onButtonClick(tag)
                },
            shape = RoundedCornerShape(12.dp),
            color = bgColor,
            shadowElevation = 0.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = textColor,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor
                    )
                }
            }
        }
    }
}

// --- HistoryPanel dan HistoryItemRow (tidak berubah, hanya corner radius disesuaikan) ---
@Composable
fun HistoryPanel(
    history: List<HistoryItem>,
    colors: com.example.calculator.ui.theme.CalculatorColors,
    onClearHistory: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = colors.textPrimary
            )
            TextButton(
                onClick = onClearHistory,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colors.error
                )
            ) {
                Text(
                    text = "Clear history",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No history yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(history) { item ->
                    HistoryItemRow(
                        item = item,
                        colors = colors,
                        onClick = { onItemClick(item.expression) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(
    item: HistoryItem,
    colors: com.example.calculator.ui.theme.CalculatorColors,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(
                width = 2.dp,
                color = colors.border,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = item.expression,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "= ${item.result}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = colors.textPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalculator() {
    CalculatorTheme {
        CalculatorScreen()
    }
}