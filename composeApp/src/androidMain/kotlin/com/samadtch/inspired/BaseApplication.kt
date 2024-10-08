package com.samadtch.inspired

import android.app.Application
import com.samadtch.inspired.common.di.initKoin
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //DI with Koin
        initKoin {
            androidContext(this@BaseApplication)
        }
    }
}