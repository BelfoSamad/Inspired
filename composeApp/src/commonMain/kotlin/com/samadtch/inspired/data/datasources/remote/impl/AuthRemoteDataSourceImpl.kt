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
            val req = client.post("oauth/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                basicAuth(BuildKonfig.CLIENT_ID, BuildKonfig.CLIENT_SECRET)

                println("Auth-2")
                println(codeVerifier)
                println(code)
                val body = FormDataContent(
                    Parameters.build {
                        append("grant_type", "authorization_code")
                        append("code_verifier", codeVerifier)
                        append("code", code)
                        append("redirect_uri", BuildKonfig.REDIRECT_URL)
                    }
                )
                println("BODY: $body")
                setBody(body)
            }
            req.body<TokenGenOutput>()
        }
    }

    override suspend fun refreshToken(refreshToken: String): TokenGenOutput {
        TODO("Not yet implemented")
    }

    override suspend fun getProfileName(token: String): String {
        TODO("Not yet implemented")
    }

}