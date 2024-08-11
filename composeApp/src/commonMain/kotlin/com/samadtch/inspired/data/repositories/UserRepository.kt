package com.samadtch.inspired.data.repositories

interface UserRepository {

    suspend fun authenticate(codeVerifier: String, code: String)

    suspend fun logout()

}