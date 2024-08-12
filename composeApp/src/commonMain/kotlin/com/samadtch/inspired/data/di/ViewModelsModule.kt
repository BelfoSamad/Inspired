package com.samadtch.inspired.data.di

import com.samadtch.inspired.feature.boarding.BoardingViewModel
import com.samadtch.inspired.feature.home.HomeViewModel
import org.koin.dsl.module

val viewmodelModule = module {
    single<BoardingViewModel> { BoardingViewModel(get(), get()) }
    single<HomeViewModel> { HomeViewModel(get(), get(), get()) }
}