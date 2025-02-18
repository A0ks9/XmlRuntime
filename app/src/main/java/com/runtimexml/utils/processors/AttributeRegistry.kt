package com.runtimexml.utils.processors

import android.util.Log
import android.view.View
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.runtimexml.utils.BaseViewAttributes
import com.runtimexml.utils.annotations.AutoAttribute
import com.runtimexml.utils.interfaces.AttributeProcessorRegistry

open class AttributeRegistry() : SymbolProcessor {

    private lateinit var codeGenerator: CodeGenerator
    private lateinit var logger: KSPLogger

    constructor(codeGenerator: CodeGenerator, logger: KSPLogger) : this() {
        this.codeGenerator = codeGenerator
        this.logger = logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoAttribute::class.qualifiedName!!)
        if (symbols.none()) return emptyList()

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            generateAttributeHandler(clazz)
        }
        return emptyList()
    }

    private fun generateAttributeHandler(clazz: KSClassDeclaration) {
        logger.info("Processing CustomView: ${clazz.simpleName.asString()}")
        val viewMappings = StringBuilder()
        val className = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()
        val attrs = findSetters(clazz)

        val file = codeGenerator.createNewFile(
            Dependencies(false, clazz.containingFile!!),
            "$packageName.generated",
            "${className}AttributeMapper"
        )

        viewMappings.append("\"$className\" to mapOf(\n")
        attrs.forEach { (attr, setter) ->
            viewMappings.append(
                "    \"$attr\" to { v, value -> (v as $className).$setter(value as ${getType(attr)}) },\n"
            )
        }
        viewMappings.append("),\n")

        // Write the generated code to a file
        file.bufferedWriter().use { writer ->
            writer.write(
                """
                package $packageName.generated
                
                import android.view.View
                import ${clazz.packageName.asString()}.${clazz.simpleName.asString()}

                object ${className}AttributeMapper {
                    private val attributeMap = mapOf<String, Map<String, (View, Any) -> Unit>>(
                        $viewMappings
                    )

                    fun applyAttribute(view: View, attrName: String, value: Any) {
                        val className = view::class.simpleName
                        val setter = attributeMap[className]?.get(attrName)

                        setter?.invoke(view, value)
                    }
                }
                """.trimIndent()
            )
        }
    }

    private fun findSetters(clazz: KSClassDeclaration): Map<String, String> {
        val setters = mutableMapOf<String, String>()

        clazz.getAllFunctions().filter { it.simpleName.asString().startsWith("set") }
            .forEach { function ->
                val attrName = function.simpleName.asString().removePrefix("set")
                    .replaceFirstChar { it.lowercase() }
                setters[attrName] = function.simpleName.asString()
            }
        return setters
    }

    private fun getType(attr: String): String {
        return when (attr) {
            "text", "hint" -> "CharSequence"
            "textSize", "lineSpacingExtra", "lineSpacingMultiplier", "letterSpacing" -> "Float"
            "maxLines", "minLines" -> "Int"

            "textColor", "textColorLink", "textColorHighlight", "textColorHint" -> "Int"
            "backgroundColor", "backgroundTint", "colorAccent", "colorPrimary" -> "Int"
            "cardBackgroundColor" -> "Int"

            "padding", "paddingLeft", "paddingTop", "paddingRight", "paddingBottom", "layout_margin", "layout_marginLeft", "layout_marginTop", "layout_marginRight", "layout_marginBottom" -> "Float"
            "layout_width", "layout_height" -> "String"

            "visibility", "checked", "enabled", "clickable", "focusable", "focusableInTouchMode" -> "Boolean"

            "src", "imageResource", "imageTint", "drawable" -> "Int"
            "scaleType" -> "ImageView.ScaleType"

            "textAllCaps" -> "Boolean"
            "gravity", "textAlignment" -> "Int"

            "textStyle" -> "Int" // e.g., Typeface.BOLD
            "fontFamily", "typeface" -> "String"

            "icon", "iconTint" -> "Int"
            "iconGravity", "iconPadding", "iconSize" -> "Float"

            "inputType", "imeOptions", "maxLength", "maxEms" -> "Int"
            "autoLink" -> "Boolean"
            "singleLine", "lines", "minEms" -> "Int"

            "switchMinWidth", "switchPadding" -> "Int"
            "switchTextAppearance", "switchTextColor" -> "Int"
            "switchThumbTint", "switchTrackTint" -> "Int"

            "indeterminate", "progress", "secondaryProgress", "max" -> "Int"
            "progressDrawable" -> "Int"

            "buttonTint" -> "Int"

            "title", "subtitle" -> "CharSequence"
            "popupTheme", "contentInsetStart", "contentInsetEnd" -> "Int"

            "elevation", "cornerRadius" -> "Float"
            "strokeWidth", "strokeColor" -> "Int"

            "cardCornerRadius", "cardElevation", "cardMaxElevation" -> "Float"
            "cardUseCompatPadding" -> "Boolean"

            "layoutManager" -> "String"
            "adapter" -> "RecyclerView.Adapter<*>"

            "onClick", "onTouch", "onFocusChange" -> "String"

            "drawableLeft", "drawableTop", "drawableRight", "drawableBottom" -> "Int"
            "shapeAppearance" -> "String"

            "rating", "numStars" -> "Float"

            "textAppearance" -> "String"

            "layout_gravity", "layout_alignParentStart", "layout_alignParentEnd" -> "String"

            // Default fallback
            else -> "Any"
        }
    }

    companion object {
        private val attributeProcessors = BaseViewAttributes.AttributeProcessors

        /**
         * Configures an [AttributeRegistry] using the provided configuration block.
         *
         * @param processorConfig Configuration block for the [AttributeRegistry].
         * @return The configured [AttributeRegistry].
         */
        @JvmStatic
        fun configureProcessor(processorConfig: AttributeRegistry.() -> Unit): AttributeRegistry {
            return AttributeRegistry().apply(processorConfig)
        }

        /**
         * Adds a single attribute and its associated processor to the registry.
         *
         * @param attributeName The name of the attribute.
         * @param attributeProcessor The processor responsible for applying the attribute.
         * @throws IllegalArgumentException if an attribute with the same name already exists.
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <V : View, T> registerAttribute(
            attributeName: String, attributeProcessor: AttributeProcessorRegistry<V, T>
        ) {
            require(attributeProcessors.none { it.first == attributeName }) {
                "Attribute with name '$attributeName' already registered."
            }
            attributeProcessors.add(attributeName to attributeProcessor as AttributeProcessorRegistry<*, Any>)
        }

        /**
         * Adds multiple attributes and their associated processors to the registry.
         *
         * @param attributeMap A map of attribute names to their processors.
         * @throws IllegalArgumentException if any attribute name is already registered.
         */
        @JvmStatic
        fun <V : View, T> registerAttributes(attributeMap: Map<String, AttributeProcessorRegistry<V, T>>) {
            attributeMap.forEach { (attr, processor) ->
                registerAttribute(attr, processor)
            }
        }

        /**
         * Adds multiple attributes with the same processor to the registry.
         *
         * @param attributeNames A vararg of attribute names.
         * @param attributeProcessor The processor responsible for applying the attributes.
         * @throws IllegalArgumentException if any attribute name is already registered.
         */
        @JvmStatic
        fun <V : View, T> registerAttributes(
            vararg attributeNames: String, attributeProcessor: AttributeProcessorRegistry<V, T>
        ) {
            attributeNames.forEach { registerAttribute(it, attributeProcessor) }
        }

        /**
         * Applies a single attribute to the target view.
         *
         * @param targetView The view to apply the attribute to.
         * @param attributeName The name of the attribute.
         * @param attributeValue The value of the attribute.
         * @throws IllegalArgumentException if no attributes are registered or if the attribute is not found.
         * @throws ClassCastException if there is a type mismatch during attribute application.
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <V : View, T> applyAttribute(targetView: V, attributeName: String, attributeValue: T?) {
            require(attributeProcessors.isNotEmpty()) { "No attributes found" }

            val attributeProcessor = attributeProcessors.find { it.first == attributeName }?.second
                ?: throw IllegalArgumentException("Attribute not found: $attributeName")

            try {
                (attributeProcessor as AttributeProcessorRegistry<V, Any>).apply(
                    targetView, attributeValue as Any
                )
            } catch (e: ClassCastException) {
                Log.e(
                    "AttributeProcessor",
                    "Casting error processing $attributeName. Error: ${e.message}"
                )
                throw e
            }
        }

        /**
         * Applies multiple attributes to the target view.
         *
         * @param targetView The view to apply the attributes to.
         * @param attributeValues A map of attribute names to their values.
         * @throws IllegalArgumentException if no attributes are registered.
         */
        @JvmStatic
        fun <V : View, T> applyAttributes(targetView: V, attributeValues: Map<String, T?>) {
            require(attributeProcessors.isNotEmpty()) { "No attributes found" }

            // Apply Non-ConstraintLayout Attributes:
            val nonConstraintAttributes =
                attributeValues.filter { !it.key.isConstraintLayoutAttribute() }
            applyAttributesInternal(targetView, nonConstraintAttributes)

            // Extract ConstraintLayout Attributes:
            val constraintAttributes =
                attributeValues.filter { it.key.isConstraintLayoutAttribute() }

            // Apply ConstraintLayout Constraints (without Bias)
            val constraintOnlyAttributes = constraintAttributes.filter { it.key.isConstraint() }
            applyAttributesInternal(targetView, constraintOnlyAttributes)

            // Apply ConstraintLayout Bias:
            val biasAttributes = constraintAttributes.filter { it.key.isBias() }
            applyAttributesInternal(targetView, biasAttributes)
        }

        /**
         * Internal function to apply a set of attributes to the target view.
         *
         * @param targetView The view to apply the attributes to.
         * @param attributeValues A map of attribute names to their values.
         */
        @Suppress("UNCHECKED_CAST")
        private fun <V : View, T> applyAttributesInternal(
            targetView: V, attributeValues: Map<String, T?>
        ) {
            attributeValues.forEach { (attr, value) ->
                val attributeProcessor = attributeProcessors.find { it.first == attr }?.second
                    ?: throw IllegalArgumentException("Attribute not found: $attr")

                Log.d("AttributeProcessor", "Applying attribute: $attr, value: $value")

                if (value != null) {
                    try {
                        (attributeProcessor as AttributeProcessorRegistry<V, Any>).apply(
                            targetView, value as Any
                        )
                    } catch (e: ClassCastException) {
                        Log.e(
                            "AttributeProcessor",
                            "Casting error processing $attr. Error: ${e.message}"
                        )
                        // Consider rethrowing or handling differently based on your needs
                        // throw e
                    }
                } else {
                    Log.w("AttributeProcessor", "Skipping null value for attribute: $attr")
                }
            }
        }

        /**
         * Checks if an attribute name belongs to ConstraintLayout.
         *
         * @return true if the attribute is a ConstraintLayout attribute, false otherwise.
         */
        private fun String.isConstraintLayoutAttribute(): Boolean {
            return startsWith("layout_constraint")
        }

        /**
         * Checks if an attribute name is a ConstraintLayout constraint (excluding bias).
         *
         * @return true if the attribute is a ConstraintLayout constraint, false otherwise.
         */
        private fun String.isConstraint(): Boolean {
            return isConstraintLayoutAttribute() && !contains("Bias")
        }

        /**
         * Checks if an attribute name is a ConstraintLayout bias.
         *
         * @return true if the attribute is a ConstraintLayout bias, false otherwise.
         */
        private fun String.isBias(): Boolean {
            return isConstraintLayoutAttribute() && contains("Bias")
        }
    }
}