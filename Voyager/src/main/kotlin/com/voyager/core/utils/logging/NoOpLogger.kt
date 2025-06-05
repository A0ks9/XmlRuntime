package com.voyager.core.utils.logging

/**
 * A default logger implementation that does nothing.
 * This is used when no custom logger is provided by the application.
 *
 * Key Features:
 * - Zero overhead logging
 * - Thread-safe operations
 * - Memory efficient
 * - Performance optimized
 *
 * Best Practices:
 * 1. Use only as a fallback
 * 2. Replace with proper logger in production
 * 3. Consider performance impact
 * 4. Handle logger configuration properly
 *
 * Example Usage:
 * ```kotlin
 * // Default logger (does nothing)
 * val logger = NoOpLogger()
 *
 * // Replace with custom logger
 * LoggerFactory.setLogger(MyCustomLogger())
 * ```
 */
class NoOpLogger : Logger {
    /**
     * No-op implementation of verbose logging.
     * @param tag The log tag (ignored)
     * @param message The log message (ignored)
     * @param throwable The throwable (ignored)
     */
    override fun verbose(tag: String, message: String, throwable: Throwable?) {}

    /**
     * No-op implementation of debug logging.
     * @param tag The log tag (ignored)
     * @param message The log message (ignored)
     * @param throwable The throwable (ignored)
     */
    override fun debug(tag: String, message: String, throwable: Throwable?) {}

    /**
     * No-op implementation of info logging.
     * @param tag The log tag (ignored)
     * @param message The log message (ignored)
     * @param throwable The throwable (ignored)
     */
    override fun info(tag: String, message: String, throwable: Throwable?) {}

    /**
     * No-op implementation of warning logging.
     * @param tag The log tag (ignored)
     * @param message The log message (ignored)
     * @param throwable The throwable (ignored)
     */
    override fun warn(tag: String, message: String, throwable: Throwable?) {}

    /**
     * No-op implementation of error logging.
     * @param tag The log tag (ignored)
     * @param message The log message (ignored)
     * @param throwable The throwable (ignored)
     */
    override fun error(tag: String, message: String, throwable: Throwable?) {}

    /**
     * No-op implementation of level-based logging.
     * @param level The log level (ignored)
     * @param tag The log tag (ignored)
     * @param message The log message (ignored)
     * @param throwable The throwable (ignored)
     */
    override fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {}
} 