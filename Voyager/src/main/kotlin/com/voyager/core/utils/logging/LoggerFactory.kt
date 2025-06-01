package com.voyager.core.utils.logging

/**
 * Provides access to the configured [Logger] instance.
 * The application using the library can set a custom [Logger] implementation via [setLogger].
 */
object LoggerFactory {
    @Volatile
    private var currentLogger: Logger = NoOpLogger()

    /**
     * Sets the [Logger] instance to be used by the library.
     * This should be called once during the application's initialization.
     * @param logger The logger implementation to use.
     */
    fun setLogger(logger: Logger) {
        currentLogger = logger
    }

    /**
     * Returns the currently configured [Logger] instance.
     * @return The current Logger.
     */
    fun getLogger(): Logger = currentLogger

    /**
     * Returns a [Logger] instance with a specific tag.
     * This is a convenience function for classes to get a logger instance with their class name as the tag.
     * @param tag The tag for the logger.
     * @return A Logger instance.
     */
    fun getLogger(tag: String): Logger = TaggedLogger(currentLogger, tag)
}

/**
 * A wrapper logger that adds a tag to the messages before forwarding to the underlying logger.
 */
private class TaggedLogger(private val logger: Logger, private val tag: String) : Logger {
    override fun d(tag: String, message: String, throwable: Throwable?) {
        logger.d(this.tag, message, throwable)
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        logger.i(this.tag, message, throwable)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        logger.w(this.tag, message, throwable)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        logger.e(this.tag, message, throwable)
    }
} 