/**
 * This file defines `KspProcessorProvider`, the service provider interface implementation
 * for KSP (Kotlin Symbol Processing) in the Voyager processor module.
 *
 * KSP discovers and loads symbol processors through implementations of the
 * [SymbolProcessorProvider] interface. This class serves as that entry point,
 * responsible for creating and providing instances of Voyager's [AttributeProcessor].
 *
 * @see com.google.devtools.ksp.processing.SymbolProcessorProvider
 * @see com.voyager.processors.ksp.AttributeProcessor
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.processors

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.voyager.processors.ksp.AttributeProcessor

/**
 * `KspProcessorProvider` is the entry point for the Kotlin Symbol Processing (KSP) API.
 *
 * KSP discovers this class through the service provider configuration file
 * (`META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider`).
 * When KSP initializes, it calls the [create] method of this provider to obtain an
 * instance of the [SymbolProcessor] (in this case, [AttributeProcessor]) that will
 * perform the actual annotation processing and code generation.
 *
 * This class is responsible for instantiating the [AttributeProcessor] and providing it
 * with essential KSP services from the [SymbolProcessorEnvironment], such as the
 * [com.google.devtools.ksp.processing.CodeGenerator] and [com.google.devtools.ksp.processing.KSPLogger].
 */
class KspProcessorProvider : SymbolProcessorProvider {

    /**
     * Called by KSP to create a new instance of the [SymbolProcessor].
     *
     * This implementation instantiates and returns an [AttributeProcessor],
     * injecting it with the necessary [SymbolProcessorEnvironment.codeGenerator] and
     * [SymbolProcessorEnvironment.logger] from the KSP environment.
     *
     * @param environment The KSP [SymbolProcessorEnvironment] providing access to KSP services
     *                    like code generation and logging.
     * @return A new instance of [AttributeProcessor] configured with the provided environment.
     */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AttributeProcessor(
            environment.codeGenerator, environment.logger
        )
    }
}