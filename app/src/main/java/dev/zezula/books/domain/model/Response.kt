package dev.zezula.books.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

sealed class Response<out T> {

    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val t: Throwable) : Response<Nothing>()

    fun onError(action: (exception: Throwable) -> Unit): Response<T> {
        if (this is Error) {
            action(this.t)
        }
        return this
    }

    fun fold(onFailure: (exception: Throwable) -> Unit, onSuccess: (data: T) -> Unit) {
        if (this is Error) {
            onFailure(this.t)
        }
        if (this is Success) {
            onSuccess(this.data)
        }
    }
}

fun <R, T : R> Response<T>.getOrDefault(default: R): R {
    return if (this is Response.Success) {
        this.data
    } else {
        default
    }
}

fun <T> Flow<Response<T>>.onResponseError(action: (Throwable) -> Unit): Flow<Response<T>> = flow {
    collect {
        if (it is Response.Error) {
            action(it.t)
        }
        emit(it)
    }
}

inline fun <T, R> T.asResponse(block: T.() -> R): Response<R> {
    return try {
        Response.Success(block())
    } catch (e1: HttpException) {
        Timber.w("Failed to get data: ${e1.message}")
        Response.Error(e1)
    } catch (e2: IOException) {
        Timber.w("Failed to get data: ${e2.message}")
        Response.Error(e2)
    }
}

fun <T> Flow<T>.asResponse(): Flow<Response<T>> {
    return this.map<T, Response<T>> {
        Response.Success(it)
    }.catch { e ->
        when (e) {
            is HttpException, is IOException -> {
                Timber.w("Failed to get data: ${e.message}")
                emit(Response.Error(e))
            }
            else -> {
                throw e
            }
        }
    }
}
