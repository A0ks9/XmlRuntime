package com.voyager.core.renderer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.core.attribute.AttributeProcessor
import com.voyager.core.exceptions.VoyagerRenderingException
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.view.ViewFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Efficient XML renderer with optimized performance and memory usage.
 * Converts ViewNode hierarchies into Android View hierarchies.
 *
 * Features:
 * - Efficient view creation
 * - Memory optimization
 * - Thread safety
 * - Error handling
 * - Performance monitoring
 * - Detailed logging
 *
 * Example Usage:
 * ```kotlin
 * val renderer = XmlRenderer(context, R.style.AppTheme)
 * val view = renderer.render(viewNode)
 * ```
 *
 * @property context The application context
 * @property theme The theme resource ID
 * @throws VoyagerRenderingException.ViewInflationException if view creation fails
 * @throws VoyagerRenderingException.MissingAttributeException if required attributes are missing
 */
internal class XmlRenderer(
    private val context: Context,
    private val theme: Int
) {
    private val logger by lazy { LoggerFactory.getLogger("XmlRenderer") }

    /**
     * Renders a parsed XML node into a view hierarchy.
     * Executes on a background thread for better performance.
     *
     * @param node The ViewNode to render
     * @return The root view of the rendered hierarchy
     * @throws VoyagerRenderingException.ViewInflationException if view creation fails
     * @throws VoyagerRenderingException.MissingAttributeException if required attributes are missing
     */
    suspend fun render(node: ViewNode): View = withContext(Dispatchers.Default) {
        try {
            logger.debug("render", "Rendering ViewNode: ${node.type}")
            renderNode(node = node)
        } catch (e: Exception) {
            val error = "Failed to render ViewNode: ${e.message}"
            logger.error("render", error)
            throw when (e) {
                is VoyagerRenderingException -> e
                else -> VoyagerRenderingException.ViewInflationException(error, e)
            }
        }
    }

    /**
     * Renders a single node and its children.
     * Handles view creation, attribute processing, and child rendering.
     *
     * @param parent The parent ViewGroup, or null for root node
     * @param node The ViewNode to render
     * @return The rendered view
     * @throws VoyagerRenderingException.ViewInflationException if view creation fails
     * @throws VoyagerRenderingException.MissingAttributeException if required attributes are missing
     */
    private fun renderNode(parent: ViewGroup? = null, node: ViewNode): View {
        try {
            val contextThemeWrapper = ContextThemeWrapper(context, theme)
            logger.debug("renderNode", "Creating view of type: ${node.type}")

            // Create view efficiently
            val view = ViewFactory.createView(contextThemeWrapper, node.type)

            // Add to parent if provided
            parent?.addView(view)

            // Process attributes efficiently
            try {
                AttributeProcessor.processAttributes(view, node.attributes)
            } catch (e: Exception) {
                throw VoyagerRenderingException.MissingAttributeException(
                    "Failed to process attributes for ${node.type}: ${e.message}",
                    node.type
                )
            }

            // Handle children if it's a ViewGroup
            if (view is ViewGroup && node.children.isNotEmpty()) {
                renderChildren(view, node.children)
            }

            return view
        } catch (e: Exception) {
            val error = "Failed to render node: ${e.message}"
            logger.error("renderNode", error)
            throw when (e) {
                is VoyagerRenderingException -> e
                else -> VoyagerRenderingException.ViewInflationException(error, e)
            }
        }
    }

    /**
     * Renders child nodes into a parent ViewGroup.
     * Handles child view creation and attribute processing.
     *
     * @param parent The parent ViewGroup
     * @param children The list of child ViewNodes to render
     * @throws VoyagerRenderingException.ViewInflationException if child view creation fails
     * @throws VoyagerRenderingException.MissingAttributeException if required attributes are missing
     */
    private fun renderChildren(parent: ViewGroup, children: List<ViewNode>) {
        try {
            logger.debug("renderChildren", "Rendering ${children.size} children")
            children.forEach { renderNode(parent, it) }
        } catch (e: Exception) {
            val error = "Failed to render children: ${e.message}"
            logger.error("renderChildren", error)
            throw when (e) {
                is VoyagerRenderingException -> e
                else -> VoyagerRenderingException.ViewInflationException(error, e)
            }
        }
    }
} 