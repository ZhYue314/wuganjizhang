package com.seamless.bookkeeper.presentation.screens.transaction

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.ExpenseLight
import com.seamless.bookkeeper.presentation.theme.IncomeLight
import com.seamless.bookkeeper.presentation.theme.TransferLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    onDismiss: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
        .format(java.util.Date(state.timestamp))

    val typeIndex = AddTransactionViewModel.typeList.indexOf(state.type).coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = AddTransactionViewModel.getPageForType(state.type),
        pageCount = { 3 }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var totalX = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (kotlin.math.abs(totalX) > 100f) {
                            val types = AddTransactionViewModel.typeList
                            val i = types.indexOf(state.type).coerceAtLeast(0)
                            val n = if (totalX < 0) (i + 1).coerceAtMost(types.lastIndex)
                            else (i - 1).coerceAtLeast(0)
                            if (n != i) viewModel.setType(types[n])
                        }
                        totalX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalX += dragAmount
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = Dimens.md, top = Dimens.sm, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val scope = rememberCoroutineScope()
                listOf("支出" to "EXPENSE", "收入" to "INCOME", "转账" to "TRANSFER").forEach { (label, type) ->
                    val isSelected = state.type == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val page = AddTransactionViewModel.getPageForType(type)
                                scope.launch { pagerState.animateScrollToPage(page) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) when (type) {
                                "EXPENSE" -> ExpenseLight
                                "INCOME" -> IncomeLight
                                else -> TransferLight
                            } else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        LaunchedEffect(pagerState.currentPage) {
            viewModel.setPage(pagerState.currentPage)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimens.md),
            beyondViewportPageCount = 1
        ) { page ->
            val pageType = AddTransactionViewModel.getTypeForPage(page)
            val cats = state.allCategories[pageType] ?: emptyList()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(Dimens.sm))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cats.take(12).forEach { category ->
                        val isSelected = state.selectedCategory?.id == category.id
                        Column(
                            modifier = Modifier
                                .width(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(category.color).copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .then(if (isSelected) Modifier.border(1.5.dp, Color(category.color), RoundedCornerShape(12.dp)) else Modifier)
                                .clickable { viewModel.setCategory(category) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(
                                    if (isSelected) Color(category.color) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                contentAlignment = Alignment.Center
                            ) { Text(category.icon, fontSize = 18.sp) }
                            Spacer(Modifier.height(2.dp))
                            Text(category.name, style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center, maxLines = 1,
                                color = if (isSelected) Color(category.color) else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = Dimens.md, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "¥ ${state.amount.ifBlank { "0.00" }}",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = when (state.type) {
                    "EXPENSE" -> ExpenseLight
                    "INCOME" -> IncomeLight
                    else -> TransferLight
                }
            )
        }

        HorizontalDivider()

        NumericKeypad(
            onDigit = { viewModel.appendDigit(it) },
            onDelete = { viewModel.deleteLastDigit() },
            onClear = { viewModel.clearAmount() },
            onConfirm = {
                if (state.amount.isNotBlank() && state.amount != "0") {
                    viewModel.save(onDismiss)
                }
            },
            onSaveAndAdd = {
                if (state.amount.isNotBlank() && state.amount != "0") {
                    viewModel.save { viewModel.clearAmount() }
                }
            }
        )

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.md, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AuxField("账户", state.selectedAccount?.name ?: "选择", Modifier.weight(1f))
            AuxField("商户", state.merchantName.ifBlank { "填写" }, Modifier.weight(1f))
            AuxField("备注", state.note.ifBlank { "填写" }, Modifier.weight(1f))
            AuxField("时间", dateStr, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AuxField(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}

@Composable
private fun NumericKeypad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    onSaveAndAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        KeypadRow(listOf("1", "2", "3", "⌫"), onDigit, onDelete)
        Spacer(Modifier.height(4.dp))
        KeypadRow(listOf("4", "5", "6", "+"), onDigit, onDelete)
        Spacer(Modifier.height(4.dp))
        KeypadRow(listOf("7", "8", "9", "-"), onDigit, onDelete)
        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Key(".", Modifier.weight(1f)) { onDigit(".") }
            Key("0", Modifier.weight(1f)) { onDigit("0") }
            ActionKey("再记", Modifier.weight(1f)) { onSaveAndAdd() }
            PrimaryKey("完成", Modifier.weight(1f)) { onConfirm() }
        }
    }
}

@Composable
private fun KeypadRow(keys: List<String>, onDigit: (String) -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        keys.forEach { key ->
            if (key == "⌫") {
                Key(key, Modifier.weight(1f), bg = MaterialTheme.colorScheme.surfaceVariant) { onDelete() }
            } else {
                Key(key, Modifier.weight(1f)) { onDigit(key) }
            }
        }
    }
}

@Composable
private fun Key(text: String, modifier: Modifier = Modifier, bg: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ActionKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 16.sp)
    }
}

@Composable
private fun PrimaryKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
