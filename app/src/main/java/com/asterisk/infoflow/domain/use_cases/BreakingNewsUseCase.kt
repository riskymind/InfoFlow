package com.asterisk.infoflow.domain.use_cases

import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BreakingNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke(): Flow<Resource<List<NewsArticle>>> {
        return newsRepository.getBreakingNews()
    }

}