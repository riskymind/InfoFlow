package com.asterisk.infoflow.data.local.entity

import androidx.room.Entity

@Entity(tableName = "search_result_table", primaryKeys = ["searchQuery","articleUrl"])
data class SearchResult(
    val searchQuery: String,
    val articleUrl: String,
    val queryPosition: Int
)
