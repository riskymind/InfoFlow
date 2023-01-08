package com.asterisk.infoflow.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.asterisk.infoflow.data.local.entity.BreakingNewsArticle
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity
import com.asterisk.infoflow.data.local.entity.SearchResult
import com.asterisk.infoflow.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticle(articles: List<NewsArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNewsArticle(breakingNews: List<BreakingNewsArticle>)

    @Query("SELECT * FROM breaking_news_table INNER JOIN news_article_table ON articleUrl = url")
    fun getAllBreakingNewsArticles(): Flow<List<NewsArticleEntity>>

    @Query("DELETE FROM breaking_news_table")
    suspend fun deleteAllBreakingNews()

    @Query("DELETE FROM news_article_table WHERE updateAt < :timeStampInMillis AND isSaved = 0")
    suspend fun deleteNonSavedArticle(timeStampInMillis: Long)

    @Update
    suspend fun updateArticle(article: NewsArticleEntity)

    @Query("SELECT * FROM news_article_table WHERE isSaved = 1")
    fun getAllSavedArticles(): Flow<List<NewsArticleEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResult(searchResults: List<SearchResult>)

    @Query("DELETE FROM search_result_table WHERE searchQuery = :query")
    suspend fun deleteSearchResults(query: String)

    @Query("SELECT MAX(queryPosition) FROM search_result_table WHERE searchQuery = :query")
    suspend fun getLastQueryPosition(query: String): Int?

    @Query("SELECT * FROM search_result_table INNER JOIN news_article_table ON articleUrl = url WHERE searchQuery = :query ORDER BY queryPosition")
    fun getSearchResultArticlesPaged(query: String): PagingSource<Int, NewsArticleEntity>
}