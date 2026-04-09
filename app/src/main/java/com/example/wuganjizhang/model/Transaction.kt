package com.example.wuganjizhang.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 交易类型: expense(支出), income(收入), transfer(转账)
    val type: String = "expense",
    
    // 金额
    val amount: Double = 0.0,
    
    // 分类ID
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,
    
    // 分类名称（冗余字段，方便查询）
    @ColumnInfo(name = "category_name")
    val categoryName: String? = null,
    
    // 账户ID
    @ColumnInfo(name = "account_id")
    val accountId: Int = 0,
    
    // 账户名称（冗余字段）
    @ColumnInfo(name = "account_name")
    val accountName: String? = null,
    
    // 商户/备注
    val merchant: String? = null,
    
    // 交易时间（时间戳）
    val timestamp: Long = System.currentTimeMillis(),
    
    // 标签（JSON格式或逗号分隔）
    val tags: String? = null,
    
    // 备注
    val remark: String? = null,
    
    // 来源: auto(自动识别), manual(手动录入)
    val source: String? = null,
    
    // 置信度（自动识别时）
    val confidence: Float = 1.0f,
    
    // 附件（图片路径、语音路径等，JSON格式）
    val attachments: String? = null,
    
    // 币种
    val currency: String = "CNY",
    
    // 原币种金额（多币种时使用）
    @ColumnInfo(name = "original_amount")
    val originalAmount: Double = 0.0,
    
    // 原币种
    @ColumnInfo(name = "original_currency")
    val originalCurrency: String? = null,
    
    // 汇率
    @ColumnInfo(name = "exchange_rate")
    val exchangeRate: Double = 1.0,
    
    // 创建时间
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    // 更新时间
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    // 是否删除（软删除）
    val deleted: Boolean = false
)
