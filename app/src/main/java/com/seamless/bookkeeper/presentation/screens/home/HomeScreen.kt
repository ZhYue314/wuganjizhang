package com.seamless.bookkeeper.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.presentation.common.EmptyState
import com.seamless.bookkeeper.presentation.screens.transaction.TransactionDetailSheet
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.ExpenseLight
import com.seamless.bookkeeper.presentation.theme.IncomeLight
import com.seamless.bookkeeper.util.CurrencyUtil
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
    var selectedTx by remember { mutableStateOf<TransactionWithRelations?>(null) }

    Scaffold(
        topBar = { HomeTopBar(state, viewModel, onMenuClick, onSearchClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, contentDescription = "添加交易") }
        }
    ) { paddingValues ->
        HomeContent(
            state = state,
            viewModel = viewModel,
            paddingValues = paddingValues,
            onTransactionClick = { selectedTx = it },
            onTransactionLongClick = { tx ->
                viewModel.toggleSelectionMode()
                viewModel.toggleSelection(tx.transaction.id)
            }
        )
    }

    TransactionDetailSheet(
        transaction = selectedTx,
        visible = selectedTx != null,
        onDismiss = { selectedTx = null },
        onDelete = { selectedTx?.let { viewModel.deleteTransaction(it) }; selectedTx = null },
        onEdit = { selectedTx = null; onAddTransactionClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    state: HomeUiState,
    viewModel: HomeViewModel,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val config = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background
    )
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
                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = config
        )
    } else {
        TopAppBar(
            title = { Text("无感记账") },
            navigationIcon = {
                IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "菜单") }
            },
            actions = {
                IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, contentDescription = "搜索") }
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = config
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    onTransactionClick: (TransactionWithRelations) -> Unit,
    onTransactionLongClick: (TransactionWithRelations) -> Unit
) {
    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.transactions.isEmpty() -> {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                EmptyState(message = "暂无交易记录", actionText = "点击 + 添加一笔")
            }
        }
        else -> TransactionList(state, viewModel, paddingValues, onTransactionClick, onTransactionLongClick)
    }
}

@Composable
private fun TransactionList(
    state: HomeUiState,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    onTransactionClick: (TransactionWithRelations) -> Unit,
    onTransactionLongClick: (TransactionWithRelations) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(horizontal = Dimens.md, vertical = Dimens.sm),
        verticalArrangement = Arrangement.spacedBy(Dimens.sm)
    ) {
        item(key = "overview") {
            OverviewCard(state.monthlyIncome, state.monthlyExpense)
        }

        state.groupedTransactions.forEach { (dateLabel, txs) ->
            val allSelected = txs.all { it.transaction.id in state.selectedIds }
            item(key = "hdr_$dateLabel") {
                DateHeader(
                    label = dateLabel,
                    totalExpense = state.dailyTotals[dateLabel] ?: BigDecimal.ZERO,
                    isSelectionMode = state.isSelectionMode,
                    isAllSelected = allSelected,
                    onToggleAll = { txs.forEach { viewModel.toggleSelection(it.transaction.id) } }
                )
            }
            items(txs, key = { it.transaction.id }) { tx ->
                TransactionCard(
                    transaction = tx,
                    isSelectionMode = state.isSelectionMode,
                    isSelected = tx.transaction.id in state.selectedIds,
                    onClick = {
                        if (state.isSelectionMode) viewModel.toggleSelection(tx.transaction.id)
                        else onTransactionClick(tx)
                    },
                    onLongClick = { onTransactionLongClick(tx) }
                )
            }
        }
    }
}

@Composable
fun OverviewCard(income: BigDecimal, expense: BigDecimal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(Dimens.shapeLarge)
    ) {
        Column(modifier = Modifier.padding(Dimens.md)) {
            Text(
                "本月结余",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                CurrencyUtil.formatCNY(income.subtract(expense)),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(Dimens.sm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("支出", style = MaterialTheme.typography.labelMedium)
                    Text(CurrencyUtil.formatCNY(expense), style = MaterialTheme.typography.bodyLarge,
                        color = ExpenseLight, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("收入", style = MaterialTheme.typography.labelMedium)
                    Text(CurrencyUtil.formatCNY(income), style = MaterialTheme.typography.bodyLarge,
                        color = IncomeLight, fontWeight = FontWeight.Bold)
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
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (isSelectionMode) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isAllSelected, onCheckedChange = { onToggleAll() })
            }
        } else {
            Text(
                "支出 ${CurrencyUtil.formatCNY(totalExpense)}",
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
    val catColor = transaction.categoryColor?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant
    val isExpense = tx.type == "EXPENSE"

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
                Checkbox(checked = isSelected, onCheckedChange = { onClick() }, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(Dimens.sm))
            }
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(catColor),
                contentAlignment = Alignment.Center
            ) {
                Text(transaction.categoryName?.take(1) ?: "?", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            Spacer(Modifier.width(Dimens.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    tx.merchantName ?: transaction.categoryName ?: "未分类",
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${DateUtil.formatDateTime(tx.timestamp)} · ${transaction.accountName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${if (isExpense) "-" else "+"}${CurrencyUtil.formatCNY(tx.amount)}",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = if (isExpense) ExpenseLight else IncomeLight
            )
        }
    }
}
