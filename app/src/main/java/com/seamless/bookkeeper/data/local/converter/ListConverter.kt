package com.seamless.bookkeeper.data.local.converter

import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromStringList(value: String?): List<String>? = value?.split(",")?.map { it.trim() }

    @TypeConverter
    fun toStringList(list: List<String>?): String? = list?.joinToString(",")

    @TypeConverter
    fun fromLongList(value: String?): List<Long>? = value?.split(",")?.mapNotNull { it.trim().toLongOrNull() }

    @TypeConverter
    fun toLongList(list: List<Long>?): String? = list?.joinToString(",")
}
