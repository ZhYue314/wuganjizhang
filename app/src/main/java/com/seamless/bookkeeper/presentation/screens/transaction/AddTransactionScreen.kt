package com.seamless.bookkeeper.presentation.screens.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.ExpenseLight
import com.seamless.bookkeeper.presentation.theme.IncomeLight
import com.seamless.bookkeeper.util.CurrencyUtil
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    onDismiss: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("手动记账") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "取消")
                    }
                },
                actions = {
                    Text(
                        text = "保存",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.amount.isNotBlank() && state.amount != "0")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .clickable(enabled = state.amount.isNotBlank() && state.amount != "0") {
                                viewModel.save(onDismiss)
                            }
                            .padding(horizontal = Dimens.md, vertical = Dimens.sm)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.md)
            ) {
                Spacer(Modifier.height(Dimens.sm))

                // Type segment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("支出" to "EXPENSE", "收入" to "INCOME", "转账" to "TRANSFER").forEach { (label, type) ->
                        val isSelected = state.type == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) when (type) {
                                        "EXPENSE" -> ExpenseLight
                                        "INCOME" -> IncomeLight
                                        else -> MaterialTheme.colorScheme.primary
                                    } else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setType(type) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Dimens.md))

                // Category grid
                Text("选择分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(Dimens.sm))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.categories.take(12).forEach { category ->
                        val isSelected = state.selectedCategory?.id == category.id
                        Column(
                            modifier = Modifier
                                .width(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(category.color).copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        1.5.dp, Color(category.color), RoundedCornerShape(12.dp)
                                    ) else Modifier
                                )
                                .clickable { viewModel.setCategory(category) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color(category.color)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(category.icon, fontSize = 18.sp)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                category.name,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                color = if (isSelected) Color(category.color) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Dimens.md))

                // Amount display
                Text("金额", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(Dimens.sm))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¥ ${state.amount.ifBlank { "0.00" }}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (state.type) {
                            "EXPENSE" -> ExpenseLight
                            "INCOME" -> IncomeLight
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Spacer(Modifier.height(Dimens.md))

                // Auxiliary fields row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AuxField("账户", state.selectedAccount?.name ?: "选择", modifier = Modifier.weight(1f))
                    AuxField("商户", state.merchantName.ifBlank { "备注" }, modifier = Modifier.weight(1f))
                }
            }

            // Divider + Numeric keypad
            HorizontalDivider()
            NumericKeypad(
                onDigit = { viewModel.appendDigit(it) },
                onDelete = { viewModel.deleteLastDigit() },
                onClear = { viewModel.clearAmount() },
                onConfirm = {
                    if (state.amount.isNotBlank() && state.amount != "0") {
                        viewModel.save(onDismiss)
                    }
                }
            )
        }
    }
}

@Composable
private fun AuxField(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$label: $value",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NumericKeypad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0", "⌫")
        )
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (key) {
                                    "⌫" -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                            .clickable {
                                when (key) {
                                    "⌫" -> onDelete()
                                    else -> onDigit(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            key,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = if (key == "⌫") FontWeight.Normal else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
        }
        // Confirm row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onConfirm),
            contentAlignment = Alignment.Center
        ) {
            Text("确认", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
