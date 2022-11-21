package com.asterisk.infoflow.commons

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = channelFlow {

    val data = query().first()

    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(Resource.Loading(it)) }
        }
        try {
            delay(2000)
            saveFetchResult(fetch())
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        } catch (e: HttpException) {
            loading.cancel()
            query().collect { send(Resource.Error(e.localizedMessage ?: "unexpected error.", it)) }
        } catch (e: IOException) {
            loading.cancel()
            query().collect {
                send(
                    Resource.Error(
                        "couldn't reach the sever, check internet connection",
                        it
                    )
                )
            }
        }
    } else {
        query().collect { send(Resource.Success(it)) }
    }
}