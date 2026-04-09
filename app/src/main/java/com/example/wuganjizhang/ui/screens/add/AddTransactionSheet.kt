package com.example.wuganjizhang.ui.screens.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.example.wuganjizhang.model.Account
import com.example.wuganjizhang.model.Category
import com.example.wuganjizhang.ui.viewmodel.AddTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    if (!isVisible) return
    
    val uiState by viewModel.uiState.collectAsState()
    var showAccountPicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // 跳过部分展开，直接全屏
    )
    val coroutineScope = rememberCoroutineScope()
    
    // 只在首次显示时展开，避免重复动画
    LaunchedEffect(Unit) {
        sheetState.expand()
    }

    // 保存成功后关闭
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetForm()
            sheetState.hide() // 先执行下滑动画
            onDismiss() // 再通知父组件关闭
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
                Button(
                    onClick = {
                        if (!uiState.isLoading) {
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
                        Text("保存")
                    }
                }
            }

            // 类型切换
            TypeSelector(
                selectedType = uiState.selectedType,
                onTypeChanged = viewModel::updateType
            )

            // 内容区域 - 添加切换动画
            AnimatedContent(
                targetState = uiState.selectedType,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "ContentType"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

            // 金额输入
            AmountInput(
                amount = uiState.amount,
                onAmountChanged = viewModel::updateAmount,
                error = if (uiState.error == "请输入有效金额") uiState.error else null
            )

            // 分类选择（转账时不显示）
            if (uiState.selectedType != "transfer") {
                CategoryGrid(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::selectCategory,
                    showError = uiState.error == "请选择分类"
                )
            }

            // 账户选择
            AccountSelector(
                accounts = uiState.accounts,
                selectedAccount = uiState.selectedAccount,
                onSelectAccount = { showAccountPicker = true }
            )

            // 商户名称
            OutlinedTextField(
                value = uiState.merchant,
                onValueChange = viewModel::updateMerchant,
                label = { Text("商户名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 备注
            OutlinedTextField(
                value = uiState.remark,
                onValueChange = viewModel::updateRemark,
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // 其他错误提示（非分类错误）
            uiState.error?.let { error ->
                if (error != "请选择分类" && error != "请输入有效金额") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 转账时添加底部空白，防止页面上移
            if (uiState.selectedType == "transfer") {
                Spacer(modifier = Modifier.height(200.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 占据剩余空间，确保填满屏幕
            Spacer(modifier = Modifier.weight(1f))
                } // AnimatedContent 的 Column 结束
            } // AnimatedContent 结束
        } // 外层 Column 结束
    } // ModalBottomSheet 结束

    // 账户选择对话框
    if (showAccountPicker) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            selectedAccount = uiState.selectedAccount,
            onAccountSelected = { account ->
                viewModel.selectAccount(account)
                showAccountPicker = false
            },
            onDismiss = { showAccountPicker = false }
        )
    }
}

@Composable
fun TypeSelector(
    selectedType: String,
    onTypeChanged: (String) -> Unit
) {
    val types = listOf(
        Triple("expense", "支出", Color(0xFFFF6B6B)),
        Triple("income", "收入", Color(0xFF4CAF50)),
        Triple("transfer", "转账", Color(0xFF2196F3))
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        types.forEach { (type, label, activeColor) ->
            val isSelected = selectedType == type
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTypeChanged(type) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) activeColor else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
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
    accounts: List<Account>,
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
