package com.voyager.core.utils.logging

/**
 * Factory class for creating and managing logger instances.
 * Provides access to the configured [Logger] instance and allows for custom logger implementations.
 *
 * Key Features:
 * - Thread-safe logger management
 * - Support for custom logger implementations
 * - Tagged logger creation
 * - Default logger fallback
 * - Performance optimized
 *
 * Best Practices:
 * 1. Set logger once during application initialization
 * 2. Use appropriate logger types
 * 3. Consider performance impact
 * 4. Handle logger configuration properly
 * 5. Use tagged loggers for better organization
 *
 * Example Usage:
 * ```kotlin
 * // Set custom logger
 * LoggerFactory.setLogger(MyCustomLogger())
 *
 * // Get base logger
 * val logger = LoggerFactory.getLogger()
 *
 * // Get tagged logger
 * val taggedLogger = LoggerFactory.getLogger("MyClass")
 * ```
 */
object LoggerFactory {
    @Volatile
    private var currentLogger: Logger = NoOpLogger()
    private val loggerCache = mutableMapOf<String, TaggedLoggerInterface>()

    /**
     * Sets the [Logger] instance to be used by the library.
     * This should be called once during the application's initialization.
     * Thread-safe operation.
     *
     * @param logger The logger implementation to use.
     * @throws IllegalArgumentException if logger is null
     */
    @Synchronized
    fun setLogger(logger: Logger) {
        requireNotNull(logger) { "Logger cannot be null" }
        currentLogger = logger
        // Clear cache when logger changes
        loggerCache.clear()
    }

    /**
     * Returns the currently configured base [Logger] instance.
     * This logger requires a tag parameter for each log method.
     * Thread-safe operation.
     *
     * @return The current base Logger.
     */
    fun getLogger(): Logger = currentLogger

    /**
     * Returns a TaggedLoggerInterface instance.
     * This logger does NOT require a tag parameter for its log methods,
     * but accepts an optional 'place' string.
     * Thread-safe operation with caching.
     *
     * @param tag The tag to use for all log messages from this logger.
     * @return A TaggedLoggerInterface instance.
     * @throws IllegalArgumentException if tag is null or empty
     */
    @Synchronized
    fun getLogger(tag: String): TaggedLoggerInterface {
        require(tag.isNotBlank()) { "Tag cannot be null or empty" }
        return loggerCache.getOrPut(tag) { TaggedLogger(currentLogger, tag) }
    }

    /**
     * Clears the logger cache.
     * Useful for testing or when logger configuration changes.
     * Thread-safe operation.
     */
    @Synchronized
    fun clearCache() {
        loggerCache.clear()
    }
}

/**
 * An internal implementation of [TaggedLoggerInterface] that adds a tag to the messages
 * before forwarding to the underlying base [Logger]. It combines the predefined tag
 * with an optional place string.
 *
 * Key Features:
 * - Efficient tag formatting
 * - Thread-safe operations
 * - Performance optimized
 * - Support for all log levels
 *
 * @property logger The base logger to use
 * @property tag The predefined tag for all messages
 */
private class TaggedLogger(private val logger: Logger, private val tag: String) : TaggedLoggerInterface {
    /**
     * Formats the tag by combining the predefined tag with an optional place.
     * Thread-safe operation.
     *
     * @param place An optional string specifying the location within the tagged source
     * @return The formatted tag
     */
    private fun formatTag(place: String?): String {
        return if (place.isNullOrBlank()) {
            tag
        } else {
            "$tag - $place"
        }
    }

    override fun verbose(place: String?, message: String, throwable: Throwable?) {
        logger.verbose(formatTag(place), message, throwable)
    }

    override fun debug(place: String?, message: String, throwable: Throwable?) {
        logger.debug(formatTag(place), message, throwable)
    }

    override fun info(place: String?, message: String, throwable: Throwable?) {
        logger.info(formatTag(place), message, throwable)
    }

    override fun warn(place: String?, message: String, throwable: Throwable?) {
        logger.warn(formatTag(place), message, throwable)
    }

    override fun error(place: String?, message: String, throwable: Throwable?) {
        logger.error(formatTag(place), message, throwable)
    }

    override fun log(level: LogLevel, place: String?, message: String, throwable: Throwable?) {
        logger.log(level, formatTag(place), message, throwable)
    }
}