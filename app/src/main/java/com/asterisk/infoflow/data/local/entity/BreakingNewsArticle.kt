package com.asterisk.infoflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breaking_news_table")
data class BreakingNewsArticle(
    val articleUrl: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
