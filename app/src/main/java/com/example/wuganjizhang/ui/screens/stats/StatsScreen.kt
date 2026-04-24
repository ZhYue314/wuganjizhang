package com.example.wuganjizhang.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wuganjizhang.ui.theme.ExpenseColor
import com.example.wuganjizhang.ui.theme.IncomeColor
import com.example.wuganjizhang.ui.viewmodel.StatsViewModel
import com.example.wuganjizhang.ui.viewmodel.TimeDimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计分析") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 时间维度切换
            item {
                TimeDimensionSelector(
                    selected = uiState.timeDimension,
                    onSelected = { viewModel.setTimeDimension(it) }
                )
            }
            
            // 汇总卡片
            item {
                SummaryCard(
                    income = uiState.totalIncome,
                    expense = uiState.totalExpense,
                    balance = uiState.balance
                )
            }
            
            // 分类占比
            if (uiState.categoryStats.isNotEmpty()) {
                item {
                    CategoryBreakdown(categoryStats = uiState.categoryStats)
                }
            }
            
            // 分类排行榜
            if (uiState.categoryStats.isNotEmpty()) {
                items(uiState.categoryStats) { stat ->
                    CategoryRankItem(stat = stat)
                }
            }
        }
    }
}

@Composable
fun TimeDimensionSelector(
    selected: TimeDimension,
    onSelected: (TimeDimension) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimeDimension.entries.forEach { dimension ->
                FilterChip(
                    selected = selected == dimension,
                    onClick = { onSelected(dimension) },
                    label = { 
                        Text(
                            when (dimension) {
                                TimeDimension.DAY -> "日"
                                TimeDimension.WEEK -> "周"
                                TimeDimension.MONTH -> "月"
                                TimeDimension.YEAR -> "年"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    income: Double,
    expense: Double,
    balance: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "本月汇总",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "收入",
                    amount = String.format("%.2f", income),
                    color = IncomeColor
                )
                SummaryItem(
                    label = "支出",
                    amount = String.format("%.2f", expense),
                    color = ExpenseColor
                )
                SummaryItem(
                    label = "结余",
                    amount = String.format("%.2f", balance),
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
        horizontalAlignment = Alignment.CenterHorizontally
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
            color = color
        )
    }
}

@Composable
fun CategoryBreakdown(
    categoryStats: List<com.example.wuganjizhang.ui.viewmodel.CategoryStat>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "分类占比",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            categoryStats.take(5).forEach { stat ->
                CategoryBarItem(stat = stat)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CategoryBarItem(
    stat: com.example.wuganjizhang.ui.viewmodel.CategoryStat
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            Color(stat.color),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Text(
                    text = stat.categoryName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = "${String.format("%.1f", stat.percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = (stat.percentage / 100).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = Color(stat.color),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun CategoryRankItem(
    stat: com.example.wuganjizhang.ui.viewmodel.CategoryStat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color(stat.color),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stat.categoryName.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = stat.categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = String.format("%.1f%%", stat.percentage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = String.format("%.2f", stat.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = ExpenseColor
            )
        }
    }
}
