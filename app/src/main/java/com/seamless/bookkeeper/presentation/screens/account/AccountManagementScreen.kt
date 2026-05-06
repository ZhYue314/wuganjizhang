package com.seamless.bookkeeper.presentation.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.util.CurrencyUtil
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    onBack: () -> Unit,
    viewModel: AccountManagementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val totalBalance = state.accounts.sumOf { it.currentBalance.toDouble() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("账户管理") },
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
            if (state.accounts.isEmpty()) {
                item {
                    Text("暂无账户", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp))
                }
            } else {
                items(state.accounts) { account ->
                    AccountCard(account = account)
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(Dimens.shapeMedium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(Dimens.md),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("总余额", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(CurrencyUtil.formatCNY(java.math.BigDecimal(totalBalance)),
                                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountCard(account: AccountEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.shapeMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(Dimens.shapeSmall))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(account.icon, fontSize = 24.sp)
            }
            Spacer(Modifier.width(Dimens.sm))
            Column(Modifier.weight(1f)) {
                Text(account.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("类型: ${account.type}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                CurrencyUtil.formatCNY(account.currentBalance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
