package com.anahit.mediaplayer.data.di

import com.anahit.mediaplayer.core.common.DefaultDispatcherProvider
import com.anahit.mediaplayer.core.common.DispatcherProvider
import com.anahit.mediaplayer.data.repository.FavoritesRepositoryImpl
import com.anahit.mediaplayer.data.repository.MediaStoreRepositoryImpl
import com.anahit.mediaplayer.domain.repository.FavoritesRepository
import com.anahit.mediaplayer.domain.repository.MediaRepository
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
