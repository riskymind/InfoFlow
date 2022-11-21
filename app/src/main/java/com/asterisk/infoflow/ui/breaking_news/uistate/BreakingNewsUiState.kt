package com.asterisk.infoflow.ui.breaking_news.uistate

import com.asterisk.infoflow.domain.model.NewsArticle

data class BreakingNewsUiState(
    val breakingNews: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isError: String = ""
)
