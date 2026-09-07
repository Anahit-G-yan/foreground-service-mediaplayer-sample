package com.anahit.mediaplayer.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.anahit.mediaplayer.core.datastore.playbackDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providePlaybackDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.playbackDataStore
}
