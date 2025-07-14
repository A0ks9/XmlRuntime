package com.voyager.core.compiler

import com.voyager.core.model.ViewNode

/**
 * Represents a compiled layout with all optimizations applied.
 */
data class CompiledLayout(
    val id: String,
    val originalNode: ViewNode,
    val optimizedNode: ViewNode,
    val bytecode: ByteArray,
    val metadata: LayoutMetadata
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CompiledLayout
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Metadata about the compilation process and optimizations.
 */
data class LayoutMetadata(
    val compilationTime: Long,
    val optimizations: List<OptimizationType>,
    val performance: PerformanceMetrics
)

/**
 * Results of static analysis performed on the layout.
 */
data class StaticAnalysisResult(
    val performanceMetrics: PerformanceMetrics,
    val appliedOptimizations: List<OptimizationType>,
    val detectedIssues: List<LayoutIssue>
)

/**
 * Performance metrics collected during analysis.
 */
data class PerformanceMetrics(
    var viewHierarchyDepth: Int = 0,
    var totalViewCount: Int = 0,
    var attributeCount: Int = 0,
    var estimatedInflationTime: Long = 0,
    var estimatedMemoryUsage: Long = 0
)

/**
 * Types of optimizations that can be applied.
 */
enum class OptimizationType {
    FLATTEN_HIERARCHY,
    VIEW_RECYCLING,
    OPTIMIZE_MATCH_PARENT,
    USE_RECYCLER_VIEW,
    REMOVE_UNNECESSARY_WRAPPER,
    MERGE_ATTRIBUTES,
    CACHE_VIEWS,
    PRELOAD_RESOURCES
}

/**
 * Layout issues detected during analysis.
 */
enum class LayoutIssue {
    DEEP_HIERARCHY,
    TOO_MANY_VIEWS,
    INEFFICIENT_LAYOUT,
    MISSING_ATTRIBUTES,
    PERFORMANCE_WARNING
} 