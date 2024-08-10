package com.samadtch.inspired.data.datasources.local.preferences

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun getDataStore(filename: String) = module {
    single(qualifier = named(filename)) {
        createDataStore {
            androidContext().filesDir?.resolve(filename)?.absolutePath
                ?: throw Exception("Couldn't get Android Datastore context.")
        }
    }
}