package com.samadtch.inspired.data.datasources.di

import com.samadtch.inspired.data.datasources.remote.FoldersRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.impl.FoldersRemoteDataSourceImpl
import org.koin.dsl.module

val remoteSourcesModule = module {
    single<FoldersRemoteDataSource> {
        FoldersRemoteDataSourceImpl(get())
        //FakeFoldersRemoteDataSource()
    }
}