package com.seamless.bookkeeper.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_records")
data class BackupRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "backup_time") val backupTime: Long,
    @ColumnInfo(name = "backup_path") val backupPath: String,
    @ColumnInfo(name = "backup_size") val backupSize: Long,
    @ColumnInfo(name = "backup_type") val backupType: String,
    val status: String
)
