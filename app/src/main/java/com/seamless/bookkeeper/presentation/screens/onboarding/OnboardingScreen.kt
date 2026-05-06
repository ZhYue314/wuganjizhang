package com.seamless.bookkeeper.presentation.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seamless.bookkeeper.presentation.theme.Dimens
import com.seamless.bookkeeper.presentation.theme.PrimaryLight
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> OnboardingPage(
                        emoji = "🍃",
                        title = "欢迎使用无感记账",
                        desc = "自动识别微信、支付宝交易记录\n完全本地存储，无需联网"
                    )
                    1 -> OnboardingPage(
                        emoji = "🔔",
                        title = "开启通知监听",
                        desc = "授予通知访问权限后\nApp 可自动识别支付通知并记录"
                    )
                    2 -> OnboardingPage(
                        emoji = "📊",
                        title = "管理你的账单",
                        desc = "多维度统计图表\n按日/周/月/年查看收支趋势"
                    )
                    3 -> OnboardingPage(
                        emoji = "🔒",
                        title = "安全可靠",
                        desc = "所有数据仅存储在本地\n支持数据备份和恢复"
                    )
                }
            }
        }

        // Page indicator dots
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.md),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == i) 24.dp else 8.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == i) PrimaryLight
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
                if (i < 3) Spacer(Modifier.weight(1f))
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.lg, vertical = Dimens.md),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            OutlinedButton(
                onClick = onComplete,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(Dimens.shapeMedium)
            ) { Text("跳过") }

            Button(
                onClick = {
                    if (pagerState.currentPage < 3) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(Dimens.shapeMedium)
            ) {
                Text(if (pagerState.currentPage < 3) "下一步" else "开始使用")
            }
        }
    }
}

@Composable
private fun OnboardingPage(emoji: String, title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(120.dp).clip(CircleShape)
                .background(PrimaryLight.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 48.sp)
        }
        Spacer(Modifier.height(32.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Dimens.md))
        Text(
            desc,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}
