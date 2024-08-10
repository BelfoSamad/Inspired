package com.samadtch.inspired.data.di

import com.samadtch.inspired.feature.home.HomeViewModel
import org.koin.dsl.module

val viewmodelModule = module {
    single<HomeViewModel> { HomeViewModel(get()) }
}