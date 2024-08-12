package com.samadtch.inspired.common.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.samadtch.bilinguai.BuildKonfig
import com.samadtch.inspired.data.repositories.ConfigRepository
import com.samadtch.inspired.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.koin.java.KoinJavaComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return KoinJavaComponent.get(FirebaseRemoteConfig::class.java)
    }

    //Additional (Config/User Repositories)
    @Provides
    fun provideUserRepository(): UserRepository {
        return KoinJavaComponent.get(UserRepository::class.java)
    }

    @Provides
    fun provideConfigRepository(): ConfigRepository {
        return KoinJavaComponent.get(ConfigRepository::class.java)
    }
}