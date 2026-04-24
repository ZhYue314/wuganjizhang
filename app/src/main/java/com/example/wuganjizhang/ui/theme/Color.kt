package com.example.wuganjizhang.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// 浅色主题配色
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4F6EF7),
    secondary = Color(0xFF6B85F9),
    tertiary = Color(0xFF22C55E),
    background = Color(0xFFF5F5F7),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E),
    error = Color(0xFFEF4444)
)

// 深色主题配色
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6B85F9),
    secondary = Color(0xFF4F6EF7),
    tertiary = Color(0xFF22C55E),
    background = Color(0xFF0F0F14),
    surface = Color(0xFF1A1A24),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    error = Color(0xFFEF5350)
)

// 自定义颜色
val ExpenseColor = Color(0xFFEF4444)
val IncomeColor = Color(0xFF22C55E)
val TransferColor = Color(0xFF2196F3)
val WarningColor = Color(0xFFF59E0B)
