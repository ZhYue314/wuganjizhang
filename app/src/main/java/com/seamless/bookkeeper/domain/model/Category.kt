package com.seamless.bookkeeper.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Int,
    val type: TransactionType,
    val isPreset: Boolean = false,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val parentId: Long? = null
)
