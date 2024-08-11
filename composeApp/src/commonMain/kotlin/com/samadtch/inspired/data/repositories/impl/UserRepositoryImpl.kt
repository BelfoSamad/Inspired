package com.samadtch.inspired.data.repositories.impl

import com.samadtch.inspired.data.datasources.local.preferences.TokenPreferencesDataSource
import com.samadtch.inspired.data.datasources.remote.AuthRemoteDataSource
import com.samadtch.inspired.data.repositories.UserRepository

class UserRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenPrefsDataSource: TokenPreferencesDataSource
) : UserRepository {

    override suspend fun authenticate(codeVerifier: String, code: String){
        println("Auth-1")
        val token = authRemoteDataSource.generateToken(codeVerifier, code)
        tokenPrefsDataSource.saveToken(token.accessToken, token.refreshToken)
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

}