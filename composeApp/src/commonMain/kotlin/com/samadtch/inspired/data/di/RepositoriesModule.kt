package com.samadtch.inspired.data.di

import com.samadtch.inspired.common.di.provideDispatcher
import com.samadtch.inspired.data.repositories.AssetsRepository
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.data.repositories.impl.AssetsRepositoryImpl
import com.samadtch.inspired.data.repositories.impl.FoldersRepositoryImpl
import org.koin.dsl.module

val repositoriesModule = module {
    single<FoldersRepository> {
        FoldersRepositoryImpl(get(), provideDispatcher())
        //FakeDataRepository()
    }
    single<AssetsRepository> {
        AssetsRepositoryImpl(get(), provideDispatcher())
        //FakeAssetsRepository()
    }
}