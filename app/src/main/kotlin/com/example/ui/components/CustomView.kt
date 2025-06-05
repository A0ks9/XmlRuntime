package com.example.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.voyager.annotations.Attribute
import com.voyager.annotations.ViewRegister

/**
 * A custom button implementation that demonstrates how to create custom views
 * that can be used in dynamically inflated XML layouts.
 * 
 * This class shows how to:
 * 1. Register a custom view with Voyager using @ViewRegister
 * 2. Define custom XML attributes using @Attribute
 * 3. Handle custom attribute values in the view
 */
@ViewRegister("CustomButton")
class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    /**
     * Custom attribute to set the button text
     * Can be used in XML as: app:customText="Your Text"
     */
    @Attribute("customText")
    fun setCustomText(text: String) {
        this.text = text
    }

    /**
     * Custom attribute to set the text size
     * Can be used in XML as: app:customTextSize="16"
     */
    @Attribute("customTextSize")
    fun setCustomTextSize(size: Float) {
        this.textSize = size
    }

    /**
     * Custom attribute to control button enabled state
     * Can be used in XML as: app:customEnabled="true"
     */
    @Attribute("customEnabled")
    fun setCustomEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }
}
