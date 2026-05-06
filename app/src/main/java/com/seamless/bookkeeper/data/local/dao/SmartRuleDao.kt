package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seamless.bookkeeper.data.local.entity.SmartRuleEntity

@Dao
interface SmartRuleDao {
    @Insert
    suspend fun insert(rule: SmartRuleEntity): Long

    @Update
    suspend fun update(rule: SmartRuleEntity): Int

    @Delete
    suspend fun delete(rule: SmartRuleEntity): Int

    @Query("SELECT * FROM smart_rules WHERE merchant_name = :merchantName LIMIT 1")
    suspend fun findByMerchant(merchantName: String): SmartRuleEntity?

    @Query("""
        SELECT * FROM smart_rules
        WHERE keyword IS NOT NULL
          AND :merchantName LIKE '%' || keyword || '%'
        ORDER BY confidence DESC
        LIMIT 1
    """)
    suspend fun findByKeywordMatch(merchantName: String): SmartRuleEntity?

    @Query("UPDATE smart_rules SET confidence = :confidence WHERE id = :id")
    suspend fun updateConfidence(id: Long, confidence: Float): Int

    @Query("UPDATE smart_rules SET usage_count = usage_count + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(rule: SmartRuleEntity): Long
}
