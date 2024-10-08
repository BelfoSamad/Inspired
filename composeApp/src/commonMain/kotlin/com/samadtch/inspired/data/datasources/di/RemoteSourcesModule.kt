package com.samadtch.inspired.data.datasources.di

import com.samadtch.inspired.data.datasources.remote.AssetsRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.AuthRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.FoldersRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.getConfigRemoteSource
import com.samadtch.inspired.data.datasources.remote.impl.AssetsRemoteDataSourceImpl
import com.samadtch.inspired.data.datasources.remote.impl.AuthRemoteDataSourceImpl
import com.samadtch.inspired.data.datasources.remote.impl.FoldersRemoteDataSourceImpl
import org.koin.dsl.module

val remoteSourcesModule = module {
    includes(getConfigRemoteSource())
    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(get())
        //FakeAuthRemoteDataSource()
    }
    single<FoldersRemoteDataSource> {
        FoldersRemoteDataSourceImpl(get())
        //FakeFoldersRemoteDataSource()
    }
    single<AssetsRemoteDataSource> {
        AssetsRemoteDataSourceImpl(get())
        //FakeAssetsRemoteDataSource()
    }
}