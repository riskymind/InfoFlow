package com.asterisk.infoflow.data.remote.dto

import com.asterisk.infoflow.data.remote.dto.NewsArticleDTO

data class NewsResponse(
    val articles: List<NewsArticleDTO>
)
