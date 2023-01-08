package com.asterisk.infoflow.ui.search_news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.use_cases.SaveArticleUseCase
import com.asterisk.infoflow.domain.use_cases.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase,
    private val saveArticleUseCase: SaveArticleUseCase
) : ViewModel() {

    private val currentQuery = MutableStateFlow<String?>(null)

    var refreshInProgress = false
    var pendingScrollToTopAfterRefresh = false
    var pendingScrollToTopAfterNewQuery = false
    var newQueryInProgress = false

    val searchResults = currentQuery.flatMapLatest { query ->
        query?.let { searchNewsUseCase(query) } ?: emptyFlow()
    }.cachedIn(viewModelScope)

    fun onSearchQuerySubmit(query: String) {
        currentQuery.value = query
        pendingScrollToTopAfterNewQuery = true
        newQueryInProgress = true
    }

    fun saveArticle(article: NewsArticle) {
        viewModelScope.launch {
            saveArticleUseCase(article)
        }
    }
}