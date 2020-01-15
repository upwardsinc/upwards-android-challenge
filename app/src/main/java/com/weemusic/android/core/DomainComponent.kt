package com.weemusic.android.core

import com.weemusic.android.domain.GetTopAlbumsUseCase
import com.weemusic.android.network.iTunesApi
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@DomainScope
@Component(
    modules = [DomainComponent.DomainModule::class],
    dependencies = [NetworkComponent::class]
)
interface DomainComponent {

    fun getTopAlbumsUseCase(): GetTopAlbumsUseCase

    @Module
    class DomainModule {

        @DomainScope
        @Singleton
        fun getTopAlbumsUseCase(iTunesApi: iTunesApi) =
            GetTopAlbumsUseCase(iTunesApi)
    }
}