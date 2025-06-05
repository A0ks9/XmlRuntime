package com.voyager.core.utils.logging

/**
 * Interface for the logging system within the Voyager library.
 * Applications using the library can provide their own implementation of this interface
 * to handle logs from the library.
 *
 * Key Features:
 * - Multiple log levels (VERBOSE, DEBUG, INFO, WARN, ERROR)
 * - Thread-safe logging operations
 * - Support for throwable stack traces
 * - Tag-based log categorization
 * - Performance optimized
 *
 * Best Practices:
 * 1. Use appropriate log levels
 * 2. Include meaningful tags
 * 3. Provide detailed messages
 * 4. Handle throwables properly
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * class MyLogger : Logger {
 *     override fun debug(tag: String, message: String, throwable: Throwable?) {
 *         // Implementation
 *     }
 *     // Other methods...
 * }
 * ```
 */
interface Logger {
    /**
     * Logs a verbose message.
     * Use for detailed debugging information that is typically only needed during development.
     *
     * @param tag Used to identify the source of a log message. It usually names the class where the call originates.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun verbose(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a debug message.
     * Use for debugging information that is helpful during development but not essential in production.
     *
     * @param tag Used to identify the source of a log message. It usually names the class where the call originates.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun debug(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs an info message.
     * Use for general information about application flow.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun info(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a warning message.
     * Use for potentially harmful situations that don't prevent the application from working.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun warn(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs an error message.
     * Use for serious problems that prevent the application from working correctly.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun error(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a message with the specified level.
     * This is a convenience method for logging with a specific level.
     *
     * @param level The log level to use.
     * @param tag Used to identify the source of a log message.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        when (level) {
            LogLevel.VERBOSE -> verbose(tag, message, throwable)
            LogLevel.DEBUG -> debug(tag, message, throwable)
            LogLevel.INFO -> info(tag, message, throwable)
            LogLevel.WARN -> warn(tag, message, throwable)
            LogLevel.ERROR -> error(tag, message, throwable)
        }
    }
}

/**
 * Enum representing different log levels.
 * Used to categorize log messages by their importance and purpose.
 */
enum class LogLevel {
    VERBOSE,  // Most detailed logging level
    DEBUG,    // Debugging information
    INFO,     // General information
    WARN,     // Warning messages
    ERROR     // Error messages
}

/**
 * Interface for a logger that has a predefined tag.
 * Methods in this interface do not require a tag parameter; the call site information
 * (class and method) is automatically determined.
 *
 * Key Features:
 * - Predefined tag for all log messages
 * - Optional place parameter for more specific location
 * - Support for all log levels
 * - Thread-safe operations
 *
 * Example Usage:
 * ```kotlin
 * val logger = LoggerFactory.getLogger("MyClass")
 * logger.debug("methodName", "Processing data")
 * ```
 */
interface TaggedLoggerInterface {
    /**
     * Logs a verbose message with the predefined tag.
     * @param place An optional string specifying the location within the tagged source (e.g., function name).
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun verbose(place: String? = null, message: String, throwable: Throwable? = null)

    /**
     * Logs a debug message with the predefined tag.
     * @param place An optional string specifying the location within the tagged source (e.g., function name).
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun debug(place: String? = null, message: String, throwable: Throwable? = null)

    /**
     * Logs an info message with the predefined tag.
     * @param place An optional string specifying the location within the tagged source.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun info(place: String? = null, message: String, throwable: Throwable? = null)

    /**
     * Logs a warning message with the predefined tag.
     * @param place An optional string specifying the location within the tagged source.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun warn(place: String? = null, message: String, throwable: Throwable? = null)

    /**
     * Logs an error message with the predefined tag.
     * @param place An optional string specifying the location within the tagged source.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun error(place: String? = null, message: String, throwable: Throwable? = null)

    /**
     * Logs a message with the specified level.
     * @param level The log level to use.
     * @param place An optional string specifying the location within the tagged source.
     * @param message The message to log.
     * @param throwable An optional Throwable to be logged with the message.
     */
    fun log(level: LogLevel, place: String? = null, message: String, throwable: Throwable? = null) {
        when (level) {
            LogLevel.VERBOSE -> verbose(place, message, throwable)
            LogLevel.DEBUG -> debug(place, message, throwable)
            LogLevel.INFO -> info(place, message, throwable)
            LogLevel.WARN -> warn(place, message, throwable)
            LogLevel.ERROR -> error(place, message, throwable)
        }
    }
}