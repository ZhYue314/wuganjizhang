package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.seamless.bookkeeper.data.local.entity.TemplateSkipDateEntity

@Dao
interface TemplateSkipDateDao {
    @Insert
    suspend fun insert(skipDate: TemplateSkipDateEntity): Long

    @Query("SELECT * FROM template_skip_dates WHERE template_id = :templateId")
    suspend fun getByTemplateId(templateId: Long): List<TemplateSkipDateEntity>

    @Query("DELETE FROM template_skip_dates WHERE template_id = :templateId")
    suspend fun deleteByTemplateId(templateId: Long): Int

    @Query("DELETE FROM template_skip_dates WHERE skip_date = :date AND template_id = :templateId")
    suspend fun deleteByDate(date: Long, templateId: Long): Int
}
