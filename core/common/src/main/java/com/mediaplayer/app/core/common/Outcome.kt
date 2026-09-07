package com.mediaplayer.app.core.common

import kotlinx.coroutines.CancellationException

sealed interface Outcome<out T> {
    data class Success<T>(
        val data: T,
    ) : Outcome<T>

    data class Error(
        val throwable: Throwable,
    ) : Outcome<Nothing>
}

inline fun <T, R> Outcome<T>.fold(
    onSuccess: (T) -> R,
    onError: (Throwable) -> R,
): R =
    when (this) {
        is Outcome.Success -> onSuccess(data)
        is Outcome.Error -> onError(throwable)
    }

fun <T> Outcome<T>.getOrNull(): T? = fold(onSuccess = { it }, onError = { null })

/**
 * A generic catch-all is the point of this helper: any failure from [block] becomes an
 * [Outcome.Error] instead of propagating, while coroutine cancellation is always rethrown.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> runAsOutcome(block: () -> T): Outcome<T> =
    try {
        Outcome.Success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Exception) {
        Outcome.Error(e)
    }
