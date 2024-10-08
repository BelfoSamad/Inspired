package com.samadtch.inspired.data.repositories.impl

import com.samadtch.inspired.data.datasources.local.preferences.AppPreferencesDataSource
import com.samadtch.inspired.data.datasources.remote.ConfigRemoteSource
import com.samadtch.inspired.data.repositories.ConfigRepository

class ConfigRepositoryImpl(
    private val appPreferencesDataSource: AppPreferencesDataSource,
    private val configDataSource: ConfigRemoteSource,
) : ConfigRepository {

    override fun isFirstOpen() = appPreferencesDataSource.isFirstOpen

    override suspend fun setFirstTimeOpened() {
        appPreferencesDataSource.setFirstOpen()
    }

    override fun isLoggedIn() = appPreferencesDataSource.isLoggedIn

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        appPreferencesDataSource.setLoggedIn(loggedIn)
    }

    override suspend fun getAppDetails() = mapOf(
        "privacy" to configDataSource.getStringConfig("PRIVACY_POLICY_LINK"),
        "tos" to configDataSource.getStringConfig("TOS_LINK"),
        "developer" to configDataSource.getStringConfig("DEVELOPER_ID")
    )

}