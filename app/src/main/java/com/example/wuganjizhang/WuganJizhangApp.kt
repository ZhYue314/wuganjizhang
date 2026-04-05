package com.example.wuganjizhang

import android.app.Application
import com.example.wuganjizhang.data.DatabaseSeeder
import com.example.wuganjizhang.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WuganJizhangApp : Application() {
    
    @Inject
    lateinit var database: AppDatabase
    
    override fun onCreate() {
        super.onCreate()
        // Seed database with initial data
        DatabaseSeeder.seed(database)
    }
}
