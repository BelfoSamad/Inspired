package com.samadtch.inspired.data.datasources.local.preferences

import org.koin.core.module.Module

interface TokenPreferencesDataSource {

    fun saveToken(accessToken: String, refreshToken: String, expiresAt: Int)

    fun resetTokens()

    fun getAccessToken(): String?

    fun getRefreshToken(): String?

    fun expiresAt(): Int

}

expect fun getTokenPreferencesDataSource(): Module