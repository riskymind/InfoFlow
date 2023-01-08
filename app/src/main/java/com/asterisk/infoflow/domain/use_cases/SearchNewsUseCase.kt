package com.asterisk.infoflow.domain.use_cases

import androidx.paging.PagingData
import androidx.paging.map
import com.asterisk.infoflow.data.local.entity.toNewsArticle
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(query: String): Flow<PagingData<NewsArticle>> {
        val articles = newsRepository.getSearchResultPaged(query)
        return articles.map {
            it.map { articles ->
                articles.toNewsArticle()
            }
        }
    }
}