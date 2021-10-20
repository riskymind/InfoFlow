package com.asterisk.infoflow.di

import android.app.Application
import androidx.room.Room
import com.asterisk.infoflow.api.NewsApi
import com.asterisk.infoflow.data.NewsArticleDatabase
import com.asterisk.infoflow.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi =
        retrofit.create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NewsArticleDatabase =
        Room.databaseBuilder(app, NewsArticleDatabase::class.java, "news_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDbDao(db: NewsArticleDatabase) = db.newsArticleDao()




}