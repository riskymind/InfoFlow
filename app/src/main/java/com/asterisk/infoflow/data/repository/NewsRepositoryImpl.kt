package com.asterisk.infoflow.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.commons.networkBoundResource
import com.asterisk.infoflow.data.local.NewsArticleDao
import com.asterisk.infoflow.data.local.NewsArticleDatabase
import com.asterisk.infoflow.data.remote.NewsApi
import com.asterisk.infoflow.data.remote.dto.NewsResponse
import com.asterisk.infoflow.data.remote.dto.toBreakingNewsArticle
import com.asterisk.infoflow.data.remote.dto.toNewsArticleEntity
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val newsArticleDB: NewsArticleDatabase
) : NewsRepository {

    private val newsArticleDao = newsArticleDB.newsArticleDao()

    override suspend fun getBreakingNews(): Flow<Resource<List<NewsArticle>>> =
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
                val news = newsArticleDTO.map { it.toNewsArticleEntity() }
                val breakingNews = newsArticleDTO.map { it.toBreakingNewsArticle() }

                newsArticleDB.withTransaction {
                    newsArticleDao.deleteAllBreakingNews()
                    newsArticleDao.insertNewsArticle(news)
                    newsArticleDao.insertBreakingNewsArticle(breakingNews)
                }
            }
        )

    override suspend fun searchNews(): List<NewsArticle> {
        val response = newsApi.getBreakingNews()
        val serverArticle = response.articles
        val newsArticles = serverArticle.map { it.toNewsArticleEntity() }
        val uiNewsArticle = newsArticles.map { it.toNewsArticle() }
        return uiNewsArticle
    }

}