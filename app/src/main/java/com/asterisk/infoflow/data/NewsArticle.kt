package com.asterisk.infoflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_article_table")
data class NewsArticle(
    val title: String?,
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val thumbnailUrl: String?,
    val isSaved: Boolean,
    val updateAt: Long = System.currentTimeMillis()
)

