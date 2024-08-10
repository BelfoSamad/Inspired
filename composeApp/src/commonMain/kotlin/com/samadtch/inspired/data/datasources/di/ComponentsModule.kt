package com.samadtch.inspired.data.datasources.di

import com.samadtch.bilinguai.BuildKonfig
import com.samadtch.inspired.data.datasources.local.preferences.appDSFileName
import com.samadtch.inspired.data.datasources.local.preferences.getDataStore
import com.samadtch.inspired.data.datasources.local.preferences.userDSFileName
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val componentsModule = module {
    // HTTP Client
    single {
        HttpClient(CIO) {
            expectSuccess = true//Throw error if failed

            //Content Negotiation
            val json = Json { ignoreUnknownKeys = true }
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Application.Json)
            }
            install(DefaultRequest) {
                url(BuildKonfig.BASE_URL)
            }
            install(Logging) {
                level = LogLevel.ALL // This will log everything including request bodies
            }
        }
    }

    //Preferences
    includes(
        getDataStore(userDSFileName),
        getDataStore(appDSFileName)
    )
}