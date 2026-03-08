package com.epic_engine.swisskit.core.common

sealed class SwissKitException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    class DatabaseException(message: String, cause: Throwable? = null) : SwissKitException(message, cause)
    class NetworkException(message: String, cause: Throwable? = null) : SwissKitException(message, cause)
    class NotFound(message: String) : SwissKitException(message)
    class PermissionDenied(message: String) : SwissKitException(message)
    class Unknown(message: String, cause: Throwable? = null) : SwissKitException(message, cause)
}
