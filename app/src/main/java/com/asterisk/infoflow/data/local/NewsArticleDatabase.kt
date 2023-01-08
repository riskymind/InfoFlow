package com.asterisk.infoflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asterisk.infoflow.data.local.entity.BreakingNewsArticle
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity
import com.asterisk.infoflow.data.local.entity.SearchQueryRemoteKey
import com.asterisk.infoflow.data.local.entity.SearchResult

@Database(
    entities = [NewsArticleEntity::class, BreakingNewsArticle::class, SearchResult::class, SearchQueryRemoteKey::class],
    version = 1
)
abstract class NewsArticleDatabase : RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
    abstract fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao
}