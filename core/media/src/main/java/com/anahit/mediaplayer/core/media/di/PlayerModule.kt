package com.anahit.mediaplayer.core.media.di

import com.anahit.mediaplayer.core.media.PlayerController
import com.anahit.mediaplayer.core.media.PlayerControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
    @Binds
    abstract fun bindPlayerController(impl: PlayerControllerImpl): PlayerController
}
