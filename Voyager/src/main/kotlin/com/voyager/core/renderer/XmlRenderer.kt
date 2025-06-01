package com.voyager.core.renderer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.core.attribute.AttributeProcessor
import com.voyager.core.model.ViewNode
import com.voyager.core.view.ViewFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Efficient XML renderer with optimized performance and memory usage.
 */
internal class XmlRenderer(
    private val context: Context,
    private val theme: Int,
) {

    /**
     * Renders a parsed XML node into a view hierarchy.
     */
    suspend fun render(node: ViewNode): View = withContext(Dispatchers.Default) { renderNode(node) }

    private fun renderNode(node: ViewNode): View {
        val contextThemeWrapper = ContextThemeWrapper(context, theme)
        // Create view efficiently
        val view = ViewFactory.createView(contextThemeWrapper, node.type)

        // Process attributes efficiently
        AttributeProcessor.processAttributes(view, node.attributes)

        // Handle children if it's a ViewGroup
        if (view is ViewGroup && node.children.isNotEmpty()) {
            renderChildren(view, node.children)
        }

        return view
    }

    private fun renderChildren(parent: ViewGroup, children: List<ViewNode>) {
        children.forEach { child ->
            val childView = renderNode(child)
            parent.addView(childView)
        }
    }
} 