package com.voyager.core.profiling

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.view.View
import android.view.ViewGroup
import com.voyager.core.utils.logging.LoggerFactory
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Advanced memory profiler and leak detection system for Voyager.
 * 
 * Features:
 * - Real-time memory monitoring
 * - View leak detection
 * - Memory usage analytics
 * - Automatic cleanup suggestions
 * - Performance impact analysis
 * 
 * Benefits:
 * - Prevents OutOfMemoryError crashes
 * - Identifies memory bottlenecks
 * - Provides actionable optimization suggestions
 * - Tracks memory trends over time
 */
class MemoryProfiler private constructor() {
    private val logger = LoggerFactory.getLogger("MemoryProfiler")
    
    // Memory tracking
    private val memorySnapshots = mutableListOf<MemorySnapshot>()
    private val maxSnapshots = 100
    
    // View tracking for leak detection
    private val trackedViews = ConcurrentHashMap<String, TrackedView>()
    private val trackedActivities = ConcurrentHashMap<String, WeakReference<Activity>>()
    
    // Statistics
    private val stats = MemoryStats()
    
    // Configuration
    private var isEnabled = true
    private var monitoringInterval = DEFAULT_MONITORING_INTERVAL
    private var leakDetectionEnabled = true
    
    // Scheduled monitoring
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "VoyagerMemoryProfiler").apply { isDaemon = true }
    }
    
    /**
     * Initializes the memory profiler with the given context.
     */
    fun initialize(context: Context) {
        if (!isEnabled) return
        
        logger.info("initialize", "Starting memory profiler")
        
        // Start periodic monitoring
        scheduler.scheduleAtFixedRate(
            { performMonitoring(context) },
            monitoringInterval,
            monitoringInterval,
            TimeUnit.MILLISECONDS
        )
        
        // Start leak detection
        if (leakDetectionEnabled) {
            scheduler.scheduleAtFixedRate(
                { performLeakDetection() },
                LEAK_DETECTION_INTERVAL,
                LEAK_DETECTION_INTERVAL,
                TimeUnit.MILLISECONDS
            )
        }
        
        logger.info("initialize", "Memory profiler started successfully")
    }
    
    /**
     * Tracks a view for potential memory leaks.
     */
    fun trackView(view: View, identifier: String = view.javaClass.simpleName) {
        if (!isEnabled || !leakDetectionEnabled) return
        
        val trackingId = "${identifier}_${System.currentTimeMillis()}"
        val trackedView = TrackedView(
            id = trackingId,
            viewRef = WeakReference(view),
            creationTime = System.currentTimeMillis(),
            viewType = view.javaClass.simpleName,
            identifier = identifier
        )
        
        trackedViews[trackingId] = trackedView
        stats.viewsTracked.incrementAndGet()
        
        logger.debug("trackView", "Started tracking view: $identifier")
    }
    
    /**
     * Tracks an activity for memory leak detection.
     */
    fun trackActivity(activity: Activity) {
        if (!isEnabled || !leakDetectionEnabled) return
        
        val activityId = "${activity.javaClass.simpleName}_${System.currentTimeMillis()}"
        trackedActivities[activityId] = WeakReference(activity)
        
        logger.debug("trackActivity", "Started tracking activity: ${activity.javaClass.simpleName}")
    }
    
    /**
     * Stops tracking a view.
     */
    fun untrackView(identifier: String) {
        val removed = trackedViews.values.removeAll { it.identifier == identifier }
        if (removed) {
            logger.debug("untrackView", "Stopped tracking view: $identifier")
        }
    }
    
    /**
     * Performs immediate memory analysis and returns results.
     */
    fun analyzeMemory(context: Context): MemoryAnalysis {
        logger.debug("analyzeMemory", "Performing memory analysis")
        
        val snapshot = captureMemorySnapshot(context)
        val leaks = detectLeaks()
        val suggestions = generateOptimizationSuggestions(snapshot, leaks)
        
        return MemoryAnalysis(
            snapshot = snapshot,
            detectedLeaks = leaks,
            suggestions = suggestions,
            analysisTime = System.currentTimeMillis()
        )
    }
    
    /**
     * Returns current memory statistics.
     */
    fun getStats(): MemoryStats = stats.copy()
    
    /**
     * Returns memory usage history.
     */
    fun getMemoryHistory(): List<MemorySnapshot> = memorySnapshots.toList()
    
    /**
     * Configures the memory profiler.
     */
    fun configure(config: MemoryProfilerConfig) {
        isEnabled = config.enabled
        monitoringInterval = config.monitoringInterval
        leakDetectionEnabled = config.leakDetectionEnabled
        
        logger.info("configure", "Memory profiler configured: enabled=$isEnabled, interval=${monitoringInterval}ms")
    }
    
    /**
     * Clears all tracking data and history.
     */
    fun clear() {
        trackedViews.clear()
        trackedActivities.clear()
        memorySnapshots.clear()
        stats.reset()
        
        logger.info("clear", "Memory profiler data cleared")
    }
    
    /**
     * Shuts down the memory profiler.
     */
    fun shutdown() {
        scheduler.shutdown()
        clear()
        logger.info("shutdown", "Memory profiler shut down")
    }
    
    // Private methods
    private fun performMonitoring(context: Context) {
        try {
            val snapshot = captureMemorySnapshot(context)
            
            synchronized(memorySnapshots) {
                memorySnapshots.add(snapshot)
                if (memorySnapshots.size > maxSnapshots) {
                    memorySnapshots.removeAt(0)
                }
            }
            
            // Check for memory warnings
            checkMemoryWarnings(snapshot)
            
            stats.monitoringCycles.incrementAndGet()
            
        } catch (e: Exception) {
            logger.error("performMonitoring", "Error during memory monitoring", e)
        }
    }
    
    private fun captureMemorySnapshot(context: Context): MemorySnapshot {
        val runtime = Runtime.getRuntime()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        return MemorySnapshot(
            timestamp = System.currentTimeMillis(),
            usedMemory = runtime.totalMemory() - runtime.freeMemory(),
            freeMemory = runtime.freeMemory(),
            totalMemory = runtime.totalMemory(),
            maxMemory = runtime.maxMemory(),
            systemAvailableMemory = memoryInfo.availMem,
            systemTotalMemory = memoryInfo.totalMem,
            isLowMemory = memoryInfo.lowMemory,
            nativeHeapSize = Debug.getNativeHeapSize(),
            nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize(),
            trackedViewCount = trackedViews.size,
            trackedActivityCount = trackedActivities.size
        )
    }
    
    private fun performLeakDetection() {
        try {
            val leaks = detectLeaks()
            
            if (leaks.isNotEmpty()) {
                stats.leaksDetected.addAndGet(leaks.size)
                logger.warning("performLeakDetection", "Detected ${leaks.size} potential memory leaks")
                
                leaks.forEach { leak ->
                    logger.warning("performLeakDetection", "Leak detected: ${leak.description}")
                }
            }
            
            stats.leakDetectionCycles.incrementAndGet()
            
        } catch (e: Exception) {
            logger.error("performLeakDetection", "Error during leak detection", e)
        }
    }
    
    private fun detectLeaks(): List<MemoryLeak> {
        val leaks = mutableListOf<MemoryLeak>()
        val currentTime = System.currentTimeMillis()
        
        // Check for view leaks
        val viewLeaks = trackedViews.values.filter { trackedView ->
            val view = trackedView.viewRef.get()
            val age = currentTime - trackedView.creationTime
            
            // Consider a view leaked if it's been tracked for too long and is not garbage collected
            view != null && age > VIEW_LEAK_THRESHOLD && !isViewAttached(view)
        }
        
        viewLeaks.forEach { trackedView ->
            leaks.add(
                MemoryLeak(
                    type = LeakType.VIEW_LEAK,
                    description = "View ${trackedView.identifier} appears to be leaked",
                    severity = LeakSeverity.MEDIUM,
                    suggestions = listOf(
                        "Ensure view is properly removed from parent",
                        "Check for retained references to the view",
                        "Verify listeners are properly cleared"
                    )
                )
            )
        }
        
        // Check for activity leaks
        val activityLeaks = trackedActivities.values.filter { activityRef ->
            val activity = activityRef.get()
            activity != null && activity.isFinishing
        }
        
        activityLeaks.forEach { _ ->
            leaks.add(
                MemoryLeak(
                    type = LeakType.ACTIVITY_LEAK,
                    description = "Activity appears to be leaked after finishing",
                    severity = LeakSeverity.HIGH,
                    suggestions = listOf(
                        "Check for static references to the activity",
                        "Ensure async tasks are properly cancelled",
                        "Verify listeners are unregistered in onDestroy"
                    )
                )
            )
        }
        
        return leaks
    }
    
    private fun isViewAttached(view: View): Boolean {
        return view.parent != null || view.isAttachedToWindow
    }
    
    private fun checkMemoryWarnings(snapshot: MemorySnapshot) {
        // Check for high memory usage
        val memoryUsageRatio = snapshot.usedMemory.toFloat() / snapshot.maxMemory
        if (memoryUsageRatio > HIGH_MEMORY_THRESHOLD) {
            stats.memoryWarnings.incrementAndGet()
            logger.warning("checkMemoryWarnings", "High memory usage detected: ${(memoryUsageRatio * 100).toInt()}%")
        }
        
        // Check for system low memory
        if (snapshot.isLowMemory) {
            stats.lowMemoryWarnings.incrementAndGet()
            logger.warning("checkMemoryWarnings", "System low memory detected")
        }
    }
    
    private fun generateOptimizationSuggestions(
        snapshot: MemorySnapshot,
        leaks: List<MemoryLeak>
    ): List<OptimizationSuggestion> {
        val suggestions = mutableListOf<OptimizationSuggestion>()
        
        // Memory usage suggestions
        val memoryUsageRatio = snapshot.usedMemory.toFloat() / snapshot.maxMemory
        if (memoryUsageRatio > 0.8f) {
            suggestions.add(
                OptimizationSuggestion(
                    type = SuggestionType.MEMORY_OPTIMIZATION,
                    title = "High Memory Usage",
                    description = "Memory usage is at ${(memoryUsageRatio * 100).toInt()}%",
                    actions = listOf(
                        "Clear unused view caches",
                        "Reduce view recycling pool sizes",
                        "Consider using lighter view types"
                    ),
                    priority = Priority.HIGH
                )
            )
        }
        
        // Leak-based suggestions
        if (leaks.isNotEmpty()) {
            suggestions.add(
                OptimizationSuggestion(
                    type = SuggestionType.LEAK_PREVENTION,
                    title = "Memory Leaks Detected",
                    description = "${leaks.size} potential memory leaks found",
                    actions = leaks.flatMap { it.suggestions },
                    priority = Priority.HIGH
                )
            )
        }
        
        // View count suggestions
        if (snapshot.trackedViewCount > 100) {
            suggestions.add(
                OptimizationSuggestion(
                    type = SuggestionType.VIEW_OPTIMIZATION,
                    title = "High View Count",
                    description = "${snapshot.trackedViewCount} views are being tracked",
                    actions = listOf(
                        "Consider using view recycling",
                        "Implement view virtualization",
                        "Reduce view hierarchy complexity"
                    ),
                    priority = Priority.MEDIUM
                )
            )
        }
        
        return suggestions
    }
    
    companion object {
        private const val DEFAULT_MONITORING_INTERVAL = 5000L // 5 seconds
        private const val LEAK_DETECTION_INTERVAL = 30000L // 30 seconds
        private const val VIEW_LEAK_THRESHOLD = 60000L // 1 minute
        private const val HIGH_MEMORY_THRESHOLD = 0.85f // 85%
        
        @Volatile
        private var INSTANCE: MemoryProfiler? = null
        
        fun getInstance(): MemoryProfiler {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MemoryProfiler().also { INSTANCE = it }
            }
        }
    }
}

