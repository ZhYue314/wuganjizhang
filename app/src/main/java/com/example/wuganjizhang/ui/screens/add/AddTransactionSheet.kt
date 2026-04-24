package com.example.wuganjizhang.ui.screens.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.example.wuganjizhang.model.Account
import com.example.wuganjizhang.model.Category
import com.example.wuganjizhang.ui.viewmodel.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddTransactionSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    if (!isVisible) return
    
    val uiState by viewModel.uiState.collectAsState()
    var showAccountPicker by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }  // 日期时间选择器
    var showMerchantDialog by remember { mutableStateOf(false) }  // 商户输入对话框
    var showRemarkDialog by remember { mutableStateOf(false) }  // 备注输入对话框
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // 跳过部分展开，直接全屏
    )
    val coroutineScope = rememberCoroutineScope()
    
    // 标记是否是由用户点击引起的滚动，避免循环触发
    var isUserClick by remember { mutableStateOf(false) }
    var isResetting by remember { mutableStateOf(false) } // 标记是否正在重置
    
    // 创建 Pager 状态，根据类型索引设置初始页
    val typeIndex = when (uiState.selectedType) {
        "expense" -> 0
        "income" -> 1
        "transfer" -> 2
        else -> 0
    }
    val pagerState = rememberPagerState(
        initialPage = typeIndex,
        pageCount = { 3 }
    )
    
    // 同步 Pager 滑动与 ViewModel 状态 - 只在非用户点击时更新
    LaunchedEffect(pagerState.currentPage) {
        // 如果不是用户点击引起的，才更新 ViewModel
        if (!isUserClick && pagerState.isScrollInProgress.not()) {
            val newType = when (pagerState.currentPage) {
                0 -> "expense"
                1 -> "income"
                2 -> "transfer"
                else -> "expense"
            }
            if (newType != uiState.selectedType) {
                viewModel.updateType(newType)
            }
        }
        // 延迟重置标志，确保动画完成
        if (isUserClick) {
            kotlinx.coroutines.delay(350)
            isUserClick = false
        }
    }
    
    // 同步 ViewModel 状态变化到 Pager
    LaunchedEffect(uiState.selectedType) {
        // 如果是重置或用户点击引起的，不跳转页面
        if (!isResetting && !isUserClick) {
            val targetPage = when (uiState.selectedType) {
                "expense" -> 0
                "income" -> 1
                "transfer" -> 2
                else -> 0
            }
            if (pagerState.currentPage != targetPage) {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }
    
    // 只在首次显示时展开，避免重复动画
    LaunchedEffect(Unit) {
        sheetState.expand()
    }

    // 保存成功后关闭
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            isResetting = true // 标记开始重置
            sheetState.hide() // 先执行下滑动画
            onDismiss() // 再通知父组件关闭
            viewModel.resetForm() // 最后重置表单
            kotlinx.coroutines.delay(100) // 等待一下
            isResetting = false // 重置完成
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 自定义顶栏：取消 | 保存
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.clearError() // 清除错误状态
                        viewModel.resetForm() // 重置表单（会清除编辑模式）
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("取消", color = Color.White)
                }
                
                // 根据编辑模式显示不同标题
                Text(
                    text = if (uiState.isEditMode) "编辑交易" else "添加交易",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        if (!uiState.isLoading) {
                            viewModel.clearError() // 点击保存时清除错误状态
                            viewModel.saveTransaction()
                        }
                    },
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (uiState.isEditMode) "更新" else "保存")
                    }
                }
            }

            // 类型切换 - 支持滑动
            TypeSelector(
                selectedType = uiState.selectedType,
                onTypeChanged = { newType ->
                    viewModel.updateType(newType)
                    
                    val targetPage = when (newType) {
                        "expense" -> 0
                        "income" -> 1
                        "transfer" -> 2
                        else -> 0
                    }
                    isUserClick = true
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = targetPage,
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 250,
                                easing = androidx.compose.animation.core.FastOutSlowInEasing
                            )
                        )
                    }
                },
                pagerState = pagerState
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // 分类选择区域 - 使用 HorizontalPager 只切换分类
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)  // 增加高度，为分类提供更多空间
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    userScrollEnabled = true,
                    pageSpacing = 0.dp
                ) { page ->
                    val pageCategories = when (page) {
                        0 -> uiState.expenseCategories
                        1 -> uiState.incomeCategories
                        else -> emptyList()
                    }
                    
                    if (pageCategories.isNotEmpty()) {
                        CategoryGrid(
                            categories = pageCategories,
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = viewModel::selectCategory,
                            showError = false
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "💸",
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "转账",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 金额输入框
            Spacer(modifier = Modifier.height(12.dp))
            AmountInput(
                amount = uiState.amount,
                onAmountChanged = viewModel::updateAmount,
                error = if (uiState.error == "请输入有效金额") uiState.error else null
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // 账户、商户、备注、时间 - 超紧凑点击式布局（放在键盘上方）
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 账户选择（占25%）- 只显示名称
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAccountPicker = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "账户",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.selectedAccount?.name ?: "选择",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                    
                    // 商户（占25%）- 点击弹出输入框
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showMerchantDialog = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "商户",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (uiState.merchant.isNotBlank()) uiState.merchant else "添加",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (uiState.merchant.isNotBlank()) FontWeight.Medium else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                    
                    // 备注（占25%）- 点击弹出输入框
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showRemarkDialog = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "备注",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (uiState.remark.isNotBlank()) uiState.remark else "添加",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (uiState.remark.isNotBlank()) FontWeight.Medium else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                    
                    // 时间（占25%）- 只显示日期
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDateTimePicker = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "时间",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = formatDateShort(uiState.timestamp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // 数字键盘（与辅助信息间隔1dp）
            Spacer(modifier = Modifier.height(1.dp))
            NumericKeyboard(
                onDigitClick = viewModel::appendDigit,
                onDeleteClick = viewModel::deleteLastDigit
            )
        } // 外层 Column 结束
    } // ModalBottomSheet 结束

    // 账户选择对话框
    if (showAccountPicker) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            selectedAccount = uiState.selectedAccount,
            onAccountSelected = { account ->
                viewModel.selectAccount(account)
                coroutineScope.launch {
                    showAccountPicker = false
                }
            },
            onDismiss = {
                coroutineScope.launch {
                    showAccountPicker = false
                }
            }
        )
    }
    
    // 日期时间选择对话框
    if (showDateTimePicker) {
        DateTimePickerDialog(
            initialTimestamp = uiState.timestamp,
            onDateTimeSelected = { newTimestamp ->
                viewModel.updateTimestamp(newTimestamp)
                showDateTimePicker = false
            },
            onDismiss = {
                showDateTimePicker = false
            }
        )
    }
    
    // 商户输入对话框
    if (showMerchantDialog) {
        AlertDialog(
            onDismissRequest = { showMerchantDialog = false },
            title = { Text("商户名称") },
            text = {
                OutlinedTextField(
                    value = uiState.merchant,
                    onValueChange = viewModel::updateMerchant,
                    label = { Text("输入商户名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = { showMerchantDialog = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMerchantDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 备注输入对话框
    if (showRemarkDialog) {
        AlertDialog(
            onDismissRequest = { showRemarkDialog = false },
            title = { Text("备注") },
            text = {
                OutlinedTextField(
                    value = uiState.remark,
                    onValueChange = viewModel::updateRemark,
                    label = { Text("输入备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            },
            confirmButton = {
                Button(onClick = { showRemarkDialog = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemarkDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TypeSelector(
    selectedType: String,
    onTypeChanged: (String) -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    val types = listOf(
        Triple("expense", "支出", Color(0xFFFF6B6B)),
        Triple("income", "收入", Color(0xFF4CAF50)),
        Triple("transfer", "转账", Color(0xFF2196F3))
    )
    
    // 计算滑动偏移
    val currentPage = pagerState.currentPage
    val currentPageOffset = pagerState.currentPageOffsetFraction
    
    // 计算当前和下一个页面的颜色
    val currentColor = when (currentPage) {
        0 -> types[0].third
        1 -> types[1].third
        2 -> types[2].third
        else -> types[0].third
    }
    
    val nextColor = when {
        currentPageOffset < 0 && currentPage > 0 -> {
            when (currentPage - 1) {
                0 -> types[0].third
                1 -> types[1].third
                2 -> types[2].third
                else -> types[0].third
            }
        }
        currentPageOffset > 0 && currentPage < 2 -> {
            when (currentPage + 1) {
                0 -> types[0].third
                1 -> types[1].third
                2 -> types[2].third
                else -> types[0].third
            }
        }
        else -> currentColor
    }
    
    // 计算渐变比例
    val fraction = Math.abs(currentPageOffset).coerceIn(0f, 1f)
    
    // 混合颜色 - 真正的颜色渐变
    val blendedColor = lerpColor(currentColor, nextColor, fraction)
    
    // 使用 Box 实现滑动背景效果
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
    ) {
        // 滑动的彩色背景块
        val indicatorWidthPercent = 1f / types.size
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            // 滑动背景指示器 - 使用 animateContentSize 实现平滑移动
            val offsetFraction = currentPage + currentPageOffset
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(indicatorWidthPercent)
                    .graphicsLayer {
                        translationX = offsetFraction * size.width
                    }
                    .background(
                        blendedColor,
                        RoundedCornerShape(8.dp)
                    )
            )
            
            // 按钮层
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                types.forEachIndexed { index, (type, label, _) ->
                    val isSelected = selectedType == type
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTypeChanged(type) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// 颜色线性插值函数
fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

@Composable
fun AmountInput(
    amount: String,
    onAmountChanged: (String) -> Unit,
    error: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (error != null) 
                MaterialTheme.colorScheme.errorContainer 
            else MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = amount,
                onValueChange = onAmountChanged,
                placeholder = { 
                    Text(
                        text = if (error != null && amount.isEmpty()) "请在此输入有效金额" else "0.00",
                        style = if (error != null && amount.isEmpty()) 
                            MaterialTheme.typography.bodyLarge
                        else MaterialTheme.typography.displaySmall,
                        color = if (error != null && amount.isEmpty()) 
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                },
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    showError: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "选择分类",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (showError) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "请选择分类",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    isSelected = selectedCategory?.id == category.id,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 根据 icon 字段显示对应的 emoji
            val emoji = when (category.icon) {
                "restaurant" -> "🍴"
                "directions_car" -> "🚗"
                "shopping_bag" -> "🛍️"
                "movie" -> "🎬"
                "home" -> "🏠"
                "local_hospital" -> "🏥"
                "school" -> "📚"
                "more_horiz" -> "⋯"
                "work" -> "💼"
                "card_giftcard" -> "🎁"
                "trending_up" -> "📈"
                "free_breakfast" -> "☕"
                else -> "📦"
            }
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AccountSelector(
    selectedAccount: Account?,
    onSelectAccount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelectAccount),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "账户",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedAccount?.name ?: "选择账户",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = ">",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AccountPickerDialog(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择账户") },
        text = {
            LazyColumn {
                items(accounts) { account ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onAccountSelected(account) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedAccount?.id == account.id)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = account.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "余额: ¥${account.balance}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selectedAccount?.id == account.id) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun DateTimePickerDialog(
    initialTimestamp: Long,
    onDateTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = initialTimestamp
    
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期时间") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 日期选择
                Text(
                    text = "日期",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 年份
                    OutlinedTextField(
                        value = selectedYear.toString(),
                        onValueChange = { 
                            if (it.length <= 4) {
                                selectedYear = it.toIntOrNull() ?: selectedYear
                            }
                        },
                        label = { Text("年") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    // 月份
                    OutlinedTextField(
                        value = (selectedMonth + 1).toString(),
                        onValueChange = { 
                            val month = it.toIntOrNull()
                            if (month != null && month in 1..12) {
                                selectedMonth = month - 1
                            }
                        },
                        label = { Text("月") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    // 日
                    OutlinedTextField(
                        value = selectedDay.toString(),
                        onValueChange = { 
                            val day = it.toIntOrNull()
                            if (day != null && day in 1..31) {
                                selectedDay = day
                            }
                        },
                        label = { Text("日") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 时间选择
                Text(
                    text = "时间",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 小时
                    OutlinedTextField(
                        value = selectedHour.toString().padStart(2, '0'),
                        onValueChange = { 
                            val hour = it.toIntOrNull()
                            if (hour != null && hour in 0..23) {
                                selectedHour = hour
                            }
                        },
                        label = { Text("时") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    // 分钟
                    OutlinedTextField(
                        value = selectedMinute.toString().padStart(2, '0'),
                        onValueChange = { 
                            val minute = it.toIntOrNull()
                            if (minute != null && minute in 0..59) {
                                selectedMinute = minute
                            }
                        },
                        label = { Text("分") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "预览: ${formatPreview(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newCalendar = Calendar.getInstance()
                newCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)
                newCalendar.set(Calendar.MILLISECOND, 0)
                onDateTimeSelected(newCalendar.timeInMillis)
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun formatDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    return dateFormat.format(Date(timestamp))
}

private fun formatDateShort(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return dateFormat.format(Date(timestamp))
}

private fun formatPreview(year: Int, month: Int, day: Int, hour: Int, minute: Int): String {
    return String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hour, minute)
}

@Composable
fun NumericKeyboard(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "⌫")
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { key ->
                        Button(
                            onClick = {
                                when (key) {
                                    "⌫" -> onDeleteClick()
                                    else -> onDigitClick(key)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (key == "⌫") 
                                    MaterialTheme.colorScheme.errorContainer 
                                else 
                                    MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (key == "⌫") 
                                    MaterialTheme.colorScheme.onErrorContainer 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
