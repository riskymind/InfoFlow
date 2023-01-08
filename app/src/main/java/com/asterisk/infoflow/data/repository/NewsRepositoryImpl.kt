package com.asterisk.infoflow.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.commons.networkBoundResource
import com.asterisk.infoflow.data.local.NewsArticleDatabase
import com.asterisk.infoflow.data.local.SearchNewsRemoteMediator
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity
import com.asterisk.infoflow.data.local.entity.toNewsArticle
import com.asterisk.infoflow.data.remote.NewsApi
import com.asterisk.infoflow.data.remote.dto.toNewsArticleEntity
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val newsArticleDB: NewsArticleDatabase
) : NewsRepository {

    private val newsArticleDao = newsArticleDB.newsArticleDao()
    override suspend fun getBreakingNews(
        forceRefresh: Boolean,
        onFetchSucceed: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<NewsArticle>>> =
        networkBoundResource(
            query = {
                newsArticleDao.getAllBreakingNewsArticles().map { newsArticledEntity ->
                    newsArticledEntity.map { it.toNewsArticle() }
                }
            },
            fetch = {
                val response = newsApi.getBreakingNews()
                response.articles
            },
            saveFetchResult = { newsArticleDTO ->
                val savedArticle = newsArticleDao.getAllSavedArticles().first()

                val news = newsArticleDTO.map { newsDTO ->
                    val isSaved = savedArticle.any { it.url == newsDTO.url }
                    NewsArticleEntity(
                        title = newsDTO.title,
                        url = newsDTO.url,
                        thumbnailUrl = newsDTO.urlToImage,
                        isSaved = isSaved
                    )
                }


                val breakingNews = news.map {
                    it.toBreakingNewsArticle()
                }

                newsArticleDB.withTransaction {
                    newsArticleDao.deleteAllBreakingNews()
                    newsArticleDao.insertNewsArticle(news)
                    newsArticleDao.insertBreakingNewsArticle(breakingNews)
                }
            },
            shouldFetch = { cachedArticles ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedArticles = cachedArticles.sortedBy { it.updatedAt }
                    val oldestTimeStamp = sortedArticles.firstOrNull()?.updatedAt
                    val needsRefresh =
                        oldestTimeStamp == null || oldestTimeStamp < System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(
                            5
                        )
                    needsRefresh
                }
            },
            onFetchSucceed = onFetchSucceed,
            onFetchFailed = { onFetchFailed(it) }
        )


    override suspend fun searchNews(): List<NewsArticle> {
        val response = newsApi.getBreakingNews()
        val serverArticle = response.articles
        val newsArticles = serverArticle.map { it.toNewsArticleEntity() }
        return newsArticles.map { it.toNewsArticle() }
    }

    override suspend fun deleteOlderNonSavedArticle(timeStamp: Long) {
        newsArticleDao.deleteNonSavedArticle(timeStamp)
    }

    override suspend fun updateArticle(article: NewsArticleEntity) {
        newsArticleDao.updateArticle(article)
    }

    override fun getAllSavedArticle(): Flow<List<NewsArticle>> {
        val articles = newsArticleDao.getAllSavedArticles()
        return articles.map { it.map { it.toNewsArticle() } }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getSearchResultPaged(query: String): Flow<PagingData<NewsArticleEntity>> =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 200),
            remoteMediator = SearchNewsRemoteMediator(query, newsApi, newsArticleDB),
            pagingSourceFactory = { newsArticleDao.getSearchResultArticlesPaged(query) }
        ).flow


}