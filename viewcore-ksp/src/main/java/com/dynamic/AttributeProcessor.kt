package com.dynamic

import com.dynamic.annotations.AutoViewAttributes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class AttributeProcessor(
    private val codeGenerator: CodeGenerator, private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(AutoViewAttributes::class.qualifiedName!!)
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
                Dependencies.ALL_FILES, generatedPackage, "${className}ViewAttributeParser"
            ).bufferedWriter().use { writer ->
                writer.write(buildString {
                    appendLine("package $generatedPackage")
                    appendLine()
                    appendLine("import android.view.View")
                    appendLine("import android.content.Context")
                    appendLine("import androidx.appcompat.view.ContextThemeWrapper")
                    appendLine("import $packageName.$className")
                    appendLine("import com.dynamic.utils.processors.ViewAttributeParser")
                    appendLine("import com.dynamic.utils.processors.ViewProcessor")
                    appendLine()
                    appendLine("class ${className}ViewAttributeParser : ViewAttributeParser() {")
                    appendLine()
                    appendLine("    override fun getViewType() = \"$viewClassName\"")
                    appendLine()
                    appendLine("    private val attributeMap = mapOf<String, (View, Any) -> Unit>(")
                    appendLine("        $attributeMappings")
                    appendLine("    )")
                    appendLine()
                    appendLine("    override fun addAttributes() {")
                    appendLine("        attributeMap.forEach { (attr, setter) -> registerAttribute<View, Any>(attr, setter) }")
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
        val viewAnnotation =
            clazz.annotations.find { it.shortName.asString() == "AutoViewAttributes" }

        val viewClassNameValue =
            viewAnnotation?.arguments?.find { it.name?.asString() == "viewClassName" }?.value as? String
                ?: clazz.qualifiedName?.asString() ?: ""

        logger.info("View class name value for ${clazz.simpleName.asString()}: $viewClassNameValue")
        return viewClassNameValue
    }

    private fun generateAttributeMappings(clazz: KSClassDeclaration): String {
        val attributeMappings = clazz.getAllFunctions().filter {
            it.simpleName.asString()
                .startsWith("set") && it.annotations.any { it.shortName.asString() == "AutoAttribute" }
        }.mapNotNull { function ->
            val attrName = function.simpleName.asString().removePrefix("set")
                .replaceFirstChar { it.lowercase() }

            //what if the type isn't primitive and it needs an import (handle that)
            val paramType =
                function.parameters.firstOrNull()?.type?.resolve()?.declaration?.simpleName?.asString()

            if (paramType == null) {
                logger.warn("Parameter type is null for function ${function.simpleName.asString()} in class ${clazz.simpleName.asString()}. Skipping attribute mapping.")
                return@mapNotNull null
            }

            "\"$attrName\" to { v, value -> (v as ${clazz.simpleName.asString()}).${function.simpleName.asString()}(value as $paramType) }"
        }.toList() // Convert to list before joining

        logger.info("Attribute mappings for ${clazz.simpleName.asString()}: ${attributeMappings.size} attributes found")

        return attributeMappings.joinToString(",\n        ")
    }
}
