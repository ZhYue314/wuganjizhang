package com.example.wuganjizhang.data.local

import androidx.room.TypeConverter
import com.example.wuganjizhang.data.model.*
import java.util.Date

class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toTransactionType(type: String): TransactionType {
        return TransactionType.valueOf(type)
    }
    
    @TypeConverter
    fun fromTransactionSource(source: TransactionSource): String {
        return source.name
    }
    
    @TypeConverter
    fun toTransactionSource(source: String): TransactionSource {
        return TransactionSource.valueOf(source)
    }
    
    @TypeConverter
    fun fromCategoryType(type: CategoryType): String {
        return type.name
    }
    
    @TypeConverter
    fun toCategoryType(type: String): CategoryType {
        return CategoryType.valueOf(type)
    }
    
    @TypeConverter
    fun fromAccountType(type: AccountType): String {
        return type.name
    }
    
    @TypeConverter
    fun toAccountType(type: String): AccountType {
        return AccountType.valueOf(type)
    }
    
    @TypeConverter
    fun fromPeriodType(type: PeriodType): String {
        return type.name
    }
    
    @TypeConverter
    fun toPeriodType(type: String): PeriodType {
        return PeriodType.valueOf(type)
    }
}
