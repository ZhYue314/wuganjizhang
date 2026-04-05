package com.example.wuganjizhang.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Emoji or icon identifier
    val color: String, // Hex color code
    val type: CategoryType, // EXPENSE or INCOME
    val isPreset: Boolean = false,
    val sortOrder: Int = 0,
    val isEnabled: Boolean = true
)

enum class CategoryType {
    EXPENSE, INCOME
}
