package com.voyager.core.compiler

import com.voyager.core.model.ViewNode
import com.voyager.core.utils.logging.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap

/**
 * Advanced layout compiler that generates optimized bytecode for layout inflation.
 * Modernized for speed, memory, and conciseness.
 */
class LayoutCompiler {
    private val logger = LoggerFactory.getLogger("LayoutCompiler")
    private val compiledLayouts = ConcurrentHashMap<String, CompiledLayout>()
    private val optimizationRules = OptimizationRuleEngine()

    /**
     * Compiles a layout into optimized bytecode representation.
     * This process includes static analysis, optimization, and bytecode generation.
     */
    suspend fun compileLayout(
        layoutId: String,
        viewNode: ViewNode
    ): CompiledLayout = withContext(Dispatchers.Default) {
        compiledLayouts[layoutId] ?: run {
            try {
                val analysis = viewNode.staticAnalysis
                val optimized = optimizationRules.optimize(viewNode, analysis)
                val bytecode = optimized.toBytecode
                val compiled = CompiledLayout(
                    id = layoutId,
                    originalNode = viewNode,
                    optimizedNode = optimized,
                    bytecode = bytecode,
                    metadata = LayoutMetadata(
                        compilationTime = System.currentTimeMillis(),
                        optimizations = analysis.appliedOptimizations,
                        performance = analysis.performanceMetrics
                    )
                )
                compiledLayouts[layoutId] = compiled
                logger.info("compileLayout", "Compiled layout: $layoutId")
                compiled
            } catch (e: Exception) {
                logger.error("compileLayout", "Failed to compile layout $layoutId", e)
                throw LayoutCompilationException("Failed to compile layout $layoutId", e)
            }
        }
    }

    companion object {
        const val MAX_RECOMMENDED_DEPTH = 10
        const val MAX_RECOMMENDED_VIEWS = 50
    }
}

private inline val ViewNode.viewDepth: Int
    get() {
        var maxDepth = 0
        val stack = ArrayDeque<Pair<ViewNode, Int>>()
        stack.add(this to 0)
        while (stack.isNotEmpty()) {
            val (node, depth) = stack.removeLast()
            if (depth > maxDepth) maxDepth = depth
            node.children.forEach { stack.add(it to depth + 1) }
        }
        return maxDepth
    }

private inline val ViewNode.viewCount: Int
    get() = 1 + children.sumOf { it.viewCount }

private inline val ViewNode.staticAnalysis: StaticAnalysisResult
    get() {
        val metrics = PerformanceMetrics()
        val optimizations = mutableListOf<OptimizationType>()
        val issues = mutableListOf<LayoutIssue>()
        val depth = viewDepth
        metrics.viewHierarchyDepth = depth
        if (depth > LayoutCompiler.MAX_RECOMMENDED_DEPTH) {
            issues += LayoutIssue.DEEP_HIERARCHY
            optimizations += OptimizationType.FLATTEN_HIERARCHY
        }
        val count = viewCount
        metrics.totalViewCount = count
        if (count > LayoutCompiler.MAX_RECOMMENDED_VIEWS) {
            issues += LayoutIssue.TOO_MANY_VIEWS
            optimizations += OptimizationType.VIEW_RECYCLING
        }
        analyzeAttributeUsage(metrics, optimizations)
        analyzeLayoutPatterns(metrics, optimizations)
        return StaticAnalysisResult(metrics, optimizations, issues)
    }

private fun ViewNode.analyzeAttributeUsage(
    metrics: PerformanceMetrics,
    optimizations: MutableList<OptimizationType>,
) {
    metrics.attributeCount += attributes.size
    if (attributes["android:layout_width"] == "match_parent" && attributes["android:layout_height"] == "match_parent") optimizations += OptimizationType.OPTIMIZE_MATCH_PARENT
    children.forEach { it.analyzeAttributeUsage(metrics, optimizations) }
}

private fun ViewNode.analyzeLayoutPatterns(
    metrics: PerformanceMetrics,
    optimizations: MutableList<OptimizationType>,
) {
    when (type) {
        "LinearLayout" -> if (children.size > 5) optimizations += OptimizationType.USE_RECYCLER_VIEW
        "FrameLayout" -> if (children.size == 1) optimizations += OptimizationType.REMOVE_UNNECESSARY_WRAPPER
    }
    children.forEach { it.analyzeLayoutPatterns(metrics, optimizations) }
}

private inline val ViewNode.toBytecode: ByteArray
    get() = BytecodeGenerator().apply {
        writeHeader(this@toBytecode)
        generateViewCreationCode(this@toBytecode)
        generateAttributeCode(this@toBytecode)
        generateLayoutCode(this@toBytecode)
        writeFinalization()
    }.toByteArray()

private fun BytecodeGenerator.generateViewCreationCode(node: ViewNode) {
    writeInstruction(OpCode.CREATE_VIEW, node.type)
    writeViewId(node.id ?: "")
    node.children.forEach { generateViewCreationCode(it) }
}

private fun BytecodeGenerator.generateAttributeCode(node: ViewNode) {
    repeat(node.attributes.size) { i ->
        writeInstruction(OpCode.SET_ATTRIBUTE, node.attributes.keyAt(i), node.attributes.valueAt(i))
    }
    node.children.forEach { generateAttributeCode(it) }
}

private fun BytecodeGenerator.generateLayoutCode(node: ViewNode) {
    writeInstruction(OpCode.SET_LAYOUT_PARAMS)
    node.children.forEach { generateLayoutCode(it) }
}