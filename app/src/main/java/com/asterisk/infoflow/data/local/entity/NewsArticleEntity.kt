package com.asterisk.infoflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.asterisk.infoflow.data.remote.dto.NewsArticleDTO
import com.asterisk.infoflow.domain.model.NewsArticle

@Entity(tableName = "news_article_table")
data class NewsArticleEntity(
    val title: String?,
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val thumbnailUrl: String?,
    val isSaved: Boolean = false,
    val updateAt: Long = System.currentTimeMillis()
) {

    fun toBreakingNewsArticle(): BreakingNewsArticle {
        return BreakingNewsArticle(articleUrl = url)
    }
}

fun NewsArticleEntity.toNewsArticle(): NewsArticle {
    return NewsArticle(
        title = title,
        url = url,
        urlToImage = thumbnailUrl,
        isSaved = isSaved,
        updatedAt = updateAt
    )
}




