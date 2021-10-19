package com.asterisk.infoflow.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsArticle::class, BreakingNewsArticle::class], version = 1)
abstract class NewsArticleDatabase : RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
}