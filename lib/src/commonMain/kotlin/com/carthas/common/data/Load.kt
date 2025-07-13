package com.carthas.common.data

import androidx.compose.runtime.Immutable


/**
 * A sealed interface for encapsulating the state of a load operation.
 *
 * This interface can be implemented to represent different states of loading:
 * - `Loading`: Indicates that a loading operation is in progress.
 * - `Done`: Holds the successfully loaded result of type [ResultType].
 * - `Error`: Represents a failure during the load operation, encapsulating the associated error.
 *
 * Useful for modeling state in asynchronous operations or data loading scenarios.
 *
 * @param ResultType The type of the result or data being loaded.
 */
@Immutable
sealed interface Load<ResultType>

/**
 * Represents a loading state in the application.
 *
 * @param T The type of the result associated with the loading state.
 */
@Immutable
class Loading<T> : Load<T>
/**
 * Represents a successful load operation containing a value of type [T].
 *
 * @param value The result value of the successful operation.
 */
@Immutable
class Done<T>(val value: T) : Load<T>
/**
 * Represents a failed state within the [Load] sealed interface.
 *
 * @param T The type of the result that was expected.
 * @property error The [Throwable] instance describing the error that occurred.
 */
@Immutable
class Error<T>(val error: Throwable) : Load<T>

/**
 * Converts a [Result] into a [Load] representation.
 *
 * @return A [Load] object which is a [Done] if the result is successful, or an [Error] if the result is a failure.
 */
fun <R> Result<R>.toLoad(): Load<R> = fold(
    onSuccess = ::Done,
    onFailure = ::Error,
)