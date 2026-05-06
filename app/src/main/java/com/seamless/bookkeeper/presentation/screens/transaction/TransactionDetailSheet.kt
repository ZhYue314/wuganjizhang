package com.seamless.bookkeeper.presentation.screens.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.presentation.common.BookkeeperBottomSheet
import com.seamless.bookkeeper.presentation.common.DangerButton
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.ExpenseLight
import com.seamless.bookkeeper.presentation.theme.IncomeLight
import com.seamless.bookkeeper.util.CurrencyUtil
import com.seamless.bookkeeper.util.DateUtil

@Composable
fun TransactionDetailSheet(
    transaction: TransactionWithRelations?,
    visible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    BookkeeperBottomSheet(visible = visible, onDismiss = onDismiss) {
        if (transaction != null) {
            val tx = transaction.transaction
            val isExpense = tx.type == "EXPENSE"
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimens.lg, vertical = Dimens.md)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "${if (isExpense) "-" else "+"}${CurrencyUtil.formatCNY(tx.amount)}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) ExpenseLight else IncomeLight
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = tx.merchantName ?: transaction.categoryName ?: "交易",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(Dimens.lg))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.shapeLarge),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(Dimens.md)) {
                        DetailRow("类型", if (isExpense) "支出" else if (tx.type == "INCOME") "收入" else "转账")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DetailRow("分类", transaction.categoryName ?: "未分类")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DetailRow("账户", transaction.accountName)
                        if (tx.merchantName != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DetailRow("商户", tx.merchantName)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DetailRow("时间", DateUtil.formatDateTime(tx.timestamp))
                        if (!tx.note.isNullOrBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DetailRow("备注", tx.note)
                        }
                    }
                }

                Spacer(Modifier.height(Dimens.sm))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.shapeMedium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(Dimens.md)) {
                        DetailRow("来源", when (tx.source) {
                            "AUTO" -> "自动识别"
                            "MANUAL" -> "手动录入"
                            "PERIODIC" -> "周期生成"
                            "FUSED" -> "融合识别"
                            else -> tx.source
                        })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DetailRow("置信度", "${(tx.confidence * 100).toInt()}%")
                    }
                }

                Spacer(Modifier.height(Dimens.md))

                DangerButton(text = "删除此交易", onClick = onDelete)
                Spacer(Modifier.height(Dimens.lg))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
