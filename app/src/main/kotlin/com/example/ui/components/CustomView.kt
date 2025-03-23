package com.example.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.example.R
import com.voyager.annotations.Attribute
import com.voyager.annotations.ViewRegister


@ViewRegister("CustomButton")
class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    @Attribute("customText")
    fun setCustomText(text: String) {
        this.text = text
    }

    @Attribute("customTextSize")
    fun setCustomTextSize(size: Float) {
        this.textSize = size
    }

    @Attribute("customEnabled")
    fun setCustomEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }
}
