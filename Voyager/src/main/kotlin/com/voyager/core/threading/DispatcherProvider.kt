package com.voyager.core.threading

import com.voyager.core.exceptions.VoyagerConfigException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides coroutine dispatchers for different operations in the Voyager framework.
 * This allows for better testing, control over threading, and performance optimization.
 *
 * Features:
 * - Thread-safe dispatcher management
 * - Configurable dispatchers for testing
 * - Performance optimization
 * - Memory efficiency
 * - Detailed logging
 *
 * Example Usage:
 * ```kotlin
 * // Default configuration
 * val dispatcherProvider = DispatcherProvider()
 *
 * // Custom configuration for testing
 * val testDispatcherProvider = DispatcherProvider(
 *     main = TestDispatcher(),
 *     io = TestDispatcher(),
 *     default = TestDispatcher()
 * )
 * ```
 *
 * @property main Dispatcher for UI operations
 * @property io Dispatcher for I/O operations
 * @property default Dispatcher for CPU-intensive operations
 * @property unconfined Dispatcher for operations that don't require specific threading
 * @throws VoyagerConfigException.InvalidConfigValueException if any dispatcher is null
 */
class DispatcherProvider(
    val main: CoroutineDispatcher = Dispatchers.Main,
    val io: CoroutineDispatcher = Dispatchers.IO,
    val default: CoroutineDispatcher = Dispatchers.Default,
    val unconfined: CoroutineDispatcher = Dispatchers.Unconfined,
)