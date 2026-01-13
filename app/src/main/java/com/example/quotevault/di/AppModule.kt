package com.example.quotevault.di

import android.content.Context
import com.example.quotevault.data.local.PreferencesManager
import com.example.quotevault.data.remote.SupabaseClientWrapper
import com.example.quotevault.data.repository.AuthRepository
import com.example.quotevault.data.repository.CollectionRepository
import com.example.quotevault.data.repository.FavoriteRepository
import com.example.quotevault.data.repository.QuoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClientWrapper {
        return SupabaseClientWrapper()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        supabase: SupabaseClientWrapper
    ): AuthRepository {
        return AuthRepository(supabase)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        supabase: SupabaseClientWrapper
    ): FavoriteRepository {
        return FavoriteRepository(supabase)
    }

    @Provides
    @Singleton
    fun provideQuoteRepository(
        supabase: SupabaseClientWrapper,
        favoriteRepository: FavoriteRepository
    ): QuoteRepository {
        return QuoteRepository(supabase, favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(
        supabase: SupabaseClientWrapper
    ): CollectionRepository {
        return CollectionRepository(supabase)
    }
}