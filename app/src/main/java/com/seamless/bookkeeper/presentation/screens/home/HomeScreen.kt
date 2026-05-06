package com.seamless.bookkeeper.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.presentation.common.BookkeeperBottomSheet
import com.seamless.bookkeeper.presentation.common.EmptyState
import com.seamless.bookkeeper.presentation.screens.transaction.TransactionDetailSheet
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.ExpenseLight
import com.seamless.bookkeeper.presentation.theme.IncomeLight
import com.seamless.bookkeeper.util.DateUtil
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onAddTransactionClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTransaction by remember { mutableStateOf<TransactionWithRelations?>(null) }

    Scaffold(
        topBar = {
            if (state.isSelectionMode) {
                TopAppBar(
                    title = { Text("已选择 ${state.selectedIds.size} 项") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "取消")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.deleteSelected() },
                            enabled = state.selectedIds.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            } else {
                TopAppBar(
                    title = { Text("无感记账") },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "菜单")
                        }
                    },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加交易")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("加载中...", style = MaterialTheme.typography.bodyLarge)
            }
        } else if (state.transactions.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    message = "暂无交易记录",
                    actionText = "点击 + 添加一笔"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(horizontal = Dimens.md, vertical = Dimens.sm),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "overview") {
                    OverviewCard(state.monthlyIncome, state.monthlyExpense, viewModel)
                }

                state.groupedTransactions.forEach { (dateLabel, txs) ->
                    item(key = "header_$dateLabel") {
                        DateHeader(
                            label = dateLabel,
                            totalExpense = state.dailyTotals[dateLabel] ?: BigDecimal.ZERO,
                            isSelectionMode = state.isSelectionMode,
                            isAllSelected = txs.all { it.transaction.id in state.selectedIds },
                            onToggleAll = {
                                val ids = txs.map { it.transaction.id }
                                if (txs.all { it.transaction.id in state.selectedIds }) {
                                    ids.forEach { viewModel.toggleSelection(it) }
                                } else {
                                    ids.forEach { viewModel.toggleSelection(it) }
                                }
                            }
                        )
                    }
                    items(txs, key = { it.transaction.id }) { tx ->
                        TransactionCard(
                            transaction = tx,
                            isSelectionMode = state.isSelectionMode,
                            isSelected = tx.transaction.id in state.selectedIds,
                            onClick = {
                                if (state.isSelectionMode) {
                                    viewModel.toggleSelection(tx.transaction.id)
                                } else {
                                    selectedTransaction = tx
                                }
                            },
                            onLongClick = { viewModel.toggleSelectionMode(); viewModel.toggleSelection(tx.transaction.id) }
                        )
                    }
                }
            }
        }
    }

    TransactionDetailSheet(
        transaction = selectedTransaction,
        visible = selectedTransaction != null,
        onDismiss = { selectedTransaction = null },
        onDelete = {
            selectedTransaction?.let { viewModel.deleteTransaction(it) }
            selectedTransaction = null
        },
        onEdit = {
            selectedTransaction = null
            onAddTransactionClick()
        }
    )
}

@Composable
fun OverviewCard(
    income: BigDecimal,
    expense: BigDecimal,
    viewModel: HomeViewModel
) {
    val balance = income.subtract(expense)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(Dimens.shapeLarge)
    ) {
        Column(modifier = Modifier.padding(Dimens.lg)) {
            Text(
                text = "本月结余",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(Dimens.sm))
            Text(
                text = viewModel.formatAmount(balance),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(Dimens.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("支出", style = MaterialTheme.typography.bodySmall)
                    Text(
                        viewModel.formatAmount(expense),
                        style = MaterialTheme.typography.titleMedium,
                        color = ExpenseLight,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("收入", style = MaterialTheme.typography.bodySmall)
                    Text(
                        viewModel.formatAmount(income),
                        style = MaterialTheme.typography.titleMedium,
                        color = IncomeLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DateHeader(
    label: String,
    totalExpense: BigDecimal,
    isSelectionMode: Boolean,
    isAllSelected: Boolean,
    onToggleAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (isSelectionMode) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isAllSelected, onCheckedChange = { onToggleAll() })
            }
        } else {
            Text(
                text = "支出 ${com.seamless.bookkeeper.util.CurrencyUtil.formatCNY(totalExpense)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TransactionCard(
    transaction: TransactionWithRelations,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val tx = transaction.transaction
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(Dimens.shapeSmall)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.md, vertical = Dimens.sm).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(Dimens.sm))
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        (transaction.categoryColor?.let { androidx.compose.ui.graphics.Color(it) }
                            ?: MaterialTheme.colorScheme.surfaceVariant)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.categoryName?.take(1) ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
            Spacer(Modifier.width(Dimens.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = tx.merchantName ?: transaction.categoryName ?: "未分类",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        append(DateUtil.formatDateTime(tx.timestamp))
                        append(" · ")
                        append(transaction.accountName)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val amountColor = when (tx.type) {
                "EXPENSE" -> ExpenseLight
                "INCOME" -> IncomeLight
                else -> MaterialTheme.colorScheme.onSurface
            }
            val sign = when (tx.type) {
                "EXPENSE" -> "-"
                "INCOME" -> "+"
                else -> ""
            }
            Text(
                text = "$sign${com.seamless.bookkeeper.util.CurrencyUtil.formatCNY(tx.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
