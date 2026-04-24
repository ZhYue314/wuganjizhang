package com.example.wuganjizhang.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wuganjizhang.model.Transaction
import com.example.wuganjizhang.ui.theme.ExpenseColor
import com.example.wuganjizhang.ui.theme.IncomeColor
import com.example.wuganjizhang.ui.theme.TransferColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailSheet(
    transaction: Transaction?,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,  // 编辑回调（不带参数，由父组件处理）
    formatAmount: (Double) -> String
) {
    if (!isVisible || transaction == null) return

    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = sheetState,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 顶部栏：标题 + 关闭按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "交易详情",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "关闭")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 金额显示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (transaction.type) {
                        "expense" -> ExpenseColor.copy(alpha = 0.1f)
                        "income" -> IncomeColor.copy(alpha = 0.1f)
                        "transfer" -> TransferColor.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (transaction.type) {
                            "expense" -> "支出"
                            "income" -> "收入"
                            "transfer" -> "转账"
                            else -> "未知"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = when (transaction.type) {
                            "expense" -> ExpenseColor
                            "income" -> IncomeColor
                            "transfer" -> TransferColor
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatAmount(transaction.amount),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (transaction.type) {
                            "expense" -> ExpenseColor
                            "income" -> IncomeColor
                            "transfer" -> TransferColor
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 详细信息列表
            DetailItem(label = "分类", value = transaction.categoryName ?: "未分类")
            DetailItem(label = "账户", value = transaction.accountName ?: "未选择")
            
            // 转账类型显示对方账户
            if (transaction.type == "transfer") {
                DetailItem(label = "转入账户", value = transaction.remark ?: "-")
            }
            
            DetailItem(
                label = "时间",
                value = formatTimestamp(transaction.timestamp)
            )
            
            if (!transaction.merchant.isNullOrBlank()) {
                DetailItem(label = "商户", value = transaction.merchant)
            }
            
            if (!transaction.remark.isNullOrBlank() && transaction.type != "transfer") {
                DetailItem(label = "备注", value = transaction.remark)
            }
            
            // 标签显示
            if (!transaction.tags.isNullOrBlank()) {
                DetailItem(label = "标签", value = transaction.tags)
            }
            
            // 多币种信息
            if (transaction.originalCurrency != null && transaction.originalCurrency != transaction.currency) {
                DetailItem(
                    label = "原金额",
                    value = "${String.format("%.2f", transaction.originalAmount)} ${transaction.originalCurrency}"
                )
                DetailItem(
                    label = "汇率",
                    value = String.format("%.4f", transaction.exchangeRate)
                )
            }
            
            // 自动识别置信度
            if (transaction.source == "auto") {
                DetailItem(
                    label = "置信度",
                    value = "${(transaction.confidence * 100).toInt()}%"
                )
            }
            
            DetailItem(
                label = "来源",
                value = if (transaction.source == "auto") "自动识别" else "手动录入"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 编辑按钮
                OutlinedButton(
                    onClick = onEdit,  // 调用编辑回调，由父组件处理
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("编辑")
                }
                
                // 删除按钮
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("删除", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条交易记录吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                    onDismiss()
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
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    return dateFormat.format(Date(timestamp))
}
