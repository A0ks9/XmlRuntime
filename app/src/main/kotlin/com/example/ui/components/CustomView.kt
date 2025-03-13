package com.example.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.voyager.ksp.annotations.AutoAttribute
import com.voyager.ksp.annotations.AutoViewAttributes


@AutoViewAttributes("CustomButton")
class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    @AutoAttribute
    fun setCustomText(text: String) {
        this.text = text
    }

    @AutoAttribute
    fun setCustomTextSize(size: Float) {
        this.textSize = size
    }

    @AutoAttribute
    fun setCustomEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }
}
