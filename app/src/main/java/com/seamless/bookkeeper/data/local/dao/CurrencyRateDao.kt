package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seamless.bookkeeper.data.local.entity.CurrencyRateEntity

@Dao
interface CurrencyRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: CurrencyRateEntity): Long

    @Update
    suspend fun update(rate: CurrencyRateEntity): Int

    @Delete
    suspend fun delete(rate: CurrencyRateEntity): Int

    @Query("SELECT * FROM currency_rates WHERE currency_code = :code")
    suspend fun getByCode(code: String): CurrencyRateEntity?

    @Query("SELECT * FROM currency_rates ORDER BY currency_code ASC")
    suspend fun getAll(): List<CurrencyRateEntity>
}
