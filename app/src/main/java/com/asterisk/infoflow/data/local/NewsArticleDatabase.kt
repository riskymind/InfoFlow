package com.asterisk.infoflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asterisk.infoflow.data.local.entity.BreakingNewsArticle
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity

@Database(entities = [NewsArticleEntity::class, BreakingNewsArticle::class], version = 1)
abstract class NewsArticleDatabase : RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
}