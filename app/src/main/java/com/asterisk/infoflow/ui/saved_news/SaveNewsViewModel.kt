package com.asterisk.infoflow.ui.saved_news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.use_cases.GetSaveArticlesUseCase
import com.asterisk.infoflow.domain.use_cases.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveNewsViewModel @Inject constructor(
    private val getSaveArticlesUseCase: GetSaveArticlesUseCase,
    private val saveArticleUseCase: SaveArticleUseCase
) : ViewModel() {

    val savedArticles = getSaveArticlesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun saveArticle(article: NewsArticle) {
        viewModelScope.launch {
            saveArticleUseCase(article)
        }
    }
}