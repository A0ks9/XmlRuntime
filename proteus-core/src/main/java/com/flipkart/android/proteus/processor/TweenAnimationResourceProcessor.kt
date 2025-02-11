package com.flipkart.android.proteus.processor

import android.util.Log
import android.view.View
import android.view.animation.Animation
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.toolbox.AnimationUtils
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * A concise and functional Kotlin class for processing animation resources for Views.
 *
 * This processor simplifies loading tween animations from resource IDs and applying them to [View]s.
 * It uses a lambda function ([animationSetter]) to define how to apply the animation,
 * making it flexible and avoiding the need for subclassing in many common scenarios.
 *
 * It handles [Value] types that represent animation resource IDs (integers).
 *
 * **Usage Example:**
 *
 * ```kotlin
 * // In your Proteus layout (XML or JSON based), you might have:
 * // <TextView
 * //     android:id="@+id/animatedTextView"
 * //     android:layout_width="wrap_content"
 * //     android:layout_height="wrap_content"
 * //     app:animation="@anim/slide_in_right" // Animation resource defined in XML
 * //     android:text="Hello Animated World!" />
 *
 * // Register ViewAnimationResourceProcessor with a lambda to start the animation:
 * attributeProcessorSet.addProcessor("animation", ViewAnimationResourceProcessor { view, animation ->
 *     view.startAnimation(animation)
 * })
 * ```
 *
 * **Key improvements and explanations in Kotlin:**
 *
 * * **Lambda-based `setAnimation`:**  Instead of abstract class and subclassing, we use a constructor lambda
 *   `animationSetter: (V, Animation) -> Unit` to define how the animation is applied. This is much more concise
 *   for simple cases.
 * * **Concise Syntax:** Kotlin's syntax reduces boilerplate.
 * * **Type Inference:** Kotlin infers types, reducing explicit type declarations.
 * * **Null Safety:** Kotlin's null safety is inherently present. The `animation` variable is checked for null.
 * * **KDoc Comments:** Uses KDoc for documentation, for better readability and tooling support.
 *
 * **Important Notes:**
 *
 * *  This class focuses on handling *tween* animations loaded from XML resources using [AnimationUtils].
 * *  For ViewPropertyAnimator, a different approach might be needed (not directly covered here).
 * *  The `animationSetter` lambda is the core of how animation is applied. You provide this logic when creating the processor.
 * *  Error logging is controlled by `ProteusConstants.isLoggingEnabled()` for production performance.
 * *  The class is generic, working with any [View] subclass.
 * *  [handleResource], [handleAttributeResource], and [handleStyleResource] are empty as they are not used for this specific animation resource handling from simple `Value` types.
 *
 * @param <V> The type of [View] this processor applies to. Must be a subclass of [View].
 * @property animationSetter Lambda function to set/apply the loaded animation to the View.
 *                           Takes the View and the Animation as parameters (View, Animation) -> Unit.
 */
open class TweenAnimationResourceProcessor<V : View>(
    private val animationSetter: (V, Animation) -> Unit
) : AttributeProcessor<V>() {

    companion object {
        private const val TAG = "TweenAnimationResource"
    }

    /**
     * Handles [Value] type attributes. Loads animation from resource ID, applies it using [animationSetter].
     *
     * 1. **Load Animation:** Loads tween animation using [AnimationUtils.loadAnimation].
     * 2. **Apply Animation (if loaded):** If animation is loaded successfully (not null), calls [animationSetter] to apply it.
     * 3. **Error Log (if failed):** Logs error if loading fails and logging is enabled.
     *
     * @param view The [View] to apply animation to.
     * @param value The [Value] representing the animation resource ID.
     */
    override fun handleValue(view: V?, value: Value) {
        val animation = AnimationUtils.loadAnimation(view!!.context, value)
        animation?.let {
            animationSetter(view, it) // Apply animation using the lambda
        } ?: run {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Animation Resource not found or invalid. value -> $value")
            }
        }
    }

    /**
     * Handles [Resource] type attributes (currently empty).
     *
     * @param view The [View].
     * @param resource The [Resource] object.
     */
    override fun handleResource(view: V?, resource: Resource) {
        // Optional: Implement resource-specific handling if needed
    }

    /**
     * Handles [AttributeResource] type attributes (currently empty).
     *
     * @param view The [View].
     * @param attributeResource The [AttributeResource] object.
     */
    override fun handleAttributeResource(view: V?, attributeResource: AttributeResource) {
        // Optional: Implement attribute resource specific handling if needed
    }

    /**
     * Handles [StyleResource] type attributes (currently empty).
     *
     * @param view The [View].
     * @param styleResource The [StyleResource] object.
     */
    override fun handleStyleResource(view: V?, styleResource: StyleResource) {
        // Optional: Implement style resource specific handling if needed
    }
}