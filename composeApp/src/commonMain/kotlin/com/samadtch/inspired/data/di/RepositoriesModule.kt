package com.samadtch.inspired.data.di

import com.samadtch.inspired.common.di.provideDispatcher
import com.samadtch.inspired.data.repositories.AssetsRepository
import com.samadtch.inspired.data.repositories.ConfigRepository
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.data.repositories.UserRepository
import com.samadtch.inspired.data.repositories.impl.AssetsRepositoryImpl
import com.samadtch.inspired.data.repositories.impl.ConfigRepositoryImpl
import com.samadtch.inspired.data.repositories.impl.FoldersRepositoryImpl
import com.samadtch.inspired.data.repositories.impl.UserRepositoryImpl
import org.koin.dsl.module

val repositoriesModule = module {
    single<ConfigRepository> {
        ConfigRepositoryImpl(get(), get())
        //FakeConfigRepository()
    }
    single<UserRepository> {
        UserRepositoryImpl(get(), get(), get())
        //FakeUserRepository()
    }
    single<FoldersRepository> {
        FoldersRepositoryImpl(get(), provideDispatcher())
        //FakeFoldersRepository()
    }
    single<AssetsRepository> {
        AssetsRepositoryImpl(get(), provideDispatcher())
        //FakeAssetsRepository()
    }
}