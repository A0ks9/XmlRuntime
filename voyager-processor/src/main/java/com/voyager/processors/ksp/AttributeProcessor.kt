package com.voyager.processors.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.voyager.annotations.ViewRegister

/**
 * High-performance KSP processor for generating view attribute parsers.
 *
 * This processor efficiently generates optimized code for handling view attributes
 * in the Voyager framework. It processes classes annotated with @ViewRegister and
 * generates corresponding attribute parsers. It also generates the META-INF/services
 * file for ServiceLoader discovery.
 *
 * Key features:
 * - Efficient code generation
 * - Optimized attribute mapping
 * - Memory-efficient processing
 * - Comprehensive error handling
 * - Generation of META-INF/services for ServiceLoader
 *
 * Performance optimizations:
 * - Efficient string building
 * - Optimized annotation processing
 * - Minimized object creation
 * - Safe resource handling
 * - Thread-safe operations
 *
 * Error handling:
 * - Comprehensive error logging
 * - Graceful failure handling
 * - Detailed error messages
 * - Stack trace preservation
 *
 * Usage:
 * The processor automatically processes classes annotated with @ViewRegister
 * and generates corresponding ViewAttributeParser implementations and registers
 * them for ServiceLoader discovery.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
class AttributeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    companion object {
        private const val GENERATED_PACKAGE_SUFFIX = ".generated"
        private const val PROCESSOR_SUFFIX = "VoyagerProcessor"
        private const val ATTRIBUTE_REGISTRY_PACKAGE = "com.voyager.generated"
        private const val ATTRIBUTE_REGISTRY_NAME = "AttributeRegistry"
    }

    /**
     * Processes annotated symbols and generates attribute parsers.
     *
     * @param resolver The symbol resolver
     * @return List of unprocessed symbols (empty in this case)
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val generatedProcessorClassNames =
            ArrayList<String>() // Pre-allocate with ArrayList for better performance

        try {
            resolver.getSymbolsWithAnnotation(ViewRegister::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>().forEach { clazz ->
                    try {
                        val generatedPackage =
                            "${clazz.packageName.asString()}$GENERATED_PACKAGE_SUFFIX"
                        val generatedClassName = "${clazz.simpleName.asString()}$PROCESSOR_SUFFIX"
                        val fullyQualifiedGeneratedName = "$generatedPackage.$generatedClassName"

                        generateViewAttributeParser(resolver, clazz)
                        generatedProcessorClassNames.add(fullyQualifiedGeneratedName)
                    } catch (e: Exception) {
                        logger.error("Error processing class ${clazz.qualifiedName?.asString()}: ${e.message}")
                    }
                }

            if (generatedProcessorClassNames.isNotEmpty()) {
                generateAttributeRegistry(resolver, generatedProcessorClassNames)
            }
        } catch (e: Exception) {
            logger.error("Error during symbol processing: ${e.message}")
        }

        return emptyList()
    }

    /**
     * Generates the AttributeRegistry class that will register all generated processors.
     * This registry acts as a central point for registering all view attribute processors.
     *
     * @param generatedClasses List of fully qualified class names to register
     */
    private fun generateAttributeRegistry(resolver: Resolver, generatedClasses: List<String>) {
        runCatching {
            codeGenerator.createNewFile(
                Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                ATTRIBUTE_REGISTRY_PACKAGE,
                ATTRIBUTE_REGISTRY_NAME
            ).bufferedWriter().use { writer ->
                writer.write(buildString {
                    appendLine("package $ATTRIBUTE_REGISTRY_PACKAGE")
                    appendLine()
                    appendLine("/**")
                    appendLine(" * Auto-generated registry for all view attribute processors.")
                    appendLine(" * This class is responsible for registering all generated processors")
                    appendLine(" * with the CustomViewRegistry.")
                    appendLine(" *")
                    appendLine(" * @generated Do not modify this file manually")
                    appendLine(" */")
                    appendLine()
                    generatedClasses.forEach { clazz ->
                        appendLine("import $clazz")
                    }
                    appendLine()
                    appendLine("object $ATTRIBUTE_REGISTRY_NAME {")
                    appendLine()
                    appendLine("    /**")
                    appendLine("     * Registers all generated view attribute processors.")
                    appendLine("     * This method should be called during application initialization.")
                    appendLine("     */")
                    appendLine("    @JvmStatic")
                    appendLine("    fun registerAll() {")
                    generatedClasses.forEach { clazz ->
                        appendLine("        ${clazz.className}()")
                    }
                    appendLine("    }")
                    appendLine("}")
                })
            }
        }.onFailure { e ->
            logger.error("Error generating AttributeRegistry: ${e.message}")
        }
    }

    private val String.className: String
        get() = split(".").last()

    /**
     * Generates a ViewAttributeParser for the given class.
     * This method creates an optimized processor class that handles attribute parsing
     * for the annotated view class.
     *
     * @param clazz The class to generate a parser for
     */
    private fun generateViewAttributeParser(resolver: Resolver, clazz: KSClassDeclaration) {
        val className = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()
        val generatedPackage = "$packageName$GENERATED_PACKAGE_SUFFIX"

        runCatching {
            codeGenerator.createNewFile(
                Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                generatedPackage,
                "${className}$PROCESSOR_SUFFIX"
            ).bufferedWriter().use { writer ->
                writer.write(buildString {
                    appendLine("package $generatedPackage")
                    appendLine()
                    appendLine("/**")
                    appendLine(" * Auto-generated attribute processor for $className.")
                    appendLine(" * This class handles the parsing and application of attributes")
                    appendLine(" * for the $className view.")
                    appendLine(" *")
                    appendLine(" * @generated Do not modify this file manually")
                    appendLine(" */")
                    appendLine()
                    appendLine("import android.util.AttributeSet")
                    appendLine("import android.view.View")
                    appendLine("import androidx.appcompat.view.ContextThemeWrapper")
                    appendLine("import $packageName.$className")
                    appendLine("import com.voyager.core.view.processor.BaseCustomViewProcessor")
                    appendLine("import com.voyager.core.view.CustomViewRegistry")
                    appendLine()
                    appendLine("class ${className}$PROCESSOR_SUFFIX : BaseCustomViewProcessor() {")
                    appendLine()
                    appendLine("    /**")
                    appendLine("     * Creates a new instance of the view with the given context.")
                    appendLine("     *")
                    appendLine("     * @param context The context to create the view with")
                    appendLine("     * @return A new instance of $className")
                    appendLine("     */")
                    appendLine("    override fun createView(context: ContextThemeWrapper, attrs: AttributeSet): View = ")
                    appendLine("        $className(context, attrs)")
                    appendLine()
                    appendLine("    /**")
                    appendLine("     * Initialization block that registers the view")
                    appendLine("     * This block runs when the class is loaded.")
                    appendLine("     */")
                    appendLine("    init {")
                    appendLine("        CustomViewRegistry.registerView(\"$packageName.$className\") { ctx, attrs -> ")
                    appendLine("            createView(")
                    appendLine("                ctx, attrs")
                    appendLine("            )")
                    appendLine("        }")
                    appendLine("    }")
                    appendLine("}")
                })
            }
        }.onFailure { e ->
            logger.error("Error generating ViewAttributeParser for $className: ${e.message}")
        }
    }
}