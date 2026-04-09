package com.example.wuganjizhang.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wuganjizhang.model.Transaction
import com.example.wuganjizhang.ui.screens.add.AddTransactionSheet
import com.example.wuganjizhang.ui.theme.ExpenseColor
import com.example.wuganjizhang.ui.theme.IncomeColor
import com.example.wuganjizhang.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTransaction by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTransaction = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加交易")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 月度汇总卡片
            item {
                MonthlySummaryCard(
                    income = uiState.monthlyIncome,
                    expense = uiState.monthlyExpense,
                    balance = uiState.monthlyBalance,
                    formatAmount = viewModel::formatAmount
                )
            }

            // 交易列表
            if (uiState.transactions.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                val groupedTransactions = viewModel.groupTransactionsByDate(uiState.transactions)
                groupedTransactions.forEach { (date, transactions) ->
                    item {
                        DateHeader(date = date)
                    }
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            formatAmount = viewModel::formatAmount,
                            formatTime = viewModel::formatTime,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                    }
                }
            }
        }
    }

    // 添加交易 BottomSheet
    AddTransactionSheet(
        isVisible = showAddTransaction,
        onDismiss = { showAddTransaction = false }
    )
}

@Composable
fun MonthlySummaryCard(
    income: Double,
    expense: Double,
    balance: Double,
    formatAmount: (Double) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "本月汇总",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // 改为均匀分布
            ) {
                SummaryItem(
                    label = "收入",
                    amount = formatAmount(income),
                    color = IncomeColor
                )
                SummaryItem(
                    label = "支出",
                    amount = formatAmount(expense),
                    color = ExpenseColor
                )
                SummaryItem(
                    label = "结余",
                    amount = formatAmount(balance),
                    color = if (balance >= 0) IncomeColor else ExpenseColor
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 100.dp) // 限制最大宽度
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // 超出显示省略号
        )
    }
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    formatAmount: (Double) -> String,
    formatTime: (Long) -> String,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: 跳转到详情页 */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchant ?: "未知商户",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.categoryName ?: "未分类"} · ${formatTime(transaction.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatAmount(transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == "expense") ExpenseColor else IncomeColor
                )
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Text("×", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条交易记录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无交易记录",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击右下角按钮添加第一笔交易",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
