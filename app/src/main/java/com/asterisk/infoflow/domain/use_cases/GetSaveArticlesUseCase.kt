package com.asterisk.infoflow.domain.use_cases

import android.util.Log
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSaveArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(): Flow<List<NewsArticle>> {
        return newsRepository.getAllSavedArticle()
    }
}