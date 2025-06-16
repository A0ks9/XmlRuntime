package com.voyager.core.utils

import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Utility functions for error handling and logging.
 * Provides safe execution of code blocks with proper error logging.
 */
@PublishedApi
internal class ErrorUtils(private val loggerClass: String? = null) {
    @PublishedApi
    internal val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }

    @PublishedApi
    internal val logger by lazy {
        LoggerFactory.getLogger(
            loggerClass ?: ErrorUtils::class.java.simpleName
        )
    }

    /**
     * Executes a block of code and returns its result, or null if an exception occurs.
     * Logs any exceptions that occur during execution.
     *
     * @param block The code block to execute
     * @param errorMessage The message to log if an error occurs
     * @return The result of the block, or null if an exception occurred
     */
    inline fun <T> tryOrLog(crossinline block: () -> T, place: String? = null, crossinline errorMessage: (Throwable) -> String): T? {
        return try {
            block()
        } catch (e: Exception) {
            if (isLoggingEnabled) {
                logger.error(place ?: "ErrorUtils", errorMessage.invoke(e), e)
            }
            null
        }
    }

    /**
     * Executes a block of code and returns its result, or a default value if an exception occurs.
     * Logs any exceptions that occur during execution.
     *
     * @param block The code block to execute
     * @param errorMessage The message to log if an error occurs
     * @param defaultValue The value to return if an exception occurs
     * @return The result of the block, or the default value if an exception occurred
     */
    inline fun <T> tryOrDefault(
        crossinline block: () -> T,
        place: String? = null,
        crossinline errorMessage: (Throwable) -> String,
        crossinline defaultValue: (Throwable) -> T,
    ): T {
        return try {
            block()
        } catch (e: Exception) {
            if (isLoggingEnabled) {
                logger.error(place ?: "ErrorUtils", errorMessage.invoke(e), e)
            }
            defaultValue.invoke(e)
        }
    }

    /**
     * Executes a block of code and returns a Result containing either the success value or the exception.
     * Does not log exceptions, allowing the caller to handle them.
     *
     * @param block The code block to execute
     * @return A Result containing either the success value or the exception
     */
    inline fun <T> tryOrResult(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Executes a block of code and returns its result. If an exception occurs,
     * it logs the error (if logging is enabled) and then re-throws the exception.
     *
     * @param block The code block to execute.
     * @param errorMessage An optional message to log if an error occurs.
     * @return The result of the block if execution is successful.
     * @throws Exception if any exception occurs during the execution of the block.
     */
    inline fun <T> tryOrThrow(
        crossinline block: () -> T,
        place: String? = null,
        errorMessage: String = "",
    ): T = try {
        block()
    } catch (e: Exception) {
        if (isLoggingEnabled) {
            logger.error(place ?: "ErrorUtils", errorMessage, e)
        }
        throw e
    }
} 