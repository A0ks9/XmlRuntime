package com.voyager.utils.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.voyager.data.models.ConfigManager
import com.voyager.utils.ParseHelper.getColor
import com.voyager.utils.ParseHelper.getDrawable
import com.voyager.utils.ParseHelper.parseBoolean
import com.voyager.utils.ParseHelper.parsePorterDuff
import com.voyager.utils.ParseHelper.parseRelativeLayoutBoolean
import com.voyager.utils.Utils.DrawablePosition
import com.voyager.utils.extractViewId
import com.voyager.utils.getParentView
import com.voyager.utils.getViewID
import com.voyager.utils.isBoolean
import com.voyager.utils.isColor
import com.voyager.utils.toLayoutParam
import com.voyager.utils.view.transformation.ReverseTransformation

internal object AttributesHandler {

    private const val TAG = "AttributesHandler"
    private const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
    private val config = ConfigManager.config

    private const val TRANSFORM_ALL_CAPS = "allCaps"
    private const val TRANSFORM_PASSWORD = "password"
    private const val TRANSFORM_SINGLE_LINE = "singleLine"
    private const val TRANSFORM_DIGITS = "digits"
    private const val TRANSFORM_REVERSE = "reverse"

    //make another replacements transformations

    fun setImageSource(view: ImageView, tintSource: String) {
        var imageSource = tintSource
        if (imageSource.startsWith("//")) imageSource = "http:$imageSource"
        if (imageSource.startsWith("http:")) {
            Glide.with(view.context).load(imageSource).into(view)
        } else if (imageSource.startsWith("@drawable/")) {
            val drawable = getDrawable(view, tintSource.removePrefix("@drawable/"))
            if (drawable != null) {
                view.setImageDrawable(drawable)
            } else {
                view.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }
    }

    fun setSize(view: View, tintSource: String, isWidthDimension: Boolean) {
        val layoutParams = (view.layoutParams ?: ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
        val dimension = tintSource.toLayoutParam(
            view.resources.displayMetrics, view.getParentView(), isWidthDimension
        )
        if (isWidthDimension) layoutParams.width = dimension else layoutParams.height = dimension
        view.layoutParams = layoutParams
    }

    fun setMargin(view: View, tintSource: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            tintSource, tintSource, tintSource, tintSource
        )

    fun setMarginLeft(view: View, tintSource: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            tintSource, 0, 0, 0
        )

    fun setMarginRight(view: View, tintSource: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, 0, tintSource, 0
        )

    fun setMarginTop(view: View, tintSource: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, tintSource, 0, 0
        )

    fun setMarginBottom(view: View, tintSource: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, 0, 0, tintSource
        )

    fun setPadding(view: View, tintSource: Int) =
        view.setPadding(tintSource, tintSource, tintSource, tintSource)

    fun setPaddingLeft(view: View, tintSource: Int) = view.setPadding(tintSource, 0, 0, 0)

    fun setPaddingRight(view: View, tintSource: Int) = view.setPadding(0, 0, tintSource, 0)

    fun setPaddingTop(view: View, tintSource: Int) = view.setPadding(0, tintSource, 0, 0)

    fun setPaddingBottom(view: View, tintSource: Int) = view.setPadding(0, 0, 0, tintSource)

    fun addRelativeLayoutRule(
        view: View, tintSource: String, layoutRule: Int,
    ) {
        (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            val anchorId = when {
                tintSource.isBoolean() -> parseRelativeLayoutBoolean(parseBoolean(tintSource))
                else -> view.getParentView()?.getViewID(tintSource.extractViewId())!!
            }
            com.voyager.utils.ParseHelper.addRelativeLayoutRule(view, layoutRule, anchorId)
        }
    }

    fun addConstraintRule(
        layout: ConstraintLayout?,
        view: View,
        tintSource: String,
        constraintEdges: Pair<Int, Int>,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        val sourceId = view.id

        val targetViewId = if (tintSource == "parent") {
            ConstraintSet.PARENT_ID
        } else {
            layout?.getViewID(tintSource.extractViewId()) ?: ConstraintSet.PARENT_ID
        }
        if (config.isLoggingEnabled) Log.d(
            "view", "view: $sourceId, targetId: $targetViewId"
        )

        constraintSet.connect(sourceId, constraintEdges.first, targetViewId, constraintEdges.second)
        constraintSet.applyTo(layout)
    }

    fun setChainStyle(
        layout: ConstraintLayout?, view: View, chainOrientation: Int, tintSource: String,
    ) {
        val styleValue = when (tintSource.lowercase()) {
            "spread" -> ConstraintSet.CHAIN_SPREAD
            "spread_inside" -> ConstraintSet.CHAIN_SPREAD_INSIDE
            "packed" -> ConstraintSet.CHAIN_PACKED
            else -> ConstraintSet.CHAIN_SPREAD
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        if (chainOrientation == ConstraintSet.HORIZONTAL) {
            constraintSet.setHorizontalChainStyle(view.id, styleValue)
        } else if (chainOrientation == ConstraintSet.VERTICAL) {
            constraintSet.setVerticalChainStyle(view.id, styleValue)
        }

        constraintSet.applyTo(layout)
    }

    fun setDimensionRatio(
        layout: ConstraintLayout?, view: View, tintSource: String,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        constraintSet.setDimensionRatio(view.id, tintSource)

        constraintSet.applyTo(layout)
    }

    fun setConstraintLayoutBias(
        layout: ConstraintLayout?,
        view: View,
        isVerticalBias: Boolean,
        tintSource: Float,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        if (isVerticalBias) {
            constraintSet.setVerticalBias(view.id, tintSource)
        } else {
            constraintSet.setHorizontalBias(view.id, tintSource)
        }
        constraintSet.applyTo(layout)
    }

    //handle the background if it was from another package
    fun handleBackground(targetView: View, background: String?, applicationContext: Context) {
        background?.let {
            when {
                it.isColor() -> targetView.setBackgroundColor(
                    getColor(
                        it, applicationContext, Color.TRANSPARENT
                    )
                )

                it.startsWith("@drawable/") -> {
                    getDrawable(
                        targetView, it.removePrefix("@drawable/")
                    )?.let { foregroundDrawable ->
                        targetView.background = foregroundDrawable
                    } ?: run {
                        if (config.isLoggingEnabled) Log.w(TAG, "Drawable resource not found: $it")
                    }
                }

                else -> {
                    if (config.isLoggingEnabled) Log.w(TAG, "Unknown background format: $it")
                    targetView.background = Color.TRANSPARENT.toDrawable()
                }
            }
        }
    }

    fun handleResString(stringSource: String, applicationContext: Context) = when {
        stringSource.startsWith("@string/") -> {
            applicationContext.resources.getString(
                config.provider.getResId(
                    "string", stringSource.removePrefix("@string/")
                )
            )
        }

        //check if it binding

        else -> stringSource
    }

    //handle if the attr from another package
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleForeground(targetView: View, tintSource: String, applicationContext: Context) {
        when {
            tintSource.isColor() -> targetView.foreground =
                getColor(tintSource, applicationContext, Color.TRANSPARENT).toDrawable()

            tintSource.startsWith("?attr") -> {
                val attributeName = tintSource.removePrefix("?attr")
                val attributeId = config.provider.getResId("attr", attributeName)
                if (attributeId != -1) {
                    val foregroundDrawable =
                        getResourceFromAttribute(attributeId, applicationContext) {
                            ContextCompat.getDrawable(
                                applicationContext, it.resourceId
                            )
                        }
                    targetView.foreground = foregroundDrawable
                }
            }

            tintSource.startsWith("@") -> {
                val foregroundDrawable = getDrawableFromResource(tintSource, applicationContext)
                targetView.foreground = foregroundDrawable
                if (foregroundDrawable is AnimationDrawable) foregroundDrawable.start()
            }

            tintSource == "ripple" -> {
                val rippleDrawable = getResourceFromAttribute(
                    android.R.attr.selectableItemBackground, applicationContext
                ) { ContextCompat.getDrawable(applicationContext, it.resourceId) }
                targetView.foreground = rippleDrawable
                targetView.isClickable = true
            }

            else -> targetView.foreground = null
        }
    }

    //handle the attr if it was from another package
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleForegroundTint(targetView: View, tintSource: String, applicationContext: Context) {
        if (tintSource.isEmpty()) {
            targetView.foregroundTintList = null
            return
        }

        when {
            tintSource.isColor() -> targetView.foregroundTintList =
                ColorStateList.valueOf(getColor(tintSource, applicationContext, Color.TRANSPARENT))

            tintSource.startsWith("?attr/") -> {
                val attributeName = tintSource.removePrefix("?attr/")
                val attributeId = config.provider.getResId("attr", attributeName)
                if (attributeId != -1) {
                    targetView.foregroundTintList =
                        getResourceFromAttribute(attributeId, applicationContext) {
                            ColorStateList.valueOf(it.data)
                        }
                }
            }

            else -> throw IllegalArgumentException("Invalid tint format: $tintSource")
        }
    }

    fun loadFontFromAttribute(context: Context, fontName: String): Typeface? {
        return try {
            when (fontName.substringAfter("/")) {
                "sans-serif" -> Typeface.SANS_SERIF
                "serif" -> Typeface.SERIF
                "monospace" -> Typeface.MONOSPACE
                else -> Typeface.createFromAsset(context.assets, "fonts/$fontName.ttf")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun handleTransformationMethod(view: TextView, transformationName: String) {
        when (transformationName) {
            TRANSFORM_ALL_CAPS -> view.isAllCaps = true
            TRANSFORM_PASSWORD -> view.transformationMethod =
                PasswordTransformationMethod.getInstance()

            TRANSFORM_SINGLE_LINE -> view.transformationMethod =
                SingleLineTransformationMethod.getInstance()

            TRANSFORM_DIGITS -> view.keyListener = DigitsKeyListener.getInstance("0123456789")
            TRANSFORM_REVERSE -> view.transformationMethod = ReverseTransformation()
            else -> view.transformationMethod = null
        }
    }

    fun handleScrollbar(view: View, scrollbarType: String) {
        when {
            scrollbarType.equals("h", ignoreCase = true) || scrollbarType.equals(
                "horizontal", ignoreCase = true
            ) -> view.isHorizontalScrollBarEnabled = true

            scrollbarType.equals("v", ignoreCase = true) || scrollbarType.equals(
                "vertical", ignoreCase = true
            ) -> view.isVerticalScrollBarEnabled = true

            else -> throw IllegalArgumentException("Invalid scrollbar type: $scrollbarType")
        }
    }

    fun handleDrawablePosition(
        view: TextView,
        drawableRes: String,
        drawablePosition: DrawablePosition,
    ) {
        val context = view.context
        val drawable = getDrawableFromResource(drawableRes, context)
        val drawables = view.compoundDrawablesRelative

        val updatedDrawables = when (drawablePosition) {
            DrawablePosition.START -> arrayOf(drawable, drawables[1], drawables[2], drawables[3])
            DrawablePosition.END -> arrayOf(drawables[0], drawable, drawables[2], drawables[3])
            DrawablePosition.TOP -> arrayOf(drawables[0], drawables[1], drawable, drawables[3])
            DrawablePosition.BOTTOM -> arrayOf(drawables[0], drawables[1], drawables[2], drawable)
        }

        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            updatedDrawables[0], updatedDrawables[1], updatedDrawables[2], updatedDrawables[3]
        )
    }

    fun handleTint(view: View, colorValue: String) {
        val context = view.context
        val color = getColor(colorValue, context, Color.BLACK)

        when (view) {
            is ImageView -> view.setColorFilter(color)
            is TextView -> applyTextViewTint(view, color)
            else -> Log.w("AttributeHandler", "Tint not supported for ${view::class.java}")
        }
    }

    private fun applyTextViewTint(view: TextView, colorValue: Int) {
        val drawables = view.compoundDrawablesRelative

        val tintedDrawables = drawables.map {
            it?.mutate()?.apply {
                setTint(colorValue)
            }
        }.toTypedArray()

        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            tintedDrawables[0], tintedDrawables[1], tintedDrawables[2], tintedDrawables[3]
        )
    }

    fun handleTintMode(view: View, tintModeValue: String) {
        val tintMode = parsePorterDuff(tintModeValue)

        when (view) {
            is ImageView -> view.imageTintMode = tintMode
            is CheckBox, is RadioButton, is Switch -> view.buttonTintMode = tintMode
            is TextView -> applyTextViewTintMode(view, tintMode)
            else -> Log.w("AttributeHandler", "TintMode not supported for ${view::class.java}")
        }
    }

    private fun applyTextViewTintMode(
        view: TextView,
        mode: PorterDuff.Mode,
    ) {
        val drawables = view.compoundDrawablesRelative
        val tintedDrawables = drawables.map {
            it?.mutate()?.apply {
                setTintMode(mode)
            }
        }.toTypedArray()
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            tintedDrawables[0], tintedDrawables[1], tintedDrawables[2], tintedDrawables[3]
        )
    }

    private inline fun <T> getResourceFromAttribute(
        attributeId: Int,
        applicationContext: Context,
        resourceCallback: (TypedValue) -> T,
    ) = TypedValue().apply {
        applicationContext.theme.resolveAttribute(attributeId, this, true)
    }.let { resourceCallback(it) }


    private fun getDrawableFromResource(resource: String, applicationContext: Context): Drawable? {
        if (!resource.startsWith("@")) return null

        val (type, name) = resource.removePrefix("@").split("/", limit = 2)
        val resourceId = config.provider.getResId(type, name)

        return if (type == "color") {
            ContextCompat.getColor(applicationContext, resourceId).toDrawable()
        } else {
            ContextCompat.getDrawable(applicationContext, resourceId)
        }
    }
}