package com.runtimexml.utils.processors

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

open class AttributeProcessor() : SymbolProcessor {

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
        private val processors = BaseViewAttributes.processors

        @JvmStatic
        fun attributeRegistry(block: AttributeProcessor.() -> Unit) =
            AttributeProcessor().apply(block)

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> addAttribute(attribute: String, processor: AttributeProcessorRegistry<T>) {
            processors.add(attribute to (processor as AttributeProcessorRegistry<Any>))
        }

        @JvmStatic
        fun <T> addAttributes(attributes: Map<String, AttributeProcessorRegistry<T>>) {
            attributes.forEach { (attr, value) ->
                addAttribute(attr, value)
            }
        }

        @JvmStatic
        fun <T> addAttributes(vararg attributes: String, processor: AttributeProcessorRegistry<T>) {
            attributes.forEach { addAttribute(it, processor) }
        }

        @JvmStatic
        fun <T> applyAttribute(view: View, attrName: String, value: T?) {
            if (processors.isEmpty) throw IllegalArgumentException("No attributes found")
            if (!processors.any { it.first != attrName }) throw IllegalArgumentException("Attribute not found: $attrName")

            processors.find { it.first == attrName }?.second?.apply(view, value)
        }

        @JvmStatic
        fun <T> applyAttributes(view: View, attrs: Map<String, T?>) {
            if (processors.isEmpty) throw IllegalArgumentException("No attributes found")

            attrs.forEach { (attr, value) ->
                if (!processors.any { it.first == attr }) throw IllegalArgumentException("Attribute not found: $attr")

                processors.find { it.first == attr }?.second?.apply(view, value)
            }
        }
    }
}
