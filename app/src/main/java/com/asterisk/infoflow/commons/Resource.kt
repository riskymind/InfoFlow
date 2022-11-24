package com.asterisk.infoflow.commons

sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(error: String, data: T) : Resource<T>(data, error)
    class Loading<T>(data: T?) : Resource<T>(data)
}
