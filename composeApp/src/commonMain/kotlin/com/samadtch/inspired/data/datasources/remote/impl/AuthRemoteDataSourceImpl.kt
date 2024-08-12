package com.samadtch.inspired.data.datasources.remote.impl

import com.samadtch.bilinguai.BuildKonfig
import com.samadtch.inspired.common.exceptions.handleAuthError
import com.samadtch.inspired.data.datasources.remote.AuthRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.dto.TokenGenOutput
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class AuthRemoteDataSourceImpl(
    private val client: HttpClient
) : AuthRemoteDataSource {

    override suspend fun generateToken(codeVerifier: String, code: String): TokenGenOutput {
        return handleAuthError("generateToken") {
            client.post("oauth/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                basicAuth(BuildKonfig.CLIENT_ID, BuildKonfig.CLIENT_SECRET)
                val body = FormDataContent(
                    Parameters.build {
                        append("grant_type", "authorization_code")
                        append("code_verifier", codeVerifier)
                        append("code", code)
                        append("redirect_uri", BuildKonfig.REDIRECT_URL)
                    }
                )
                setBody(body)
            }.body<TokenGenOutput>()
        }
    }

    override suspend fun refreshToken(refreshToken: String): TokenGenOutput {
        return handleAuthError("refreshToken") {
            client.post("oauth/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                basicAuth(BuildKonfig.CLIENT_ID, BuildKonfig.CLIENT_SECRET)
                val body = FormDataContent(
                    Parameters.build {
                        append("grant_type", "refresh_token")
                        append("refresh_token", refreshToken)
                    }
                )
                setBody(body)
            }.body<TokenGenOutput>()
        }
    }

}