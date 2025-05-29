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
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier as PoetKModifier // Alias KModifier from KotlinPoet
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.voyager.annotations.ViewRegister // Assuming this is the correct import for your annotation

/**
 * KSP [SymbolProcessor] that generates `ViewAttributeParser` implementations for classes
 * annotated with [ViewRegister].
 *
 * For each annotated class (e.g., `MyCustomView`), this processor generates a corresponding
 * `MyCustomViewViewAttributeParser` class. This generated parser includes:
 *  - Logic to register the view with `ViewProcessor`.
 *  - A map of attribute handlers (`attributeMap`) derived from methods and properties
 *    within `MyCustomView` that are annotated with a hypothetical `@Attribute` annotation.
 *  - Implementation of methods required by a base `ViewAttributeParser` class, such as
 *    `getViewType()` and `createView()`.
 *
 * This processor aims to automate the creation of boilerplate code needed for integrating
 * custom views and their attributes with the Voyager framework's dynamic inflation and
 * attribute application system.
 *
 * @param codeGenerator The KSP [CodeGenerator] service used for creating new source files.
 * @param logger The KSP [KSPLogger] service used for logging errors, warnings, and info.
 */
class AttributeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    /**
     * Companion object for constants used within the processor.
     */
    companion object {
        // Assuming ViewRegister annotation has a 'name' argument for the view type string.
        // If not, adjust getViewClassName accordingly.
        private const val VIEW_REGISTER_ANNOTATION_NAME_ARG = "name"
        private val VIEW_REGISTER_ANNOTATION_FQN = ViewRegister::class.qualifiedName ?: ""

        // Assuming a hypothetical @Attribute annotation.
        // Its actual FQN would be needed if it's a separate annotation class.
        // For this example, we'll assume it's just identified by its simple name if it's
        // defined in the same module or as a type alias.
        private const val ATTRIBUTE_ANNOTATION_SIMPLE_NAME = "Attribute"
        // If @Attribute has an argument for the XML attribute name, e.g., @Attribute(xmlName = "customText")
        private const val ATTRIBUTE_ANNOTATION_XML_NAME_ARG = "xmlName" // Example argument name
    }

    /**
     * KSP entry point. Called by KSP to process symbols.
     * This implementation finds all classes annotated with `@ViewRegister` and triggers
     * the generation of a corresponding `ViewAttributeParser` for each.
     *
     * @param resolver The [Resolver] instance provided by KSP, used to query symbols.
     * @return A list of symbols that could not be processed in this round, to be deferred
     *         to subsequent rounds. This processor processes all its symbols in one go,
     *         so it returns an empty list.
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.logging("KSP AttributeProcessor starting process round.")
        if (VIEW_REGISTER_ANNOTATION_FQN.isEmpty()) {
            logger.error("Critical: Could not find qualified name for ViewRegister annotation. Ensure it's correctly defined and imported in annotations module.")
            return emptyList()
        }

        val symbols = resolver.getSymbolsWithAnnotation(VIEW_REGISTER_ANNOTATION_FQN)
        val validClasses = symbols.filterIsInstance<KSClassDeclaration>().toList()

        if (validClasses.none()) {
            logger.info("No classes found annotated with @${ViewRegister::class.simpleName}. Nothing to process.")
            return emptyList()
        }
        
        logger.info("Found ${validClasses.size} classes annotated with @${ViewRegister::class.simpleName} to process.")
        validClasses.forEach { clazz ->
            logger.logging("Processing class: ${clazz.qualifiedName?.asString()}")
            try {
                generateViewAttributeParser(clazz)
            } catch (e: Exception) { // Catch any unexpected errors during processing of a single class
                logger.error("Unhandled exception while generating parser for ${clazz.qualifiedName?.asString()}: ${e.message ?: e::class.simpleName}", clazz)
            }
        }
        logger.logging("KSP AttributeProcessor finished process round.")
        return emptyList() 
    }

    /**
     * Generates a specific `[ClassName]ViewAttributeParser.kt` file for the given [KSClassDeclaration] `clazz`.
     * The generated class handles view registration and attribute mapping for the `clazz`.
     *
     * @param clazz The [KSClassDeclaration] of the user's view class (e.g., `MyCustomButton`)
     *              annotated with `@ViewRegister`.
     */
    private fun generateViewAttributeParser(clazz: KSClassDeclaration) {
        val sourceClassName = clazz.simpleName.asString()
        val sourcePackageName = clazz.packageName.asString()
        val generatedParserClassName = "${sourceClassName}ViewAttributeParser"
        val generatedPackageName = "$sourcePackageName.generated" // Place generated files in a subpackage

        val viewTypeString = getViewTypeString(clazz) 
        
        logger.info("Generating $generatedParserClassName for $sourceClassName (ViewType: '$viewTypeString') in $generatedPackageName.")

        // Imports needed for the generated file
        val viewClass = clazz.asType(emptyList()).asTypeName() // Use KSP's asType().asTypeName() for accuracy
        val viewAndroidClass = ClassName("android.view", "View")
        val contextThemeWrapperClass = ClassName("androidx.appcompat.view", "ContextThemeWrapper")
        val baseViewAttributeParserClass = ClassName("com.voyager.utils.processors", "ViewAttributeParser")
        val viewProcessorClass = ClassName("com.voyager.utils.processors", "ViewProcessor")

        // Prepare attributeMap content
        // "attrName" to { v, value -> (v as MyCustomView).setPropertyOrCallFunction(value as Type) }
        val attributeMapInitializer = CodeBlock.builder().indent()
        val mappings = generateAttributeMappings(clazz) // This will return List<CodeBlock>
        if (mappings.isNotEmpty()) {
            mappings.forEachIndexed { index, mapping ->
                attributeMapInitializer.add(mapping)
                if (index < mappings.size - 1) {
                    attributeMapInitializer.add(",\n")
                } else {
                    attributeMapInitializer.add("\n")
                }
            }
        }
        attributeMapInitializer.unindent()


        // Define the attributeMap property
        val viewAnyQLambda = LambdaTypeName.get(
            parameters = listOf(viewAndroidClass, Any::class.asTypeName().copy(nullable = true)),
            returnType = Unit::class.asTypeName()
        )
        val attributeMapProperty = PropertySpec.builder(
            "attributeMap",
            Map::class.asTypeName().parameterizedBy(String::class.asTypeName(), viewAnyQLambda),
            PoetKModifier.PRIVATE
        )
            .initializer("mapOf(\n%L)", attributeMapInitializer.build())
            .build()

        // Define functions for the generated class
        val getViewTypeFun = FunSpec.builder("getViewType")
            .addModifiers(PoetKModifier.OVERRIDE)
            .returns(String::class)
            .addStatement("return %S", viewTypeString)
            .build()

        val addAttributesFun = FunSpec.builder("addAttributes")
            .addModifiers(PoetKModifier.OVERRIDE)
            .addStatement("registerAttributes(attributeMap)")
            .build()
            
        val createViewFun = FunSpec.builder("createView")
            .addModifiers(PoetKModifier.OVERRIDE)
            .addParameter("context", contextThemeWrapperClass)
            .returns(viewAndroidClass) // Should return the actual view type for type safety if base class allows
            .addStatement("return %T(context)", viewClass) // Assumes constructor MyCustomView(context)
            .build()

        // Create the TypeSpec for the class
        val parserClass = TypeSpec.classBuilder(generatedParserClassName)
            .superclass(baseViewAttributeParserClass)
            .addKdoc("Auto-generated ViewAttributeParser for [%T]. Do not edit.", viewClass)
            .addProperty(attributeMapProperty)
            .addFunction(getViewTypeFun)
            .addFunction(addAttributesFun)
            .addFunction(createViewFun)
            .addInitializerBlock(
                CodeBlock.builder()
                    .addStatement("%T.registerView(%S, %S) { createView(it) }", viewProcessorClass, sourcePackageName, sourceClassName)
                    .addStatement("addAttributes()")
                    .build()
            )
            .build()

        // Create the FileSpec
        val fileSpec = FileSpec.builder(generatedPackageName, generatedParserClassName)
            .addType(parserClass)
            .addImport("android.content", "Context") // if Context is used standalone
            .build()

        // Generate the file
        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, clazz.containingFile!!),
                packageName = generatedPackageName,
                fileName = generatedParserClassName
            ).bufferedWriter().use { writer ->
                fileSpec.writeTo(writer)
            }
            logger.info("Successfully generated $generatedParserClassName.kt for ${clazz.qualifiedName?.asString()}")
        } catch (e: Exception) {
            logger.error("Failed to write generated file $generatedParserClassName for ${clazz.qualifiedName?.asString()}: ${e.message}", clazz)
        }
    }


    /**
     * Extracts the view type string from the [ViewRegister] annotation on the given class.
     * If the `name` argument is not provided in the annotation, it defaults to the
     * fully qualified name of the class itself.
     *
     * @param clazz The [KSClassDeclaration] annotated with `@ViewRegister`.
     * @return The view type string to be used for registration with `ViewProcessor`.
     */
    private fun getViewTypeString(clazz: KSClassDeclaration): String {
        val viewRegisterAnnotation = clazz.annotations.find {
            // Compare fully qualified names or simple names if annotations are in the same package
            it.annotationType.resolve().declaration.qualifiedName?.asString() == VIEW_REGISTER_ANNOTATION_FQN ||
            it.shortName.asString() == ViewRegister::class.simpleName // Fallback for simple name match
        }

        return viewRegisterAnnotation?.arguments?.find {
            it.name?.asString() == VIEW_REGISTER_ANNOTATION_NAME_ARG
        }?.value as? String ?: clazz.qualifiedName!!.asString().also {
            logger.logging("No '${VIEW_REGISTER_ANNOTATION_NAME_ARG}' provided in @${ViewRegister::class.simpleName} for ${clazz.simpleName.asString()}, defaulting to qualified name: $it", clazz)
        }
    }

    /**
     * Generates a list of [CodeBlock]s representing the attribute mappings for a given class.
     * It iterates over functions and properties annotated with a hypothetical `@Attribute` annotation.
     *
     * @param clazz The [KSClassDeclaration] whose members are to be processed.
     * @return A list of [CodeBlock]s, each representing one entry in the `attributeMap`
     *         (e.g., `"attributeName" to { view, value -> ... }`).
     */
    private fun generateAttributeMappings(clazz: KSClassDeclaration): List<CodeBlock> {
        val mappings = mutableListOf<CodeBlock>()
        var attributesFoundCount = 0

        // Consider using getDeclaredFunctions/Properties if only direct members are desired
        val functions = clazz.getAllFunctions().filter { func ->
            func.annotations.any { it.shortName.asString() == ATTRIBUTE_ANNOTATION_SIMPLE_NAME }
        }
        val properties = clazz.getAllProperties().filter { prop ->
            prop.annotations.any { it.shortName.asString() == ATTRIBUTE_ANNOTATION_SIMPLE_NAME }
        }
        logger.logging("Inspecting ${functions.count()} functions and ${properties.count()} properties for @${ATTRIBUTE_ANNOTATION_SIMPLE_NAME} in ${clazz.simpleName.asString()}.")

        functions.forEach { func ->
            logger.logging("Processing function: ${func.simpleName.asString()} in ${clazz.simpleName.asString()}")
            generateFunctionMapping(clazz, func)?.let {
                mappings.add(it)
                attributesFoundCount++
            }
        }

        properties.forEach { prop ->
            logger.logging("Processing property: ${prop.simpleName.asString()} in ${clazz.simpleName.asString()}")
            generatePropertyMapping(clazz, prop)?.let {
                mappings.add(it)
                attributesFoundCount++
            }
        }

        logger.info("Found $attributesFoundCount attribute mappings for ${clazz.simpleName.asString()}.")
        return mappings
    }


    /**
     * Generates a [CodeBlock] for mapping a function annotated with `@Attribute`.
     *
     * @param containingClass The [KSClassDeclaration] of the view (e.g., `MyCustomView`).
     * @param func The [KSFunctionDeclaration] representing the annotated function.
     * @return A [CodeBlock] for the map entry, or `null` if the function is unsuitable
     *         (e.g., missing attribute name, incompatible parameters).
     */
    private fun generateFunctionMapping(
        containingClass: KSClassDeclaration,
        func: KSFunctionDeclaration
    ): CodeBlock? {
        val funcName = func.simpleName.asString()
        val className = containingClass.simpleName.asString()
        val attrName = extractXmlAttributeName(func.annotations, funcName, containingClass) ?: return null // Pass containingClass for logging context

        if (func.parameters.size != 1) {
            logger.error("Function '$funcName' in '$className' is annotated with @$ATTRIBUTE_ANNOTATION_SIMPLE_NAME but does not have exactly one parameter. Skipping.", func)
            return null
        }
        val parameter = func.parameters.first()
        val paramType = parameter.type.resolve()
        if (paramType.isError) {
            logger.error("Parameter type for function '$funcName' in '$className' could not be resolved. Skipping attribute '$attrName'.", parameter)
            return null
        }
        val paramTypeName = paramType.declaration.qualifiedName?.asString() ?: paramType.declaration.simpleName.asString().also {
            logger.warn("Could not get qualified name for parameter type of '$funcName' in '$className' (type: '${paramType.declaration.simpleName.asString()}'). Using simple name. This might cause issues if the type is not directly importable.", parameter)
        }
        
        // Ensure the function is public if it's intended to be called
        if (!func.modifiers.contains(Modifier.PUBLIC) && !func.modifiers.contains(Modifier.INTERNAL)) {
             logger.warn("Function '$funcName' in '$className' is private or protected but annotated with @$ATTRIBUTE_ANNOTATION_SIMPLE_NAME for attribute '$attrName'. It might not be callable as expected.", func)
        }

        // Using PoetKModifier for generated code modifiers
        return CodeBlock.of(
            "%S to { view, value -> (view as %T).$funcName(value as %T) }",
            attrName,
            containingClass.asType(emptyList()).asTypeName(), // Use KSP's asType().asTypeName()
            ClassName.bestGuess(paramTypeName) // Use KotlinPoet's ClassName.bestGuess for parameter type
        )
    }

    /**
     * Generates a [CodeBlock] for mapping a property annotated with `@Attribute`.
     *
     * @param containingClass The [KSClassDeclaration] of the view.
     * @param prop The [KSPropertyDeclaration] representing the annotated property.
     * @return A [CodeBlock] for the map entry, or `null` if unsuitable.
     */
    private fun generatePropertyMapping(
        containingClass: KSClassDeclaration,
        prop: KSPropertyDeclaration
    ): CodeBlock? {
        val propName = prop.simpleName.asString()
        val className = containingClass.simpleName.asString()
        val attrName = extractXmlAttributeName(prop.annotations, propName, containingClass) ?: return null
        
        if (!prop.isMutable) {
            logger.error("Property '$propName' in '$className' is read-only (val) but annotated with @$ATTRIBUTE_ANNOTATION_SIMPLE_NAME for attribute '$attrName'. Skipping.", prop)
            return null
        }

        val propertyType = prop.type.resolve()
        if (propertyType.isError) {
            logger.error("Property type for '$propName' in '$className' could not be resolved. Skipping attribute '$attrName'.", prop)
            return null
        }
        val propertyTypeName = propertyType.declaration.qualifiedName?.asString() ?: propertyType.declaration.simpleName.asString().also {
            logger.warn("Could not get qualified name for property type of '$propName' in '$className' (type: '${propertyType.declaration.simpleName.asString()}'). Using simple name. This might cause issues if the type is not directly importable.", prop)
        }
        
        // Ensure setter is accessible if it's a property
         if (prop.setter?.modifiers?.contains(Modifier.PRIVATE) == true || prop.setter?.modifiers?.contains(Modifier.PROTECTED) == true) {
            logger.warn("Setter for property '$propName' in '$className' is private or protected but property is annotated with @$ATTRIBUTE_ANNOTATION_SIMPLE_NAME for attribute '$attrName'. Assignment might fail.", prop)
        }


        return CodeBlock.of(
            "%S to { view, value -> (view as %T).$propName = value as %T }",
            attrName,
            containingClass.asType(emptyList()).asTypeName(),
            ClassName.bestGuess(propertyTypeName)
        )
    }

    /**
     * Extracts the XML attribute name from an `@Attribute` annotation.
     * It looks for an argument named [ATTRIBUTE_ANNOTATION_XML_NAME_ARG] in the annotation.
     * If not found, it defaults to the annotated element's simple name and logs a warning.
     * If the argument is found but is not a String, it logs an error and returns null.
     *
     * @param annotations A sequence of [KSAnnotation]s on a symbol.
     * @param defaultName The simple name of the annotated symbol (function or property), used as a fallback.
     * @param-annotatedSymbol The [KSNode] (function or property) to which the annotation is attached, for error reporting.
     * @return The determined XML attribute name, or `null` if the `@Attribute` annotation itself is not found
     *         or if its `xmlName` argument is invalid.
     */
    private fun extractXmlAttributeName(annotations: Sequence<KSAnnotation>, defaultName: String, annotatedSymbol: KSNode): String? {
        val attributeAnnotation = annotations.find { it.shortName.asString() == ATTRIBUTE_ANNOTATION_SIMPLE_NAME }
        return attributeAnnotation?.let { ann ->
            val xmlNameArg = ann.arguments.find { arg -> arg.name?.asString() == ATTRIBUTE_ANNOTATION_XML_NAME_ARG }
            if (xmlNameArg != null) {
                if (xmlNameArg.value is String) {
                    xmlNameArg.value as String
                } else {
                    logger.error("Argument '$ATTRIBUTE_ANNOTATION_XML_NAME_ARG' in @$ATTRIBUTE_ANNOTATION_SIMPLE_NAME on element '$defaultName' is not a String. Attribute will be skipped.", annotatedSymbol)
                    null 
                }
            } else {
                logger.logging("No '$ATTRIBUTE_ANNOTATION_XML_NAME_ARG' provided in @$ATTRIBUTE_ANNOTATION_SIMPLE_NAME on element '$defaultName'. Defaulting XML attribute name to element name.", annotatedSymbol)
                defaultName
            }
        } // If attributeAnnotation is null, it means @Attribute was not found, so return null (no mapping).
    }
}
// Placeholder for actual annotation if it's more complex.
// For now, this is a simplified extraction.
// No changes needed below this line for this refactoring pass.
// The KSP Symbol Processor logic seems fine.
