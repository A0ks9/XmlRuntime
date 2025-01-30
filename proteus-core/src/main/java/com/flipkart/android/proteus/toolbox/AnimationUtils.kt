package com.flipkart.android.proteus.toolbox

import android.content.Context
import android.util.Log
import android.view.animation.*
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value

object AnimationUtils {

    private const val TAG = "AnimationUtils"

    private const val LINEAR_INTERPOLATOR = "linearInterpolator"
    private const val ACCELERATE_INTERPOLATOR = "accelerateInterpolator"
    private const val DECELERATE_INTERPOLATOR = "decelerateInterpolator"
    private const val ACCELERATE_DECELERATE_INTERPOLATOR = "accelerateDecelerateInterpolator"
    private const val CYCLE_INTERPOLATOR = "cycleInterpolator"
    private const val ANTICIPATE_INTERPOLATOR = "anticipateInterpolator"
    private const val OVERSHOOT_INTERPOLATOR = "overshootInterpolator"
    private const val ANTICIPATE_OVERSHOOT_INTERPOLATOR = "anticipateOvershootInterpolator"
    private const val BOUNCE_INTERPOLATOR = "bounceInterpolator"
    private const val PATH_INTERPOLATOR = "pathInterpolator"

    private const val TYPE = "type"
    private const val SET = "set"
    private const val ALPHA = "alpha"
    private const val SCALE = "scale"
    private const val ROTATE = "rotate"
    private const val TRANSLATE = "translate"
    private const val PERCENT_SELF = "%"
    private const val PERCENT_RELATIVE_PARENT = "%p"


