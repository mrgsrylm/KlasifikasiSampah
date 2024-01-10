package io.github.mrsrylm.skso.common

sealed class NetworkResponseStatus<out T : Any> {
    data object Load : NetworkResponseStatus<Nothing>()
    data class Success<out T : Any>(val result: T) : NetworkResponseStatus<T>()
    data class Error(val exception: Exception) : NetworkResponseStatus<Nothing>()
}