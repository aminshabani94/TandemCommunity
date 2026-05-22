package com.asn.tandemcommunity.di

import com.asn.tandemcommunity.BuildConfig
import com.asn.tandemcommunity.data.local.LikeLocalDataSource
import com.asn.tandemcommunity.data.remote.CommunityApi
import com.asn.tandemcommunity.data.remote.KtorClientFactory
import com.asn.tandemcommunity.data.repository.CommunityRepositoryImpl
import com.asn.tandemcommunity.domain.repository.CommunityRepository
import com.asn.tandemcommunity.domain.usecase.LoadCommunityUseCase
import com.asn.tandemcommunity.domain.usecase.ObserveLikedIdsUseCase
import com.asn.tandemcommunity.domain.usecase.ToggleLikeUseCase
import com.asn.tandemcommunity.presentation.community.CommunityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { KtorClientFactory.create(enableLogging = BuildConfig.DEBUG) }
    single { CommunityApi(get()) }
    single { LikeLocalDataSource(androidContext().applicationContext) }
    single<CommunityRepository> { CommunityRepositoryImpl(get(), get()) }

    factory { LoadCommunityUseCase(get()) }
    factory { ToggleLikeUseCase(get()) }
    factory { ObserveLikedIdsUseCase(get()) }

    viewModel {
        CommunityViewModel(
            loadCommunityUseCase = get(),
            toggleLikeUseCase = get(),
            observeLikedIdsUseCase = get(),
        )
    }
}
