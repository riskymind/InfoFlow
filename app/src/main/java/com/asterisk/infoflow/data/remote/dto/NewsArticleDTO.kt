package com.asterisk.infoflow.data.remote.dto

import com.asterisk.infoflow.data.local.entity.BreakingNewsArticle
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity
import com.asterisk.infoflow.domain.model.NewsArticle

data class NewsArticleDTO(
    val title: String?,
    val url: String,
    val urlToImage: String?
)

fun NewsArticleDTO.toNewsArticleEntity(): NewsArticleEntity {
    return NewsArticleEntity(
        title = title,
        url = url,
        thumbnailUrl = urlToImage
    )
}

fun NewsArticleDTO.toBreakingNewsArticle(): BreakingNewsArticle {
    return BreakingNewsArticle(articleUrl = url)
}
