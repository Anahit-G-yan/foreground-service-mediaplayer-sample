package com.mediaplayer.app.data.di

import com.mediaplayer.app.core.common.DefaultDispatcherProvider
import com.mediaplayer.app.core.common.DispatcherProvider
import com.mediaplayer.app.data.repository.FavoritesRepositoryImpl
import com.mediaplayer.app.data.repository.MediaStoreRepositoryImpl
import com.mediaplayer.app.domain.repository.FavoritesRepository
import com.mediaplayer.app.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMediaRepository(impl: MediaStoreRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
