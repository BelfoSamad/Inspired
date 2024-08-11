package com.samadtch.inspired.data.datasources.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenGenInput(
    @SerialName("grant_type")
    val grantType: String = "authorization_code",
    @SerialName("code_verifier")
    val codeVerifier: String? = null,
    val code: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("redirect_uri")
    val redirectUri: String? = null
)

@Serializable
data class TokenGenOutput(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("token_type")
    val tokenType: String = "Bearer",
    @SerialName("expires_in")
    val expiresIn: Int,
    val scope: String,
)
