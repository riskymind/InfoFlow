package com.asterisk.infoflow.domain.repository

import androidx.paging.PagingData
import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity
import com.asterisk.infoflow.data.remote.dto.NewsResponse
import com.asterisk.infoflow.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun getBreakingNews(
        forceRefresh: Boolean,
        onFetchSucceed: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<NewsArticle>>>

    suspend fun searchNews(): List<NewsArticle>

    suspend fun deleteOlderNonSavedArticle(timeStamp: Long)

    suspend fun updateArticle(article: NewsArticleEntity)

    fun getAllSavedArticle(): Flow<List<NewsArticle>>

    fun getSearchResultPaged(query: String): Flow<PagingData<NewsArticleEntity>>
}