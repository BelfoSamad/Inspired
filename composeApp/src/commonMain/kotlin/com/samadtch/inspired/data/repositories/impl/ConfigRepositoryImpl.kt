package com.samadtch.inspired.data.repositories.impl

import com.samadtch.inspired.data.datasources.local.preferences.AppPreferencesDataSource
import com.samadtch.inspired.data.repositories.ConfigRepository

class ConfigRepositoryImpl(
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ConfigRepository {

    override fun isFirstOpen() = appPreferencesDataSource.isFirstOpen

    override suspend fun setFirstTimeOpened() {
        appPreferencesDataSource.setFirstOpen()
    }

    override fun isLoggedIn() = appPreferencesDataSource.isLoggedIn

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        appPreferencesDataSource.setLoggedIn(loggedIn)
    }

}