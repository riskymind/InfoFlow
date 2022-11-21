package com.asterisk.infoflow.domain.model

data class NewsArticle(
    val title: String?,
    val url: String,
    val urlToImage: String?,
    val isSaved: Boolean,
)
