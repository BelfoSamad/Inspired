package com.samadtch.inspired.data.datasources.di

import com.samadtch.inspired.data.datasources.local.preferences.AppPreferencesDataSource
import com.samadtch.inspired.data.datasources.local.preferences.appDSFileName
import com.samadtch.inspired.data.datasources.local.preferences.getTokenPreferencesDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val localSourcesModule = module {
    includes(getTokenPreferencesDataSource())
    single {
        AppPreferencesDataSource(get(qualifier = named(appDSFileName)))
    }
}