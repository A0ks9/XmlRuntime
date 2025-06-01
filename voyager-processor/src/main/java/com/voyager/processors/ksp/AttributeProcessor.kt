/**
 * High-performance KSP processor for generating view attribute parsers.
 *
 * This processor efficiently generates optimized code for handling view attributes
 * in the Voyager framework. It processes classes annotated with @ViewRegister and
 * generates corresponding attribute parsers.
 *
 * Key features:
 * - Efficient code generation
 * - Optimized attribute mapping
 * - Memory-efficient processing
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Efficient string building
 * - Optimized annotation processing
 * - Minimized object creation
 * - Safe resource handling
 *
 * Usage:
 * The processor automatically processes classes annotated with @ViewRegister
 * and generates corresponding ViewAttributeParser implementations.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.processors.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.voyager.annotations.ViewRegister

/**
 * Processor for generating view attribute parsers.
 *
 * This class handles the generation of ViewAttributeParser implementations
 * for classes annotated with @ViewRegister.
 */
class AttributeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    companion object {
        private const val VIEW_REGISTER_ANNOTATION = "ViewRegister"
        private const val ATTRIBUTE_ANNOTATION = "Attribute"
    }

    /**
     * Processes annotated symbols and generates attribute parsers.
     *
     * @param resolver The symbol resolver
     * @return List of unprocessed symbols (empty in this case)
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(ViewRegister::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { generateViewAttributeParser(it) }
        return emptyList()
    }

    /**
     * Generates a ViewAttributeParser for the given class.
     *
     * @param clazz The class to generate a parser for
     */
    private fun generateViewAttributeParser(clazz: KSClassDeclaration) {
        val className = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()
        val generatedPackage = "$packageName.generated"
        val viewClassName = getViewClassName(clazz)
        val attributeMappings = generateAttributeMappings(clazz)

        logger.info("Generating ViewAttributeParser for $className")

        runCatching {
            codeGenerator.createNewFile(
                Dependencies(false), generatedPackage, "${className}ViewAttributeParser"
            ).bufferedWriter().use { writer ->
                writer.write(buildString {
                    appendLine("package $generatedPackage")
                    appendLine()
                    appendLine("import android.view.View")
                    appendLine("import android.content.Context")
                    appendLine("import androidx.appcompat.view.ContextThemeWrapper")
                    appendLine("import $packageName.$className")
                    appendLine("import com.voyager.utils.processors.ViewAttributeParser")
                    appendLine("import com.voyager.utils.processors.ViewProcessor")
                    appendLine()
                    appendLine("class ${className}ViewAttributeParser : ViewAttributeParser() {")
                    appendLine()
                    appendLine("    override fun getViewType() = \"$viewClassName\"")
                    appendLine()
                    appendLine("    private val attributeMap = mapOf<String, (View, Any?) -> Unit>(")
                    appendLine("        $attributeMappings")
                    appendLine("    )")
                    appendLine()
                    appendLine("    override fun addAttributes() {")
                    appendLine("        registerAttributes(attributeMap)")
                    appendLine("    }")
                    appendLine()
                    appendLine("    override fun createView(context: ContextThemeWrapper): View = $className(context)")
                    appendLine()
                    appendLine("    init {")
                    appendLine("        ViewProcessor.registerView(\"$packageName\", \"$className\") { createView(it) }")
                    appendLine("        addAttributes()")
                    appendLine("    }")
                    appendLine("}")
                })
            }
        }.onFailure { e ->
            logger.error("Error generating ViewAttributeParser for $className: ${e.message}")
        }
    }

    /**
     * Extracts the view class name from the ViewRegister annotation.
     *
     * @param clazz The class to extract the name from
     * @return The view class name
     */
    private fun getViewClassName(clazz: KSClassDeclaration): String {
        val viewAnnotation =
            clazz.annotations.find { it.shortName.asString() == VIEW_REGISTER_ANNOTATION }
        val viewClassNameValue = viewAnnotation?.arguments?.firstOrNull()?.value as? String
            ?: clazz.qualifiedName?.asString() ?: ""

        logger.info("View class name value for ${clazz.simpleName.asString()}: $viewClassNameValue")
        return viewClassNameValue
    }

    /**
     * Generates attribute mappings for the given class.
     *
     * @param clazz The class to generate mappings for
     * @return The generated attribute mappings
     */
    private fun generateAttributeMappings(clazz: KSClassDeclaration): String {
        var functionMappingsSize = 0
        var propertyMappingsSize = 0
        val functionMappings = clazz.getAllFunctions()
            .filter { it.annotations.any { it.shortName.asString() == ATTRIBUTE_ANNOTATION } }
            .mapNotNull { function ->
                functionMappingsSize++
                generateFunctionMapping(clazz, function)
            }

        val propertyMappings = clazz.getAllProperties()
            .filter { it.annotations.any { it.shortName.asString() == ATTRIBUTE_ANNOTATION } }
            .mapNotNull { property ->
                propertyMappingsSize++
                generatePropertyMapping(clazz, property)
            }

        logger.info("Attribute mappings for ${clazz.simpleName.asString()}: ${functionMappingsSize + propertyMappingsSize} attributes found")
        return (functionMappings + propertyMappings).joinToString(",\n        ")
    }

    /**
     * Generates a mapping for a function attribute.
     *
     * @param clazz The containing class
     * @param function The function to generate a mapping for
     * @return The generated mapping or null if invalid
     */
    private fun generateFunctionMapping(
        clazz: KSClassDeclaration,
        function: com.google.devtools.ksp.symbol.KSFunctionDeclaration,
    ): String? {
        val attrName = extractAttrName(function.annotations)
        val paramType =
            function.parameters.firstOrNull()?.type?.resolve()?.declaration?.simpleName?.asString()

        if (attrName == null || paramType == null) {
            logger.warn("Skipping function: ${function.simpleName.asString()} due to missing attribute name or parameter type.")
            return null
        }

        return "\"$attrName\" to { v, value -> (v as ${clazz.simpleName.asString()}).${function.simpleName.asString()}(value as $paramType) }"
    }

    /**
     * Generates a mapping for a property attribute.
     *
     * @param clazz The containing class
     * @param property The property to generate a mapping for
     * @return The generated mapping or null if invalid
     */
    private fun generatePropertyMapping(
        clazz: KSClassDeclaration,
        property: com.google.devtools.ksp.symbol.KSPropertyDeclaration,
    ): String? {
        val attrName = extractAttrName(property.annotations)
        val propertyType = property.type.resolve().declaration.simpleName.asString()

        if (attrName == null) {
            logger.warn("Skipping property: ${property.simpleName.asString()} due to missing attribute name.")
            return null
        }

        return "\"$attrName\" to { v, value -> (v as ${clazz.simpleName.asString()}).${property.simpleName.asString()} = value as $propertyType }"
    }

    /**
     * Extracts the attribute name from annotations.
     *
     * @param annotations The annotations to extract from
     * @return The attribute name or null if not found
     */
    private fun extractAttrName(annotations: Sequence<KSAnnotation>): String? {
        return annotations.find { it.shortName.asString() == ATTRIBUTE_ANNOTATION }?.arguments?.firstOrNull()?.value?.toString()
    }
}