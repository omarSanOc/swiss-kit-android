package com.epic_engine.swisskit.core.common

import timber.log.Timber

object SwissKitLogger {
    fun d(module: String, message: String) = Timber.tag("SwissKit[$module]").d(message)

    fun i(module: String, message: String) = Timber.tag("SwissKit[$module]").i(message)

    fun w(module: String, message: String) = Timber.tag("SwissKit[$module]").w(message)

    fun e(module: String, message: String, throwable: Throwable? = null) =
        Timber.tag("SwissKit[$module]").e(throwable, message)
}
