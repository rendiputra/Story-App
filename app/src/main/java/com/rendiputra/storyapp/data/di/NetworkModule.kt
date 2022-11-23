package com.rendiputra.storyapp.data.di

import com.rendiputra.storyapp.BuildConfig
import com.rendiputra.storyapp.data.network.service.StoryService
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideStoryService(okHttpClient: OkHttpClient): StoryService {
        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(StoryService::class.java)
    }

    @Provides
    @Singleton
    fun okHttpLoggingInterceptor(): OkHttpClient {
        val level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
        val logging = HttpLoggingInterceptor().setLevel(level)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}