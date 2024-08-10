package com.samadtch.inspired.common.exceptions

import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_AUTH
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NETWORK
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NOT_FOUND
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_REQUEST_OTHER
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_SERIALIZATION
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_SERVER_OTHER
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException

class DataException(val code: Int, message: String? = null) : Exception(message) {
    companion object {
        const val API_ERROR_AUTH = 401
        const val API_ERROR_NOT_FOUND = 404
        const val API_ERROR_RATE_LIMIT = 429 //Sending requests too quickly/exceeding quota
        const val API_ERROR_SERIALIZATION = 430

        const val API_ERROR_NETWORK = 333
        const val API_ERROR_REQUEST_OTHER = 444
        const val API_ERROR_SERVER_OTHER = 555
        const val API_ERROR_OTHER = 666

        //Others
        const val IMAGE_ERROR_DECODING = 1
    }
}

suspend fun <T> handleDataError(scope: String, block: suspend () -> T): T {
    try {
        return block()
    } catch (e: IOException) {
        println("IO: ${e.message}")
        throw DataException(API_ERROR_NETWORK)
    } catch (e: SerializationException) {
        println("Serialization: ${e.message}")
        throw DataException(API_ERROR_SERIALIZATION)
    } catch (e: ClientRequestException) {
        println("Client: ${e.message}")
        when (e.response.status) {
            HttpStatusCode.Unauthorized -> throw DataException(API_ERROR_AUTH)
            HttpStatusCode.NotFound -> throw DataException(API_ERROR_NOT_FOUND)
            HttpStatusCode.TooManyRequests -> throw DataException(API_ERROR_RATE_LIMIT)
            else -> {
                sendCrashlytics(Exception("$scope: ${e.message}"))
                throw DataException(API_ERROR_REQUEST_OTHER, e.message)
            }
        }
    } catch (e: ServerResponseException) {
        println("Server: ${e.message}")
        sendCrashlytics(Exception("$scope: ${e.message}"))
        throw DataException(API_ERROR_SERVER_OTHER, e.message)
    } catch (e: Exception) {
        println("Other: ${e.message}")
        sendCrashlytics(Exception("$scope: ${e.message}"))
        throw DataException(API_ERROR_OTHER, e.message)
    }
}