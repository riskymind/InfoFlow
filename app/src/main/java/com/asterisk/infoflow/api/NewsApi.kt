package com.asterisk.infoflow.api

import com.asterisk.infoflow.utils.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    @Headers("X-Api-key: $API_KEY")
    @GET("top-headlines?country=ng&pageSize=100")
    suspend fun getBreakingNews(): NewsResponse

    @Headers("X-Api-key: $API_KEY")
    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page")page: Int,
        @Query("pageSize")pageSize: Int
    ): NewsResponse

}