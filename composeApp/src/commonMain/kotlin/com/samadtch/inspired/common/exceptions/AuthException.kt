package com.samadtch.inspired.common.exceptions

import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_CONFLICT_ERROR
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_REQUEST_ERROR
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_REQUEST_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_SERVER_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NETWORK
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException

class AuthException(val code: Int, message: String? = null) : Exception(message) {
    companion object {
        const val AUTH_TOKEN_REQUEST_ERROR = 1 //error in authorization code or refresh code
        const val AUTH_TOKEN_CONFLICT_ERROR = 2 //auth code already used or refresh token invalid
        
        const val AUTH_TOKEN_REQUEST_ERROR_OTHER = 3
        const val AUTH_TOKEN_SERVER_ERROR_OTHER = 4
        const val AUTH_TOKEN_ERROR_OTHER = 5

        const val AUTH_TOKEN_MISSING = 6
    }
}

suspend fun <T> handleAuthError(scope: String, block: suspend () -> T): T {
    try {
        return block()
    } catch (e: IOException) {
        throw DataException(API_ERROR_NETWORK)
    }  catch (e: ClientRequestException) {
        when (e.response.status) {
            HttpStatusCode.BadRequest -> {
                throw AuthException(AUTH_TOKEN_REQUEST_ERROR)
            }
            HttpStatusCode.Conflict -> throw AuthException(AUTH_TOKEN_CONFLICT_ERROR)
            else -> {
                sendCrashlytics(Exception("$scope: ${e.message}"))
                throw AuthException(AUTH_TOKEN_REQUEST_ERROR_OTHER, e.message)
            }
        }
    } catch (e: ServerResponseException) {
        sendCrashlytics(Exception("$scope: ${e.message}"))
        throw AuthException(AUTH_TOKEN_SERVER_ERROR_OTHER, e.message)
    } catch (e: Exception) {
        sendCrashlytics(Exception("$scope: ${e.message}"))
        throw AuthException(AUTH_TOKEN_ERROR_OTHER, e.message)
    }
}