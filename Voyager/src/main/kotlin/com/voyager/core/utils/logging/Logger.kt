package com.voyager.core.utils.logging

/**
 * Interface for the logging system within the Voyager library.
 * Applications using the library can provide their own implementation of this interface
 * to handle logs from the library.
 */
interface Logger {
    /**
     * Logs a debug message.
     * @param tag Used to identify the source of a log message. It usually names the class where the call originates.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun d(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs an info message.
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun i(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a warning message.
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs an error message.
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null)
} 