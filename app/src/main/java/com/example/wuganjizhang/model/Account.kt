package com.example.wuganjizhang.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 账户名称
    val name: String = "",
    
    // 账户类型: wechat(微信), alipay(支付宝), bank(银行卡), cash(现金), other(其他)
    val type: String = "cash",
    
    // 账户图标
    val icon: String = "",
    
    // 账户颜色
    val color: String = "",
    
    // 余额
    val balance: Double = 0.0,
    
    // 初始余额
    @ColumnInfo(name = "initial_balance")
    val initialBalance: Double = 0.0,
    
    // 备注
    val remark: String? = null,
    
    // 是否启用
    val enabled: Boolean = true,
    
    // 排序顺序
    val order: Int = 0,
    
    // 创建时间
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    // 更新时间
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
