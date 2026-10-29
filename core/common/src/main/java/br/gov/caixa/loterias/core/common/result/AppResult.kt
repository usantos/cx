package br.gov.caixa.loterias.core.common.result

import java.io.IOException
import kotlinx.coroutines.CancellationException

sealed interface AppError {
    data object Network : AppError
    data class Validation(val reason: String) : AppError
    data object Unexpected : AppError
}

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

suspend fun <T> appResultOf(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (validation: IllegalArgumentException) {
    AppResult.Failure(AppError.Validation(validation.message.orEmpty()))
} catch (_: IOException) {
    AppResult.Failure(AppError.Network)
} catch (_: Exception) {
    AppResult.Failure(AppError.Unexpected)
}

fun <T, R> AppResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (AppError) -> R,
): R = when (this) {
    is AppResult.Success -> onSuccess(value)
    is AppResult.Failure -> onFailure(error)
}
