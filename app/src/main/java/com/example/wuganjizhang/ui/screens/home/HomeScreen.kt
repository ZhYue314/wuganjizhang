package com.example.wuganjizhang.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.wuganjizhang.model.Transaction
import com.example.wuganjizhang.ui.screens.add.AddTransactionSheet
import com.example.wuganjizhang.ui.screens.detail.TransactionDetailSheet
import com.example.wuganjizhang.ui.theme.ExpenseColor
import com.example.wuganjizhang.ui.theme.IncomeColor
import com.example.wuganjizhang.ui.theme.TransferColor
import com.example.wuganjizhang.ui.viewmodel.HomeViewModel
import com.example.wuganjizhang.ui.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    addViewModel: AddTransactionViewModel = hiltViewModel()  // 添加记账 ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTransaction by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedTransactions = remember { mutableStateListOf<Int>() }
    var showContextMenu by remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                val allTransactionIds = uiState.transactions.map { it.id }
                val isAllSelected = allTransactionIds.isNotEmpty() && 
                    selectedTransactions.containsAll(allTransactionIds)
                
                TopAppBar(
                    title = { Text("已选择 ${selectedTransactions.size} 项") },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedTransactions.clear()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "取消")
                        }
                    },
                    actions = {
                        // 全选勾选框
                        Checkbox(
                            checked = isAllSelected,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    // 全选
                                    selectedTransactions.clear()
                                    selectedTransactions.addAll(allTransactionIds)
                                } else {
                                    // 取消全选
                                    selectedTransactions.clear()
                                }
                            }
                        )
                        
                        // 删除按钮
                        IconButton(
                            onClick = {
                                // 批量删除
                                selectedTransactions.forEach { id ->
                                    val transaction = uiState.transactions.find { it.id == id }
                                    transaction?.let { viewModel.deleteTransaction(it) }
                                }
                                isSelectionMode = false
                                selectedTransactions.clear()
                            },
                            enabled = selectedTransactions.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTransaction = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加交易")
            }
        }
    ) { paddingValues ->
        // 处理系统返回键
        BackHandler(enabled = isSelectionMode) {
            isSelectionMode = false
            selectedTransactions.clear()
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding(),
                    top = if (isSelectionMode) paddingValues.calculateTopPadding() else 0.dp
                ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                            DateHeader(
                                date = date,
                                isSelectionMode = isSelectionMode,
                                transactions = transactions,
                                selectedTransactions = selectedTransactions
                            )
                        }
                        items(transactions) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                formatAmount = viewModel::formatAmount,
                                formatTime = viewModel::formatTime,
                                onDelete = { viewModel.deleteTransaction(transaction) },
                                onClick = { selectedTransaction = transaction },
                                isSelectionMode = isSelectionMode,
                                isSelected = selectedTransactions.contains(transaction.id),
                                onToggleSelect = {
                                    if (selectedTransactions.contains(transaction.id)) {
                                        selectedTransactions.remove(transaction.id)
                                    } else {
                                        selectedTransactions.add(transaction.id)
                                    }
                                },
                                onLongPress = { position ->
                                    contextMenuPosition = position
                                    showContextMenu = true
                                }
                            )
                        }
                    }
                }
            } // LazyColumn 结束
        
        // 添加交易 BottomSheet
        AddTransactionSheet(
            isVisible = showAddTransaction,
            onDismiss = { showAddTransaction = false },
            viewModel = addViewModel  // 传入 ViewModel 实例
        )
        
        // 交易详情 BottomSheet
        TransactionDetailSheet(
            transaction = selectedTransaction,
            isVisible = selectedTransaction != null,
            onDismiss = { selectedTransaction = null },
            onDelete = {
                selectedTransaction?.let { viewModel.deleteTransaction(it) }
            },
            onEdit = {
                // 关闭详情页，打开记账页进行编辑
                val transactionToEdit = selectedTransaction
                selectedTransaction = null
                if (transactionToEdit != null) {
                    // 加载交易数据到编辑表单
                    addViewModel.loadTransactionForEdit(transactionToEdit)
                    showAddTransaction = true
                }
            },
            formatAmount = viewModel::formatAmount
        )
        
        // 长按上下文菜单
        if (showContextMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { showContextMenu = false })
                    },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "多选",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isSelectionMode = true
                                    showContextMenu = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    } // Scaffold 结束
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
                horizontalArrangement = Arrangement.SpaceEvenly
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
    // 分割整数和小数部分
    val parts = amount.split(".")
    val integerPart = parts.getOrNull(0) ?: amount
    val decimalPart = parts.getOrNull(1)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 100.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // 金额显示：整数部分 + 小数部分
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = integerPart,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
            if (decimalPart != null) {
                Text(
                    text = ".$decimalPart",
                    style = MaterialTheme.typography.bodySmall, // 字号约为 titleMedium 的一半
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DateHeader(
    date: String,
    isSelectionMode: Boolean = false,
    transactions: List<Transaction> = emptyList(),
    selectedTransactions: MutableList<Int> = mutableListOf()
) {
    val transactionIds = transactions.map { it.id }
    val isAllSelected = transactionIds.isNotEmpty() && 
        selectedTransactions.containsAll(transactionIds)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        // 多选模式下显示全选按钮
        if (isSelectionMode && transactionIds.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            // 全选当前日期的所有交易
                            selectedTransactions.removeAll(transactionIds)
                            selectedTransactions.addAll(transactionIds)
                        } else {
                            // 取消全选当前日期的所有交易
                            selectedTransactions.removeAll(transactionIds)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "全选",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    formatAmount: (Double) -> String,
    formatTime: (Long) -> String,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onLongPress: (androidx.compose.ui.geometry.Offset) -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    val maxWidth = 80.dp // 侧滑最大距离

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
                        // 选择模式下点击切换选中状态
                        onToggleSelect()
                    } else {
                        // 正常点击打开详情
                        onClick()
                    }
                },
                onLongClick = {
                    // 长按显示上下文菜单
                    onLongPress(androidx.compose.ui.geometry.Offset.Zero)
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // 第一行：分类类型 | 具体金额
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 选择模式下的勾选框
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSelect() },
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = transaction.categoryName ?: "未分类",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = when (transaction.type) {
                        "expense" -> "-${formatAmount(transaction.amount)}"
                        "income" -> "+${formatAmount(transaction.amount)}"
                        else -> formatAmount(transaction.amount)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.type) {
                        "expense" -> ExpenseColor
                        "income" -> IncomeColor
                        "transfer" -> TransferColor
                        else -> ExpenseColor
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 第二行：时间 | 备注 | 账户（右对齐）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：时间 + 备注
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(transaction.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (!transaction.remark.isNullOrBlank()) {
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = transaction.remark,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }
                
                // 右侧：账户
                Text(
                    text = transaction.accountName ?: "未知账户",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } // Column 结束
    } // Card 结束
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
