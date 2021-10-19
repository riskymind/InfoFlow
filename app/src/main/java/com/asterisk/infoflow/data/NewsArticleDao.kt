package com.asterisk.infoflow.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticle(articles: List<NewsArticle>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNewsArticle(breakingNews: List<BreakingNewsArticle>)

    @Query("SELECT * FROM breaking_news_table INNER JOIN news_article_table ON articleUrl = url")
    fun getAllBreakingNewsArticles(): Flow<List<NewsArticle>>

    @Query("DELETE FROM breaking_news_table")
    suspend fun deleteAllBreakingNews()
}