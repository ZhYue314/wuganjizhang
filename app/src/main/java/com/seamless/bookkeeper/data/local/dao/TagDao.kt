package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seamless.bookkeeper.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: TagEntity): Long

    @Update
    suspend fun update(tag: TagEntity): Int

    @Delete
    suspend fun delete(tag: TagEntity): Int

    @Query("SELECT * FROM tags ORDER BY usage_count DESC")
    suspend fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tags ORDER BY usage_count DESC")
    fun getAllFlow(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getById(id: Long): TagEntity?

    @Query("UPDATE tags SET usage_count = usage_count + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: Long): Int
}
