package com.asterisk.infoflow.data

import com.asterisk.infoflow.api.NewsApi
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val newsArticleDao: NewsArticleDao
) {

}