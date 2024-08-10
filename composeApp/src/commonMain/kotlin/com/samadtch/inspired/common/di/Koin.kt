package com.samadtch.inspired.common.di

import com.samadtch.inspired.data.datasources.di.componentsModule
import com.samadtch.inspired.data.datasources.di.remoteSourcesModule
import com.samadtch.inspired.data.di.repositoriesModule
import com.samadtch.inspired.data.di.viewmodelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        getNativeAppModule(),
        componentsModule,
        remoteSourcesModule,
        repositoriesModule,
        viewmodelModule
    )
}

//To call by IOS
fun initKoin() = initKoin {}