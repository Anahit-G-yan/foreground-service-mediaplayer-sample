package com.mediaplayer.app.feature.player.di

import com.mediaplayer.app.feature.player.ArtworkProvider
import com.mediaplayer.app.feature.player.MediaMetadataArtworkProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ArtworkModule {
    @Binds
    abstract fun bindArtworkProvider(impl: MediaMetadataArtworkProvider): ArtworkProvider
}
