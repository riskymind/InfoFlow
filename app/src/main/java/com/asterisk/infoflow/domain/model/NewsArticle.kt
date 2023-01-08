package com.asterisk.infoflow.domain.model

import androidx.room.PrimaryKey
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity

data class NewsArticle(
    val title: String?,
    val url: String,
    val urlToImage: String?,
    val isSaved: Boolean,
    val updatedAt: Long
) {
    fun toNewsArticleEntity(): NewsArticleEntity {
        return NewsArticleEntity(
            title = title,
            url = url,
            thumbnailUrl = urlToImage,
            isSaved = isSaved,
            updateAt = updatedAt
        )
    }
}