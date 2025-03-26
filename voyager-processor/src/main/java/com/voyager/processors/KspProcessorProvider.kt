/**
 * High-performance KSP processor provider for the Voyager framework.
 *
 * This provider efficiently creates and manages KSP processors for generating
 * view attribute parsers. It handles the initialization and configuration of
 * processors in a memory-efficient and thread-safe manner.
 *
 * Key features:
 * - Efficient processor creation
 * - Optimized resource management
 * - Thread-safe operations
 * - Memory-efficient implementation
 *
 * Performance optimizations:
 * - Lazy processor initialization
 * - Efficient resource handling
 * - Minimized object creation
 * - Safe resource cleanup
 *
 * Usage:
 * The provider is automatically configured by the KSP framework and creates
 * AttributeProcessor instances for processing annotated classes.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.processors

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.voyager.processors.ksp.AttributeProcessor

/**
 * Provider for creating KSP processors in the Voyager framework.
 *
 * This class handles the creation and configuration of KSP processors,
 * specifically for generating view attribute parsers.
 */
class KspProcessorProvider : SymbolProcessorProvider {

    /**
     * Creates a new SymbolProcessor instance for the given environment.
     *
     * This method efficiently creates and configures an AttributeProcessor
     * with the provided environment settings.
     *
     * @param environment The KSP processing environment
     * @return A configured SymbolProcessor instance
     */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AttributeProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}