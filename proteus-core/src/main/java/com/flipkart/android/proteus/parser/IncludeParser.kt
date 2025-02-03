package com.flipkart.android.proteus.parser

import android.view.View
import android.view.ViewGroup
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * Kotlin class for parsing `<include>` layout tags in Proteus.
 *
 * This class extends [ViewTypeParser] and is responsible for handling `<include>` tags
 * within Proteus layouts. It inflates the included layout and merges it into the current view hierarchy.
 *
 * @param V The View type that will be created (typically ViewGroup or any View that can be a parent).
 */
class IncludeParser<V : View> : ViewTypeParser<V>() {

    /**
     * Returns the type name for this parser, which is "include".
     *
     * This type name is used to identify `<include>` tags in layout definitions.
     *
     * @return The string "include".
     */
    override fun getType(): String = "include" // Directly return "include"

    /**
     * Returns the parent type for included layouts, which is "View".
     *
     * This indicates that `<include>` tags can be placed within any View-based layout.
     *
     * @return The string "View".
     */
    override fun getParentType(): String = "View" // Directly return "View"

    /**
     * Creates a [ProteusView] by inflating the layout specified in the `<include>` tag.
     *
     * It retrieves the 'layout' attribute from the include's extras, gets the corresponding [Layout] object
     * from the [ProteusContext], merges the include's attributes, and inflates the resulting layout.
     *
     * @param context   The ProteusContext for inflation.
     * @param include   The Layout representing the `<include>` tag.
     * @param data      The data [ObjectValue] for data binding.
     * @param parent    The optional parent [ViewGroup].
     * @param dataIndex The index for data binding if it's within a list/array.
     * @return The inflated [ProteusView] representing the included layout.
     * @throws IllegalArgumentException  if the 'layout' attribute is missing in the `<include>` tag.
     * @throws ProteusInflateException if the 'layout' attribute is not a string or if the layout is not found.
     */
    override fun createView(
        context: ProteusContext,
        include: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView {

        val extras = include.extras
            ?: throw IllegalArgumentException("required attribute 'layout' missing.") // Throw exception if extras is null (layout attribute missing)

        val typeValue = extras[ProteusConstants.LAYOUT] // Get the layout attribute Value
            ?: throw ProteusInflateException("required attribute 'layout' missing or is not a string") // Throw exception if layout attribute is missing or null

        if (!typeValue.isPrimitive) { // Check if layout attribute is a Primitive (String)
            throw ProteusInflateException("required attribute 'layout' missing or is not a string") // Throw exception if layout attribute is not primitive
        }

        val layoutName = typeValue.asString() // Get layout name as string
        val layoutToInflate = context.getLayout(layoutName) // Get Layout object from ProteusContext
            ?: throw ProteusInflateException("Layout '$typeValue' not found") // Throw exception if layout not found

        return context.getInflater().inflate(
            layoutToInflate.merge(include), data, parent, dataIndex
        ) // Inflate the merged layout and return ProteusView
    }

    /**
     * Adds attribute processors specific to `<include>` tags (currently none).
     *
     * In this implementation, `<include>` tags do not have any custom attribute processors beyond
     * those inherited from [ViewTypeParser]. Subclasses can override this method to add custom processors if needed.
     */
    override fun addAttributeProcessors() {
        // No specific attribute processors for include tag in this implementation
    }
}