package io.github.mrsrylm.skso.common

sealed class ScreenStatus<out T : Any> {
    data object Loading : ScreenStatus<Nothing>()
    data class Error(val message: String) : ScreenStatus<Nothing>()
    data class Success<out T : Any>(val uiData: T) : ScreenStatus<T>()
}