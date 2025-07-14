package com.voyager.core.compiler

import androidx.collection.ArrayMap
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Optimization rule engine that applies various performance optimizations to layouts.
 * This engine implements sophisticated optimization strategies to improve runtime performance.
 *
 * Optimizations Include:
 * - View hierarchy flattening
 * - Attribute merging and caching
 * - Layout pattern optimization
 * - Resource preloading
 * - Memory usage reduction
 */
class OptimizationRuleEngine {
    private val logger = LoggerFactory.getLogger("OptimizationRuleEngine")

    /**
     * Applies all applicable optimizations to the given ViewNode.
     */
    fun optimize(viewNode: ViewNode, analysisResult: StaticAnalysisResult): ViewNode {
        logger.debug("optimize", "Applying optimizations to layout")

        var optimizedNode = viewNode.copy()

        // Apply each optimization based on analysis results
        analysisResult.appliedOptimizations.forEach { optimization ->
            optimizedNode = when (optimization) {
                OptimizationType.FLATTEN_HIERARCHY -> flattenHierarchy(optimizedNode)
                OptimizationType.VIEW_RECYCLING -> enableViewRecycling(optimizedNode)
                OptimizationType.OPTIMIZE_MATCH_PARENT -> optimizeMatchParent(optimizedNode)
                OptimizationType.USE_RECYCLER_VIEW -> suggestRecyclerView(optimizedNode)
                OptimizationType.REMOVE_UNNECESSARY_WRAPPER -> removeUnnecessaryWrappers(
                    optimizedNode
                )

                OptimizationType.MERGE_ATTRIBUTES -> mergeAttributes(optimizedNode)
                OptimizationType.CACHE_VIEWS -> enableViewCaching(optimizedNode)
                OptimizationType.PRELOAD_RESOURCES -> preloadResources(optimizedNode)
            }
        }

        logger.debug(
            "optimize", "Applied ${analysisResult.appliedOptimizations.size} optimizations"
        )
        return optimizedNode
    }

    /**
     * Flattens deep view hierarchies by removing unnecessary nesting.
     */
    private fun flattenHierarchy(node: ViewNode): ViewNode {
        logger.debug("flattenHierarchy", "Flattening view hierarchy")

        // If this is a wrapper layout with only one child, consider flattening
        if (isWrapperLayout(node) && node.children.size == 1) {
            val child = node.children.first()
            // Merge attributes from parent to child where possible
            val mergedAttributes = mergeCompatibleAttributes(node.attributes, child.attributes)
            return child.copy(attributes = mergedAttributes)
        }

        // Recursively optimize children
        val optimizedChildren = node.children.map { flattenHierarchy(it) }.toMutableList()
        return node.copy(children = optimizedChildren)
    }

    /**
     * Enables view recycling optimizations for improved memory usage.
     */
    private fun enableViewRecycling(node: ViewNode): ViewNode {
        logger.debug("enableViewRecycling", "Enabling view recycling")

        val updatedAttributes = ArrayMap<String, String>(node.attributes)
        updatedAttributes["recycling_enabled"] = "true"
        updatedAttributes["recycling_type"] = node.type

        val optimizedChildren = node.children.map { enableViewRecycling(it) }.toMutableList()
        return node.copy(attributes = updatedAttributes, children = optimizedChildren)
    }

    /**
     * Optimizes match_parent layouts for better performance.
     */
    private fun optimizeMatchParent(node: ViewNode): ViewNode {
        logger.debug("optimizeMatchParent", "Optimizing match_parent layouts")

        val updatedAttributes = ArrayMap<String, String>(node.attributes)

        // Add optimization hints for match_parent layouts
        if (updatedAttributes["android:layout_width"] == "match_parent" && updatedAttributes["android:layout_height"] == "match_parent") {
            updatedAttributes["voyager:layout_optimization"] = "match_parent_optimized"
        }

        val optimizedChildren = node.children.map { optimizeMatchParent(it) }.toMutableList()
        return node.copy(attributes = updatedAttributes, children = optimizedChildren)
    }

    /**
     * Suggests using RecyclerView for long lists.
     */
    private fun suggestRecyclerView(node: ViewNode): ViewNode {
        logger.debug("suggestRecyclerView", "Suggesting RecyclerView optimization")

        if (node.type == "LinearLayout" && node.children.size > 5) {
            val updatedAttributes = ArrayMap<String, String>(node.attributes)
            updatedAttributes["voyager:optimization_suggestion"] = "consider_recycler_view"
            return node.copy(attributes = updatedAttributes)
        }

        val optimizedChildren = node.children.map { suggestRecyclerView(it) }.toMutableList()
        return node.copy(children = optimizedChildren)
    }

    /**
     * Removes unnecessary wrapper layouts.
     */
    private fun removeUnnecessaryWrappers(node: ViewNode): ViewNode {
        logger.debug("removeUnnecessaryWrappers", "Removing unnecessary wrappers")

        // Check if this wrapper can be removed
        if (isUnnecessaryWrapper(node)) {
            // Return the child instead of the wrapper
            return node.children.firstOrNull() ?: node
        }

        val optimizedChildren = node.children.map { removeUnnecessaryWrappers(it) }.toMutableList()
        return node.copy(children = optimizedChildren)
    }

