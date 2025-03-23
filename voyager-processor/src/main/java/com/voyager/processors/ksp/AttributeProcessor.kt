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

class AttributeProcessor(
    private val codeGenerator: CodeGenerator, private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(ViewRegister::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { generateViewAttributeParser(it) }
        return emptyList()
    }

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
        }.onFailure { e -> logger.error("Error generating ViewAttributeParser for $className: ${e.message}") }
    }

    private fun getViewClassName(clazz: KSClassDeclaration): String {
        val viewAnnotation = clazz.annotations.find { it.shortName.asString() == "ViewRegister" }

        val viewClassNameValue = viewAnnotation?.arguments?.firstOrNull()?.value as? String
            ?: clazz.qualifiedName?.asString() ?: ""

        logger.info("View class name value for ${clazz.simpleName.asString()}: $viewClassNameValue")
        return viewClassNameValue
    }

    private fun generateAttributeMappings(clazz: KSClassDeclaration): String {
        val attributeMappings = clazz.getAllFunctions().filter {
            it.annotations.any { it.shortName.asString() == "Attribute" }
        }.mapNotNull { function ->
            val attrName = extractAttrName(function.annotations)

            val paramType =
                function.parameters.firstOrNull()?.type?.resolve()?.declaration?.simpleName?.asString()

            if (attrName == null || paramType == null) {
                logger.warn("Skipping function: ${function.simpleName.asString()} due to missing attribute name or parameter type.")
                return@mapNotNull null
            }

            "\"$attrName\" to { v, value -> (v as ${clazz.simpleName.asString()}).${function.simpleName.asString()}(value as $paramType) }"
        }.toList()

        val propertyMappings = clazz.getAllProperties().filter {
            it.annotations.any { it.shortName.asString() == "Attribute" }
        }.mapNotNull { property ->
            val attrName = extractAttrName(property.annotations)

            val propertyType = property.type.resolve().declaration.simpleName.asString()

            if (attrName == null) {
                logger.warn("Skipping property: ${property.simpleName.asString()} due to missing attribute name.")
                return@mapNotNull null
            }

            "\"$attrName\" to { v, value -> (v as ${clazz.simpleName.asString()}).${property.simpleName.asString()} = value as $propertyType }"
        }.toList()

        logger.info("Attribute mappings for ${clazz.simpleName.asString()}: ${attributeMappings.size + propertyMappings.size} attributes found")

        return (attributeMappings + propertyMappings).joinToString(",\n        ")
    }

    private fun extractAttrName(annotations: Sequence<KSAnnotation>): String? {
        annotations.find { it.shortName.asString() == "Attribute" }?.arguments?.forEach { arg ->
            return arg.value.toString()
        }
        return null
    }
}
