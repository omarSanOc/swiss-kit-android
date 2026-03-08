package com.epic_engine.swisskit.core.common

sealed class SwissKitResult<out T> {
    data class Success<T>(val data: T) : SwissKitResult<T>()
    data class Error(val exception: SwissKitException) : SwissKitResult<Nothing>()
}

inline fun <T> SwissKitResult<T>.onSuccess(action: (T) -> Unit): SwissKitResult<T> {
    if (this is SwissKitResult.Success) action(data)
    return this
}

inline fun <T> SwissKitResult<T>.onError(action: (SwissKitException) -> Unit): SwissKitResult<T> {
    if (this is SwissKitResult.Error) action(exception)
    return this
}
