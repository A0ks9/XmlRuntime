package com.voyager.core.utils.logging

/**
 * A default logger implementation that does nothing.
 * This is used when no custom logger is provided by the application.
 */
class NoOpLogger : Logger {
    override fun d(tag: String, message: String, throwable: Throwable?) {}
    override fun i(tag: String, message: String, throwable: Throwable?) {}
    override fun w(tag: String, message: String, throwable: Throwable?) {}
    override fun e(tag: String, message: String, throwable: Throwable?) {}
} 