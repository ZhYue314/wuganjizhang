package com.example.wuganjizhang.data.dao

import androidx.room.*
import com.example.wuganjizhang.data.model.PeriodicTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodicTemplateDao {
    
    @Query("SELECT * FROM periodic_templates WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledTemplates(): Flow<List<PeriodicTemplate>>
    
    @Query("SELECT * FROM periodic_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<PeriodicTemplate>>
    
    @Query("SELECT * FROM periodic_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): PeriodicTemplate?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: PeriodicTemplate): Long
    
    @Update
    suspend fun updateTemplate(template: PeriodicTemplate)
    
    @Delete
    suspend fun deleteTemplate(template: PeriodicTemplate)
    
    @Query("UPDATE periodic_templates SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleTemplateEnabled(id: Long, enabled: Boolean)
    
    @Query("UPDATE periodic_templates SET lastExecutedDate = :lastDate, nextExecutionDate = :nextDate WHERE id = :id")
    suspend fun updateExecutionDates(id: Long, lastDate: java.util.Date?, nextDate: java.util.Date?)
}