/**
 * Represents a memory snapshot at a specific point in time.
 */
data class MemorySnapshot(
    val timestamp: Long,
    val usedMemory: Long,
    val freeMemory: Long,
    val totalMemory: Long,
    val maxMemory: Long,
    val systemAvailableMemory: Long,
    val systemTotalMemory: Long,
    val isLowMemory: Boolean,
    val nativeHeapSize: Long,
    val nativeHeapAllocatedSize: Long,
    val trackedViewCount: Int,
    val trackedActivityCount: Int
)

/**
 * Represents a tracked view for leak detection.
 */
private data class TrackedView(
    val id: String,
    val viewRef: WeakReference<View>,
    val creationTime: Long,
    val viewType: String,
    val identifier: String
)

/**
 * Results of memory analysis.
 */
data class MemoryAnalysis(
    val snapshot: MemorySnapshot,
    val detectedLeaks: List<MemoryLeak>,
    val suggestions: List<OptimizationSuggestion>,
    val analysisTime: Long
)

/**
 * Represents a detected memory leak.
 */
data class MemoryLeak(
    val type: LeakType,
    val description: String,
    val severity: LeakSeverity,
    val suggestions: List<String>
)

/**
 * Types of memory leaks.
 */
enum class LeakType {
    VIEW_LEAK,
    ACTIVITY_LEAK,
    CONTEXT_LEAK,
    LISTENER_LEAK,
    BITMAP_LEAK
}