    /**
     * Merges duplicate or redundant attributes.
     */
    private fun mergeAttributes(node: ViewNode): ViewNode {
        logger.debug("mergeAttributes", "Merging attributes")

        val mergedAttributes = optimizeAttributeMap(node.attributes)
        val optimizedChildren = node.children.map { mergeAttributes(it) }.toMutableList()

        return node.copy(attributes = mergedAttributes, children = optimizedChildren)
    }

    /**
     * Enables view caching for frequently accessed views.
     */
    private fun enableViewCaching(node: ViewNode): ViewNode {
        logger.debug("enableViewCaching", "Enabling view caching")

        val updatedAttributes = ArrayMap<String, String>(node.attributes)

        // Add caching hints for views with IDs
        if (node.id != null) {
            updatedAttributes["voyager:cache_enabled"] = "true"
            updatedAttributes["voyager:cache_key"] = node.id
        }

        val optimizedChildren = node.children.map { enableViewCaching(it) }.toMutableList()
        return node.copy(attributes = updatedAttributes, children = optimizedChildren)
    }

    /**
     * Preloads resources that will be needed at runtime.
     */
    private fun preloadResources(node: ViewNode): ViewNode {
        logger.debug("preloadResources", "Preloading resources")

        val preloadHints = mutableListOf<String>()

        // Identify resources to preload
        node.attributes.forEach { (key, value) ->
            when {
                key.contains("background") -> preloadHints.add("drawable:$value")
                key.contains("src") -> preloadHints.add("drawable:$value")
                key.contains("text") && value.toString().startsWith("@string/") -> preloadHints.add(
                    "string:${value.toString().substring(8)}"
                )
            }
        }

        val updatedAttributes = ArrayMap<String, String>(node.attributes)
        if (preloadHints.isNotEmpty()) {
            updatedAttributes["voyager:preload_resources"] = preloadHints.joinToString(",")
        }

        val optimizedChildren = node.children.map { preloadResources(it) }.toMutableList()
        return node.copy(attributes = updatedAttributes, children = optimizedChildren)
    }

    // Helper methods
    private fun isWrapperLayout(node: ViewNode): Boolean {
        return when (node.type) {
            "FrameLayout", "LinearLayout" -> {
                // Check if it's just wrapping content without meaningful attributes
                val meaningfulAttrs = node.attributes.filterKeys { key ->
                    !key.startsWith("android:layout_") && key != "android:orientation"
                }
                meaningfulAttrs.isEmpty()
            }

            else -> false
        }
    }

    private fun isUnnecessaryWrapper(node: ViewNode): Boolean {
        return node.children.size == 1 && isWrapperLayout(node) && !hasEssentialAttributes(node)
    }

    private fun hasEssentialAttributes(node: ViewNode): Boolean {
        val essentialAttributes = setOf(
            "android:background",
            "android:padding",
            "android:margin",
            "android:elevation",
            "android:clickable"
        )

        return node.attributes.keys.any { it in essentialAttributes }
    }

    private fun mergeCompatibleAttributes(
        parentAttrs: Map<String, String>?,
        childAttrs: Map<String, String>?,
    ): ArrayMap<String, String> {
        val merged = ArrayMap<String, String>()

        // Add child attributes first (they have priority)
        childAttrs?.let { merged.putAll(it) }

        // Add parent attributes that don't conflict
        parentAttrs?.forEach { (key, value) ->
            if (!merged.containsKey(key) && isTransferableAttribute(key)) {
                merged[key] = value
            }
        }

        return merged
    }

    private fun isTransferableAttribute(attributeName: String): Boolean {
        // Define which attributes can be transferred from parent to child
        val transferableAttributes = setOf(
            "android:layout_width",
            "android:layout_height",
            "android:layout_margin",
            "android:padding"
        )

        return attributeName in transferableAttributes
    }

    private fun optimizeAttributeMap(attributes: Map<String, String>?): ArrayMap<String, String> {
        if (attributes == null) return ArrayMap()

        val optimized = ArrayMap<String, String>()

        // Remove redundant attributes
        attributes.forEach { (key, value) ->
            if (!isRedundantAttribute(key, attributes)) {
                optimized[key] = value
            }
        }

        return optimized
    }

    //check for the value of them is it's same then dont
    private fun isRedundantAttribute(
        key: String,
        allAttributes: Map<String, Any>,
    ): Boolean {
        // Check for redundant attribute patterns
        when (key) {
            "android:layout_marginLeft", "android:layout_marginRight",
            "android:layout_marginTop", "android:layout_marginBottom",
                -> {
                // If layout_margin is set, individual margins might be redundant
                return allAttributes.containsKey("android:layout_margin")
            }

            "android:paddingLeft", "android:paddingRight",
            "android:paddingTop", "android:paddingBottom",
                -> {
                // If padding is set, individual paddings might be redundant
                return allAttributes.containsKey("android:padding")
            }
        }

        return false
    }
}