package com.seamless.bookkeeper

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.seamless.bookkeeper.data.DataInitializer
import com.seamless.bookkeeper.worker.AutoBackupWorker
import com.seamless.bookkeeper.worker.ServiceMonitorWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BookkeeperApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var dataInitializer: DataInitializer

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        CoroutineScope(Dispatchers.IO).launch {
            dataInitializer.initialize()
        }
        scheduleWorkers()
    }

    private fun scheduleWorkers() {
        val backupRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "auto_backup",
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )

        val monitorRequest = PeriodicWorkRequestBuilder<ServiceMonitorWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "service_monitor",
            ExistingPeriodicWorkPolicy.KEEP,
            monitorRequest
        )
    }
}
