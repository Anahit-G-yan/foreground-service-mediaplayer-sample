package com.anahit.mediaplayer.feature.player.di

import com.anahit.mediaplayer.feature.player.ArtworkProvider
import com.anahit.mediaplayer.feature.player.MediaMetadataArtworkProvider
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
