package com.example.wuganjizhang.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 分类名称
    val name: String = "",
    
    // 分类图标（drawable资源名）
    val icon: String = "",
    
    // 分类颜色
    val color: String = "",
    
    // 类型: expense(支出), income(收入)
    val type: String = "expense",
    
    // 是否为预设分类
    @ColumnInfo(name = "is_preset")
    val isPreset: Boolean = false,
    
    // 排序顺序
    val order: Int = 0,
    
    // 是否启用
    val enabled: Boolean = true,
    
    // 创建时间
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
