package com.seamless.bookkeeper.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("高级设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader("周期模板")
            SettingsItem("周期交易模板", "管理周期性自动生成的交易")
            HorizontalDivider()
            SettingsItem("币种汇率", "管理多币种和汇率设置")

            Spacer(Modifier.height(24.dp))
            SectionHeader("数据管理")
            SettingsItem("数据备份", "备份和恢复交易数据")
            SettingsItem("导入数据", "从 CSV/Excel 导入交易记录")
            SettingsItem("导出报表", "导出 PDF/Excel 报表")
            SettingsItem("数据归档", "归档历史数据")
            SettingsItem("数据清理", "按条件清理旧数据")

            Spacer(Modifier.height(24.dp))
            SectionHeader("服务设置")
            SettingsItem("无障碍设置", "监听 App 和触发条件配置")
            SettingsItem("通知权限", "通知监听服务状态")

            Spacer(Modifier.height(24.dp))
            SectionHeader("其他")
            SettingsItem("缓存管理", "清理应用缓存")
            SettingsItem("使用帮助", "查看使用说明")
            SettingsItem("隐私说明", "数据安全和隐私政策")
            SettingsItem("检查更新", "手动检查新版本")
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
