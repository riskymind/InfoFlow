package com.asterisk.infoflow.domain.use_cases

import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import javax.inject.Inject

class SaveArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke(newsArticle: NewsArticle) {
        val currentSaves = newsArticle.isSaved
        val updatedArticle = newsArticle.copy(isSaved = !currentSaves)
        newsRepository.updateArticle(updatedArticle.toNewsArticleEntity())
    }

}