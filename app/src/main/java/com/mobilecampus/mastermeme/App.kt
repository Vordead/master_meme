package com.mobilecampus.mastermeme

import android.app.Application
import com.mobilecampus.mastermeme.meme.di.dataModule
import com.mobilecampus.mastermeme.meme.di.domainModule
import com.mobilecampus.mastermeme.meme.di.presentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class App : Application() {
   companion object {
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(listOf(dataModule, domainModule, presentationModule))
        }
    }
}