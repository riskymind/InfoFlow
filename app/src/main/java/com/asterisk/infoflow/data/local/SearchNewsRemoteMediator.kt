package com.asterisk.infoflow.data.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.asterisk.infoflow.commons.Constants.NEWS_STARTING_PAGE_INDEX
import com.asterisk.infoflow.data.local.entity.NewsArticleEntity
import com.asterisk.infoflow.data.local.entity.SearchQueryRemoteKey
import com.asterisk.infoflow.data.local.entity.SearchResult
import com.asterisk.infoflow.data.remote.NewsApi
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SearchNewsRemoteMediator(
    private val searchQuery: String,
    private val newsApi: NewsApi,
    private val newsArticleDatabase: NewsArticleDatabase
) : RemoteMediator<Int, NewsArticleEntity>() {

    private val newsArticleDao = newsArticleDatabase.newsArticleDao()
    private val searchQueryRemoteKeyDao = newsArticleDatabase.searchQueryRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> NEWS_STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> searchQueryRemoteKeyDao.getRemoteKey(searchQuery).nextPage
        }

        try {
            val response = newsApi.searchNews(searchQuery, page, state.config.pageSize)
            val serverArticles = response.articles
            val savedArticles = newsArticleDao.getAllSavedArticles().first()

            val articles = serverArticles.map { newsDTO ->
                val isSaved = savedArticles.any { it.url == newsDTO.url }
                NewsArticleEntity(
                    title = newsDTO.title,
                    url = newsDTO.url,
                    thumbnailUrl = newsDTO.urlToImage,
                    isSaved = isSaved
                )
            }

            newsArticleDatabase.withTransaction {
                // Delete search result if refreshed
                if (loadType == LoadType.REFRESH) {
                    newsArticleDao.deleteSearchResults(searchQuery)
                }

                val lastQueryPosition = newsArticleDao.getLastQueryPosition(searchQuery) ?: 0
                var queryPosition = lastQueryPosition + 1

                val results = articles.map { article ->
                    SearchResult(searchQuery, article.url, queryPosition++)
                }

                val nextPageKey = page + 1

                newsArticleDao.insertNewsArticle(articles)
                newsArticleDao.insertSearchResult(results)
                searchQueryRemoteKeyDao.insertRemoteKey(
                    SearchQueryRemoteKey(
                        searchQuery,
                        nextPageKey
                    )
                )
            }

            return MediatorResult.Success(endOfPaginationReached = serverArticles.isEmpty())
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

}