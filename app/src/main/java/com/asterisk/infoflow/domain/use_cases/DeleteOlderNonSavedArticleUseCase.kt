package com.asterisk.infoflow.domain.use_cases

import com.asterisk.infoflow.domain.repository.NewsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DeleteOlderNonSavedArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke() {
        newsRepository.deleteOlderNonSavedArticle(
            System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        )
    }
}