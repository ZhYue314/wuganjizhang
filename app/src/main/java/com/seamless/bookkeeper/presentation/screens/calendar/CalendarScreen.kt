package com.seamless.bookkeeper.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.ExpenseLight
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onMenuClick: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val monthStart = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val dailyMap: Map<LocalDate, List<TransactionWithRelations>> = remember(state.transactions) {
        state.transactions.groupBy { tx ->
            Instant.ofEpochMilli(tx.transaction.timestamp)
                .atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日历视图") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "菜单")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            MonthlySummaryCard(currentMonth, dailyMap)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.md, vertical = Dimens.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ChevronLeft, "上月")
                }
                Text(
                    "${currentMonth.year}年${currentMonth.monthValue}月",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, "下月")
                }
            }

            CalendarGrid(
                yearMonth = currentMonth,
                selectedDate = selectedDate,
                dailyTransactions = dailyMap,
                onDateClick = { selectedDate = it }
            )

            Spacer(Modifier.height(Dimens.md))

            DayDetailSection(selectedDate, dailyMap[selectedDate] ?: emptyList())
        }
    }
}

@Composable
fun MonthlySummaryCard(
    month: YearMonth,
    dailyMap: Map<LocalDate, List<TransactionWithRelations>>
) {
    val monthData = dailyMap.filterKeys { YearMonth.from(it) == month }
    val totalExpense = monthData.values.flatten()
        .filter { it.transaction.type == "EXPENSE" }
        .sumOf { it.transaction.amount.toDouble() }
    val totalIncome = monthData.values.flatten()
        .filter { it.transaction.type == "INCOME" }
        .sumOf { it.transaction.amount.toDouble() }
    val count = monthData.values.sumOf { it.size }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.md, vertical = Dimens.sm),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(Dimens.shapeLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.md),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("支出", style = MaterialTheme.typography.bodySmall)
                Text("¥${String.format("%.0f", totalExpense)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("收入", style = MaterialTheme.typography.bodySmall)
                Text("¥${String.format("%.0f", totalIncome)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("笔数", style = MaterialTheme.typography.bodySmall)
                Text("$count", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    dailyTransactions: Map<LocalDate, List<TransactionWithRelations>>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDay.dayOfWeek.value % 7

    val maxExpense = dailyTransactions.filterKeys { YearMonth.from(it) == yearMonth }
        .maxOfOrNull { (_, txs) -> txs.filter { it.transaction.type == "EXPENSE" }.sumOf { it.transaction.amount.toDouble() } } ?: 1.0

    Column(modifier = Modifier.padding(horizontal = Dimens.md)) {
        val dayHeaders = listOf("日", "一", "二", "三", "四", "五", "六")
        Row(Modifier.fillMaxWidth()) {
            dayHeaders.forEach { day ->
                Text(
                    day, Modifier.weight(1f), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val day = row * 7 + col - firstDayOfWeek + 1
                    if (day in 1..daysInMonth) {
                        val date = yearMonth.atDay(day)
                        val isSelected = date == selectedDate
                        val isToday = date == LocalDate.now()
                        val dayExpense = dailyTransactions[date]
                            ?.filter { it.transaction.type == "EXPENSE" }
                            ?.sumOf { it.transaction.amount.toDouble() } ?: 0.0
                        val intensity = (dayExpense / maxExpense).coerceIn(0.0, 1.0)

                        Box(
                            modifier = Modifier
                                .weight(1f).aspectRatio(1f).padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.primaryContainer
                                        dayExpense > 0 -> MaterialTheme.colorScheme.primary.copy(
                                            alpha = (0.1f + 0.6f * intensity.toFloat()).coerceAtMost(0.7f)
                                        )
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "$day",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                if (dayExpense > 0) {
                                    Text(
                                        "¥${String.format("%.0f", dayExpense)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                        else ExpenseLight.copy(alpha = 0.7f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DayDetailSection(
    date: LocalDate,
    transactions: List<TransactionWithRelations>
) {
    Column(modifier = Modifier.padding(horizontal = Dimens.md)) {
        Text(
            "${date.monthValue}月${date.dayOfMonth}日 ${when(date.dayOfWeek) {
                DayOfWeek.MONDAY -> "周一"; DayOfWeek.TUESDAY -> "周二"; DayOfWeek.WEDNESDAY -> "周三"
                DayOfWeek.THURSDAY -> "周四"; DayOfWeek.FRIDAY -> "周五"; DayOfWeek.SATURDAY -> "周六"
                DayOfWeek.SUNDAY -> "周日"
            }}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        val total = transactions.filter { it.transaction.type == "EXPENSE" }
            .sumOf { it.transaction.amount.toDouble() }
        Text(
            "当日支出: ¥${String.format("%.2f", total)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Dimens.sm))

        if (transactions.isEmpty()) {
            Text(
                "当日无交易记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = Dimens.md)
            )
        } else {
            transactions.forEach { tx ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        tx.transaction.merchantName ?: tx.categoryName ?: "交易",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "¥${tx.transaction.amount.setScale(2, java.math.RoundingMode.HALF_UP)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (tx.transaction.type == "EXPENSE") ExpenseLight
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
