package com.asterisk.infoflow.domain.repository

import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.data.remote.dto.NewsResponse
import com.asterisk.infoflow.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun getBreakingNews(): Flow<Resource<List<NewsArticle>>>

    suspend fun searchNews(

    ): List<NewsArticle>
}