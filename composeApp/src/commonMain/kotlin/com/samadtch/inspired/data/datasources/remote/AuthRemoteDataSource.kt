package com.samadtch.inspired.data.datasources.remote

import com.samadtch.inspired.data.datasources.remote.dto.TokenGenOutput

interface AuthRemoteDataSource {

    suspend fun generateToken(codeVerifier: String, code: String): TokenGenOutput

    suspend fun refreshToken(refreshToken: String): TokenGenOutput

}