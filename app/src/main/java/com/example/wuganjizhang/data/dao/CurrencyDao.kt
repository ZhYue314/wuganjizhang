package com.example.wuganjizhang.data.dao

import androidx.room.*
import com.example.wuganjizhang.data.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    
    @Query("SELECT * FROM currencies ORDER BY currencyCode ASC")
    fun getAllCurrencies(): Flow<List<Currency>>
    
    @Query("SELECT * FROM currencies WHERE currencyCode = :code")
    suspend fun getCurrencyByCode(code: String): Currency?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: Currency)
    
    @Update
    suspend fun updateCurrency(currency: Currency)
    
    @Delete
    suspend fun deleteCurrency(currency: Currency)
    
    @Query("UPDATE currencies SET exchangeRate = :rate, updatedAt = :updateTime WHERE currencyCode = :code")
    suspend fun updateExchangeRate(code: String, rate: Double, updateTime: java.util.Date)
}
