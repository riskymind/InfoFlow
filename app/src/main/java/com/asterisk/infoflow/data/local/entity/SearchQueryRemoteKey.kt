package com.asterisk.infoflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_query_remote_key_table")
data class SearchQueryRemoteKey(
    @PrimaryKey val searchQuery: String,
    val nextPage: Int
)