    /**
     * Loads an [Animation] object from a resource.
     */
    @JvmStatic
    fun loadAnimation(context: Context, value: Value): Animation? = when {
        value.isPrimitive -> handleString(context, value.asPrimitive().getAsString())
        value.isObject -> handleElement(context, value.asObject())
        else -> {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Could not load animation for : $value")
            }
            null
        }
    }


    private fun handleString(c: Context, value: String): Animation? =
        if (ParseHelper.isTweenAnimationResource(value)) {
            try {
                val animationId = c.resources.getIdentifier(value, "anim", c.packageName)
                android.view.animation.AnimationUtils.loadAnimation(c, animationId)
            } catch (ex: Exception) {
                println("Could not load local resource $value")
                null
            }
        } else {
            null
        }


    private fun handleElement(context: Context, value: ObjectValue): Animation? {
        val animationProperties = when (value.getAsString(TYPE)?.lowercase()) {
            SET -> AnimationSetProperties(value)
            ALPHA -> AlphaAnimProperties(value)
            SCALE -> ScaleAnimProperties(value)
            ROTATE -> RotateAnimProperties(value)
            TRANSLATE -> TranslateAnimProperties(value)
            else -> null
        }
        return animationProperties?.instantiate(context)
    }

    /**
     * Loads an [Interpolator] object from a resource.
     */
    @JvmStatic
    fun loadInterpolator(context: Context, value: Value): Interpolator? = when {
        value.isPrimitive -> handleStringInterpolator(context, value.getAsString())
        value.isObject -> handleElementInterpolator(context, value.asObject())
        else -> {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Could not load interpolator for : $value")
            }
            null
        }
    }

    private fun handleStringInterpolator(c: Context, value: String): Interpolator? =
        if (ParseHelper.isTweenAnimationResource(value)) {
            try {
                val interpolatorID = c.resources.getIdentifier(value, "anim", c.packageName)
                android.view.animation.AnimationUtils.loadInterpolator(c, interpolatorID)
            } catch (ex: Exception) {
                println("Could not load local resource $value")
                null
            }
        } else {
            null
        }

    private fun handleElementInterpolator(c: Context, value: ObjectValue): Interpolator? {
        val interpolatorProperties = when (value.getAsString(TYPE)?.lowercase()) {
            LINEAR_INTERPOLATOR -> LinearInterpolator()
            ACCELERATE_INTERPOLATOR -> AccelerateInterpolator()
            DECELERATE_INTERPOLATOR -> DecelerateInterpolator()
            ACCELERATE_DECELERATE_INTERPOLATOR -> AccelerateDecelerateInterpolator()
            CYCLE_INTERPOLATOR -> CycleInterpolatorProperties(value)
            ANTICIPATE_INTERPOLATOR -> AnticipateInterpolatorProperties(value)
            OVERSHOOT_INTERPOLATOR -> OvershootInterpolatorProperties(value)
            ANTICIPATE_OVERSHOOT_INTERPOLATOR -> AnticipateOvershootInterpolatorProperties(value)
            BOUNCE_INTERPOLATOR -> BounceInterpolator()
            PATH_INTERPOLATOR -> PathInterpolatorProperties(value)
            else -> {
                if (ProteusConstants.isLoggingEnabled()) {
                    Log.e(TAG, "Unknown interpolator name: ${value.getAsString("type")}")
                }
                throw RuntimeException("Unknown interpolator name: ${value.getAsString("type")}")
            }

        }
        return when (interpolatorProperties) {
            is InterpolatorProperties -> interpolatorProperties.createInterpolator(c)
            is Interpolator -> interpolatorProperties
            else -> null
        }

    }


    /**
     * Utility class to parse a string description of a size.
     */
    private data class Description(var type: Int = Animation.ABSOLUTE, var value: Float = 0f) {
        companion object {
            fun parseValue(value: Value?): Description {
                val d = Description()
                if (value != null && value.isPrimitive) {
                    val primitive = value.asPrimitive()
                    when {
                        primitive.isNumber() -> {
                            d.type = Animation.ABSOLUTE
                            d.value = primitive.getAsFloat()
                        }

                        else -> {
                            val stringValue = primitive.getAsString()
                            when {
                                stringValue.endsWith(PERCENT_SELF) -> {
                                    val stringValueWithoutSuffix = stringValue.substring(
                                        0, stringValue.length - PERCENT_SELF.length
                                    )
                                    d.value = stringValueWithoutSuffix.toFloat() / 100
                                    d.type = Animation.RELATIVE_TO_SELF
                                }

                                stringValue.endsWith(PERCENT_RELATIVE_PARENT) -> {
                                    val stringValueWithoutSuffix = stringValue.substring(
                                        0, stringValue.length - PERCENT_RELATIVE_PARENT.length
                                    )
                                    d.value = stringValueWithoutSuffix.toFloat() / 100
                                    d.type = Animation.RELATIVE_TO_PARENT
                                }

                                else -> {
                                    d.type = Animation.ABSOLUTE
                                    d.value = primitive.getAsFloat()
                                }
                            }
                        }
                    }

                }
                return d
            }
        }

    }


    private abstract class AnimationProperties(value: ObjectValue) {
        companion object {
            const val DURATION = "duration"
            const val FILL_AFTER = "fillAfter"
            const val FILL_BEFORE = "fillBefore"
            const val FILL_ENABLED = "fillEnabled"
            const val INTERPOLATOR = "interpolator"
            const val REPEAT_COUNT = "repeatCount"
            const val REPEAT_MODE = "repeatMode"
            const val START_OFFSET = "startOffset"
            const val Z_ADJUSTMENT = "zAdjustment"
        }


        val durationC: Long? = value.getAsLong(DURATION)
        val fillAfterC: Boolean? = value.getAsBoolean(FILL_AFTER)
        val fillBeforeC: Boolean? = value.getAsBoolean(FILL_BEFORE)
        val fillEnabledC: Boolean? = value.getAsBoolean(FILL_ENABLED)
        val interpolatorC: Value? = value[INTERPOLATOR]
        val repeatCountC: Int? = value.getAsInteger(REPEAT_COUNT)
        val repeatModeC: Int? = value.getAsInteger(REPEAT_MODE)
        val startOffsetC: Long? = value.getAsLong(START_OFFSET)
        val zAdjustmentC: Int? = value.getAsInteger(Z_ADJUSTMENT)


        fun instantiate(c: Context): Animation? {
            val anim = createAnimation(c)
            anim?.apply {
                durationC?.let { setDuration(it) }
                fillAfterC?.let { fillAfter = it }
                fillBeforeC?.let { fillBefore = it }
                fillEnabledC?.let { isFillEnabled = it }
                interpolatorC?.let { loadInterpolator(c, it)?.let { interpolator = it } }
                repeatCountC?.let { setRepeatCount(it) }
                repeatModeC?.let { repeatMode = it }
                startOffsetC?.let { startOffset = it }
                zAdjustmentC?.let { zAdjustment = it }
            }
            return anim
        }


        abstract fun createAnimation(c: Context): Animation?
    }

    private class AnimationSetProperties(value: ObjectValue) : AnimationProperties(value) {

        companion object {
            const val SHARE_INTERPOLATOR = "shareInterpolator"
            const val CHILDREN = "children"
        }

        val shareInterpolator: Boolean? = value.getAsBoolean(SHARE_INTERPOLATOR)
        val children: Value? = value[CHILDREN]

        override fun createAnimation(c: Context): Animation {
            val animationSet = AnimationSet(shareInterpolator != false)

            children?.let {
                when {
                    it.isArray -> it.asArray()
                        .forEach { animationSet.addAnimation(loadAnimation(c, it)) }

                    it.isObject || it.isPrimitive -> animationSet.addAnimation(loadAnimation(c, it))
                }

            }
            return animationSet
        }
    }


    private class AlphaAnimProperties(value: ObjectValue) : AnimationProperties(value) {

        companion object {
            const val FROM_ALPHA = "fromAlpha"
            const val TO_ALPHA = "toAlpha"
        }

        val fromAlpha: Float? = value.getAsFloat(FROM_ALPHA)
        val toAlpha: Float? = value.getAsFloat(TO_ALPHA)


        override fun createAnimation(c: Context): Animation? =
            if (fromAlpha != null && toAlpha != null) AlphaAnimation(fromAlpha, toAlpha) else null

    }

    private class ScaleAnimProperties(value: ObjectValue) : AnimationProperties(value) {

        companion object {
            const val FROM_X_SCALE = "fromXScale"
            const val TO_X_SCALE = "toXScale"
            const val FROM_Y_SCALE = "fromYScale"
            const val TO_Y_SCALE = "toYScale"
            const val PIVOT_X = "pivotX"
            const val PIVOT_Y = "pivotY"
        }


        val fromXScale: Float? = value.getAsFloat(FROM_X_SCALE)
        val toXScale: Float? = value.getAsFloat(TO_X_SCALE)
        val fromYScale: Float? = value.getAsFloat(FROM_Y_SCALE)
        val toYScale: Float? = value.getAsFloat(TO_Y_SCALE)
        val pivotX: Value? = value[PIVOT_X]
        val pivotY: Value? = value[PIVOT_Y]


        override fun createAnimation(c: Context): Animation {
            val scaleAnimation = if (pivotX != null && pivotY != null) {
                val pivotXDesc = Description.parseValue(pivotX)
                val pivotYDesc = Description.parseValue(pivotY)
                ScaleAnimation(
                    fromXScale ?: 0f,
                    toXScale ?: 0f,
                    fromYScale ?: 0f,
                    toYScale ?: 0f,
                    pivotXDesc.type,
                    pivotXDesc.value,
                    pivotYDesc.type,
                    pivotYDesc.value
                )
            } else {
                ScaleAnimation(fromXScale ?: 0f, toXScale ?: 0f, fromYScale ?: 0f, toYScale ?: 0f)
            }
            return scaleAnimation

        }
    }

    private class TranslateAnimProperties(value: ObjectValue) : AnimationProperties(value) {
        companion object {
            const val FROM_X_DELTA = "fromXDelta"
            const val TO_X_DELTA = "toXDelta"
            const val FROM_Y_DELTA = "fromYDelta"
            const val TO_Y_DELTA = "toYDelta"
        }

        val fromXDelta: Value? = value[FROM_X_DELTA]
        val toXDelta: Value? = value[TO_X_DELTA]
        val fromYDelta: Value? = value[FROM_Y_DELTA]
        val toYDelta: Value? = value[TO_Y_DELTA]


        override fun createAnimation(c: Context): Animation {
            val fromXDeltaDescription = Description.parseValue(fromXDelta)
            val toXDeltaDescription = Description.parseValue(toXDelta)
            val fromYDeltaDescription = Description.parseValue(fromYDelta)
            val toYDeltaDescription = Description.parseValue(toYDelta)
            return TranslateAnimation(
                fromXDeltaDescription.type,
                fromXDeltaDescription.value,
                toXDeltaDescription.type,
                toXDeltaDescription.value,
                fromYDeltaDescription.type,
                fromYDeltaDescription.value,
                toYDeltaDescription.type,
                toYDeltaDescription.value
            )
        }

    }


    private class RotateAnimProperties(value: ObjectValue) : AnimationProperties(value) {
        companion object {
            const val FROM_DEGREES = "fromDegrees"
            const val TO_DEGREES = "toDegrees"
            const val PIVOT_X = "pivotX"
            const val PIVOT_Y = "pivotY"
        }


        val fromDegrees: Float? = value.getAsFloat(FROM_DEGREES)
        val toDegrees: Float? = value.getAsFloat(TO_DEGREES)
        val pivotX: Value? = value[PIVOT_X]
        val pivotY: Value? = value[PIVOT_Y]


        override fun createAnimation(c: Context): Animation {
            return if (pivotX != null && pivotY != null) {
                val pivotXDesc = Description.parseValue(pivotX)
                val pivotYDesc = Description.parseValue(pivotY)
                RotateAnimation(
                    fromDegrees ?: 0f,
                    toDegrees ?: 0f,
                    pivotXDesc.type,
                    pivotXDesc.value,
                    pivotYDesc.type,
                    pivotYDesc.value
                )
            } else {
                RotateAnimation(fromDegrees ?: 0f, toDegrees ?: 0f)
            }
        }
    }

    private abstract class InterpolatorProperties(value: Value) {
        abstract fun createInterpolator(c: Context): Interpolator
    }

    private class PathInterpolatorProperties(value: ObjectValue) : InterpolatorProperties(value) {

        companion object {
            const val CONTROL_X1 = "controlX1"
            const val CONTROL_Y1 = "controlY1"
            const val CONTROL_X2 = "controlX2"
            const val CONTROL_Y2 = "controlY2"
        }

        val controlX1: Float? = value.getAsFloat(CONTROL_X1)
        val controlY1: Float? = value.getAsFloat(CONTROL_Y1)
        val controlX2: Float? = value.getAsFloat(CONTROL_X2)
        val controlY2: Float? = value.getAsFloat(CONTROL_Y2)


        override fun createInterpolator(c: Context): Interpolator =
            if (controlX2 != null && controlY2 != null) {
                PathInterpolator(controlX1 ?: 0f, controlY1 ?: 0f, controlX2, controlY2)
            } else {
                PathInterpolator(controlX1 ?: 0f, controlY1 ?: 0f)
            }

    }

    private class AnticipateInterpolatorProperties(value: ObjectValue) :
        InterpolatorProperties(value) {
        companion object {
            const val TENSION = "tension"
        }

        val tension: Float? = value.getAsFloat(TENSION)
        override fun createInterpolator(c: Context): Interpolator =
            AnticipateInterpolator(tension ?: 2f)
    }

    private class OvershootInterpolatorProperties(value: ObjectValue) :
        InterpolatorProperties(value) {

        companion object {
            const val TENSION = "tension"
        }

        val tension: Float? = value.getAsFloat(TENSION)

        override fun createInterpolator(c: Context): Interpolator =
            OvershootInterpolator(tension ?: 2f)
    }

    private class AnticipateOvershootInterpolatorProperties(value: ObjectValue) :
        InterpolatorProperties(value) {
        companion object {
            const val TENSION = "tension"
            const val EXTRA_TENSION = "extraTension"
        }

        val tension: Float? = value.getAsFloat(TENSION)
        val extraTension: Float? = value.getAsFloat(EXTRA_TENSION)

        override fun createInterpolator(c: Context): Interpolator = if (tension != null) {
            if (extraTension != null) AnticipateOvershootInterpolator(
                tension, extraTension
            ) else AnticipateOvershootInterpolator(tension)
        } else {
            AnticipateOvershootInterpolator()
        }

    }

    private class CycleInterpolatorProperties(value: ObjectValue) : InterpolatorProperties(value) {
        companion object {
            const val CYCLES = "cycles"
        }

        val cycles: Float? = value.getAsFloat(CYCLES)

        override fun createInterpolator(c: Context): Interpolator = CycleInterpolator(cycles ?: 1f)
    }

}