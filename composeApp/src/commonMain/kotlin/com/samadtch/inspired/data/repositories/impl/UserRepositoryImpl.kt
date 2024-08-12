package com.samadtch.inspired.data.repositories.impl

import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_MISSING
import com.samadtch.inspired.data.datasources.local.preferences.AppPreferencesDataSource
import com.samadtch.inspired.data.datasources.local.preferences.TokenPreferencesDataSource
import com.samadtch.inspired.data.datasources.remote.AuthRemoteDataSource
import com.samadtch.inspired.data.repositories.UserRepository
import kotlinx.datetime.Clock

class UserRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenPrefsDataSource: TokenPreferencesDataSource,
    private val appPreferencesDataSource: AppPreferencesDataSource
) : UserRepository {

    override suspend fun authenticate(codeVerifier: String, code: String) {
        val token = authRemoteDataSource.generateToken(codeVerifier, code) //Generate Token
        tokenPrefsDataSource.saveToken(
            token.accessToken,
            token.refreshToken,
            Clock.System.now().epochSeconds.toInt() + token.expiresIn
        )  //Update App Tokens
        appPreferencesDataSource.setLoggedIn(true) //Update App
    }

    override suspend fun performActionWithFreshToken(execute: suspend (String) -> Unit) {
        if (tokenPrefsDataSource.getRefreshToken() == null) throw AuthException(AUTH_TOKEN_MISSING)

        //Refresh Token if needed
        if (Clock.System.now().epochSeconds < tokenPrefsDataSource.expiresAt()) {
            execute(tokenPrefsDataSource.getAccessToken()!!)
        } else {
            val token = authRemoteDataSource.refreshToken(tokenPrefsDataSource.getRefreshToken()!!)
            tokenPrefsDataSource.saveToken(
                token.accessToken,
                token.refreshToken,
                Clock.System.now().epochSeconds.toInt() + token.expiresIn
            )
            execute(token.accessToken)
        }
    }

    override suspend fun logout() {
        tokenPrefsDataSource.resetTokens() //Reset Tokens
        appPreferencesDataSource.setLoggedIn(false) //Update App
    }

}