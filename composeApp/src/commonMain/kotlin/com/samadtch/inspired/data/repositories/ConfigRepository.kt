package com.samadtch.inspired.data.repositories

import kotlinx.coroutines.flow.Flow

interface ConfigRepository {

    fun isFirstOpen(): Flow<Boolean>

    suspend fun setFirstTimeOpened()

    fun isLoggedIn(): Flow<Boolean>

    suspend fun setLoggedIn(loggedIn: Boolean)

    suspend fun getAppDetails(): Map<String, String>

}