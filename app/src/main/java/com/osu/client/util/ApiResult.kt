package com.osu.client.util

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(call())
} catch (e: retrofit2.HttpException) {
    ApiResult.Error(
        message = e.response()?.errorBody()?.string() ?: e.message ?: "HTTP error",
        code = e.code()
    )
} catch (e: java.io.IOException) {
    ApiResult.Error("Network error. Check your connection.")
} catch (e: Exception) {
    ApiResult.Error(e.message ?: "Unknown error")
}
