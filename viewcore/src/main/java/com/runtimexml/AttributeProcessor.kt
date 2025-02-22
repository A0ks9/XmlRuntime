package com.runtimexml

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.runtimexml.annotations.AutoViewAttributes

class AttributeProcessor(
    private val codeGenerator: CodeGenerator, private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val autoViewAttributeSymbols =
            resolver.getSymbolsWithAnnotation(AutoViewAttributes::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        if (autoViewAttributeSymbols.none()) {
            logger.info("No classes found with @AutoViewAttributes annotation.")
            return emptyList()
        }

        autoViewAttributeSymbols.forEach { clazz ->
            generateViewAttributeParser(clazz)
        }

        return emptyList()
    }

    private fun generateViewAttributeParser(clazz: KSClassDeclaration) {
        val className = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()
        val generatedPackageName = "$packageName.generated"

        logger.info("Generating ViewAttributeParser for $className")

        val viewClassNameValue = getViewClassName(clazz)
        val attributeMappings = generateAttributeMappings(clazz)

        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = generatedPackageName,
                fileName = "${className}ViewAttributeParser"
            ).use { outputStream ->
                outputStream.bufferedWriter().use { writer ->
                    val fileContent = buildString {
                        appendLine("package $generatedPackageName")
                        appendLine("")
                        appendLine("import android.view.View")
                        appendLine("import com.runtimexml.utils.processors.ViewAttributeParser")
                        appendLine("import com.runtimexml.utils.processors.ViewProcessor")
                        appendLine("import $packageName.${clazz.simpleName.asString()}")
                        appendLine("")
                        appendLine("class ${className}ViewAttributeParser : ViewAttributeParser() {")
                        appendLine("    ")
                        appendLine("    override fun getViewType(): String = \"$viewClassNameValue\"")
                        appendLine("    ")
                        appendLine("    private val attributeMap = mapOf<String, (View, Any) -> Unit>(")
                        appendLine("        $attributeMappings")
                        appendLine("    )")
                        appendLine("    ")
                        appendLine("    override fun addAttributes() {")
                        appendLine("        attributeMap.forEach { (attrName, setter) ->")
                        appendLine("            registerAttribute<View, Any>(attrName, setter)")
                        appendLine("        }")
                        appendLine("    }")
                        appendLine("    ")
                        appendLine("    init {")
                        appendLine("        ViewProcessor.registerViewAttributeParser(")
                        appendLine("            \"$generatedPackageName.${className}ViewAttributeParser\"")
                        appendLine("        )")
                        appendLine("    }")
                        appendLine("}")
                    }
                    writer.write(fileContent)
                }
            }
            logger.info("Successfully generated ViewAttributeParser for $className")

        } catch (e: Exception) {
            logger.error("Error generating ViewAttributeParser for $className: ${e.message}")
        }
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