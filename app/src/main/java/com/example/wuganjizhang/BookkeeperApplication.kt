package com.example.wuganjizhang

import android.app.Application
import com.example.wuganjizhang.data.DataInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BookkeeperApplication : Application() {
    
    @Inject
    lateinit var dataInitializer: DataInitializer
    
    override fun onCreate() {
        super.onCreate()
        // 初始化数据
        CoroutineScope(Dispatchers.IO).launch {
            dataInitializer.initialize()
        }
    }
}