/**
 * Severity levels for memory leaks.
 */
enum class LeakSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

/**
 * Optimization suggestion.
 */
data class OptimizationSuggestion(
    val type: SuggestionType,
    val title: String,
    val description: String,
    val actions: List<String>,
    val priority: Priority
)

/**
 * Types of optimization suggestions.
 */
enum class SuggestionType {
    MEMORY_OPTIMIZATION,
    LEAK_PREVENTION,
    VIEW_OPTIMIZATION,
    PERFORMANCE_IMPROVEMENT
}

/**
 * Priority levels for suggestions.
 */
enum class Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

/**
 * Memory profiler statistics.
 */
data class MemoryStats(
    val monitoringCycles: AtomicLong = AtomicLong(0),
    val leakDetectionCycles: AtomicLong = AtomicLong(0),
    val viewsTracked: AtomicLong = AtomicLong(0),
    val leaksDetected: AtomicLong = AtomicLong(0),
    val memoryWarnings: AtomicLong = AtomicLong(0),
    val lowMemoryWarnings: AtomicLong = AtomicLong(0)
) {
    fun copy(): MemoryStats {
        return MemoryStats(
            AtomicLong(monitoringCycles.get()),
            AtomicLong(leakDetectionCycles.get()),
            AtomicLong(viewsTracked.get()),
            AtomicLong(leaksDetected.get()),
            AtomicLong(memoryWarnings.get()),
            AtomicLong(lowMemoryWarnings.get())
        )
    }
    
    fun reset() {
        monitoringCycles.set(0)
        leakDetectionCycles.set(0)
        viewsTracked.set(0)
        leaksDetected.set(0)
        memoryWarnings.set(0)
        lowMemoryWarnings.set(0)
    }
}

/**
 * Configuration for the memory profiler.
 */
data class MemoryProfilerConfig(
    val enabled: Boolean = true,
    val monitoringInterval: Long = 5000L,
    val leakDetectionEnabled: Boolean = true
)