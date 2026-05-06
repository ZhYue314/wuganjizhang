package com.seamless.bookkeeper.presentation.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import com.seamless.bookkeeper.presentation.theme.Dimens
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryManagementScreen(
    onBack: () -> Unit,
    viewModel: CategoryManagementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分类管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(Dimens.md),
            verticalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            item {
                Text("预设分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Dimens.sm))
            }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
                    verticalArrangement = Arrangement.spacedBy(Dimens.sm)
                ) {
                    state.presetCategories.forEach { cat ->
                        CategoryGridItem(cat = cat)
                    }
                }
            }
            item {
                Text("自定义分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Dimens.sm))
            }
            if (state.customCategories.isEmpty()) {
                item {
                    Text("暂无自定义分类", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(state.customCategories) { cat ->
                    CategoryListItem(cat = cat)
                }
            }
        }
    }
}

@Composable
fun CategoryGridItem(cat: CategoryEntity) {
    Column(
        modifier = Modifier.size(width = 72.dp, height = 80.dp).clickable { },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape)
                .background(Color(cat.color).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(cat.icon, fontSize = 20.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(cat.name, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center,
            maxLines = 1, color = if (cat.isEnabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun CategoryListItem(cat: CategoryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.shapeMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(Color(cat.color).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center) {
                Text(cat.icon, fontSize = 18.sp)
            }
            Spacer(Modifier.width(Dimens.sm))
            Text(cat.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(if (cat.type == "EXPENSE") "支出" else "收入", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
