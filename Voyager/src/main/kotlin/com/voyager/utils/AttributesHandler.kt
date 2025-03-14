package com.voyager.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.voyager.utils.ParseHelper.parseRelativeLayoutBoolean

internal object AttributesHandler {

    private const val TAG = "AttributesHandler"
    private const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
    private val config = ConfigManager.config

    fun setImageSource(targetView: ImageView, attributeValue: String) {
        var imageReference = attributeValue
        if (imageReference.startsWith("//")) imageReference = "http:$imageReference"
        if (imageReference.startsWith("http:")) {
            Glide.with(targetView.context).load(imageReference).into(targetView)
        } else if (imageReference.startsWith("@drawable/")) {
            val resDrawable = getDrawable(targetView, attributeValue.removePrefix("@drawable/"))
            if (resDrawable != null) {
                targetView.setImageDrawable(resDrawable)
            } else {
                targetView.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }
    }

    fun setSize(targetView: View, attributeValue: String, isWidth: Boolean) {
        val params = (targetView.layoutParams ?: ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
        val size = attributeValue.toLayoutParam(
            targetView.resources.displayMetrics, targetView.getParentView(), isWidth
        )
        if (isWidth) params.width = size else params.height = size
        targetView.layoutParams = params
    }

    fun setMargin(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            attributeValue, attributeValue, attributeValue, attributeValue
        )

    fun setMarginLeft(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            attributeValue, 0, 0, 0
        )

    fun setMarginRight(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, 0, attributeValue, 0
        )

    fun setMarginTop(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, attributeValue, 0, 0
        )

    fun setMarginBottom(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, 0, 0, attributeValue
        )

    fun setPadding(targetView: View, attributeValue: Int) =
        targetView.setPadding(attributeValue, attributeValue, attributeValue, attributeValue)

    fun setPaddingLeft(targetView: View, attributeValue: Int) =
        targetView.setPadding(attributeValue, 0, 0, 0)

    fun setPaddingRight(targetView: View, attributeValue: Int) =
        targetView.setPadding(0, 0, attributeValue, 0)

    fun setPaddingTop(targetView: View, attributeValue: Int) =
        targetView.setPadding(0, attributeValue, 0, 0)

    fun setPaddingBottom(targetView: View, attributeValue: Int) =
        targetView.setPadding(0, 0, 0, attributeValue)

    fun addRelativeLayoutRule(
        targetView: View, attributeValue: String, relativeLayoutRule: Int,
    ) {
        (targetView.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            val anchor = when {
                attributeValue.isBoolean() -> parseRelativeLayoutBoolean(parseBoolean(attributeValue))
                else -> targetView.getParentView()?.getViewID(attributeValue.extractViewId())!!
            }
            ParseHelper.addRelativeLayoutRule(targetView, relativeLayoutRule, anchor)
        }
    }

    fun addConstraintRule(
        constraint: ConstraintLayout?,
        targetView: View,
        attributeValue: String,
        constraintSides: Pair<Int, Int>,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)

        val viewId = targetView.id

        val targetId = if (attributeValue == "parent") {
            ConstraintSet.PARENT_ID
        } else {
            constraint?.getViewID(attributeValue.extractViewId()) ?: ConstraintSet.PARENT_ID
        }
        if (config.isLoggingEnabled) Log.d(
            "ViewAttributes", "targetView: $viewId, targetId: $targetId"
        )

        constraintSet.connect(viewId, constraintSides.first, targetId, constraintSides.second)
        constraintSet.applyTo(constraint)
    }

    fun setChainStyle(
        constraint: ConstraintLayout?, targetView: View, orientation: Int, attributeValue: String,
    ) {
        val chainStyle = when (attributeValue.lowercase()) {
            "spread" -> ConstraintSet.CHAIN_SPREAD
            "spread_inside" -> ConstraintSet.CHAIN_SPREAD_INSIDE
            "packed" -> ConstraintSet.CHAIN_PACKED
            else -> ConstraintSet.CHAIN_SPREAD
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)

        if (orientation == ConstraintSet.HORIZONTAL) {
            constraintSet.setHorizontalChainStyle(targetView.id, chainStyle)
        } else if (orientation == ConstraintSet.VERTICAL) {
            constraintSet.setVerticalChainStyle(targetView.id, chainStyle)
        }

        constraintSet.applyTo(constraint)
    }

    fun setDimensionRatio(
        constraint: ConstraintLayout?, targetView: View, attributeValue: String,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)

        constraintSet.setDimensionRatio(targetView.id, attributeValue)

        constraintSet.applyTo(constraint)
    }

    fun setConstraintLayoutBias(
        constraintLayout: ConstraintLayout?,
        targetView: View,
        isVertical: Boolean,
        attributeValue: Float,
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        if (isVertical) {
            constraintSet.setVerticalBias(targetView.id, attributeValue)
        } else {
            constraintSet.setHorizontalBias(targetView.id, attributeValue)
        }
        constraintSet.applyTo(constraintLayout)
    }

    //handle the background if it was from another package
    fun handleBackground(view: View, backgroundValue: String?, context: Context) {
        backgroundValue?.let {
            when {
                it.isColor() -> view.setBackgroundColor(getColor(it, context, Color.TRANSPARENT))
                it.startsWith("@drawable/") -> {
                    getDrawable(view, it.removePrefix("@drawable/"))?.let { drawable ->
                        view.background = drawable
                    } ?: run {
                        if (config.isLoggingEnabled) Log.w(TAG, "Drawable resource not found: $it")
                    }
                }

                else -> {
                    if (config.isLoggingEnabled) Log.w(TAG, "Unknown background format: $it")
                    view.background = Color.TRANSPARENT.toDrawable()
                }
            }
        }
    }

    fun handleResString(resString: String, context: Context) = when {
        resString.startsWith("@string/") -> {
            context.resources.getString(
                config.provider.getResId(
                    "string", resString.removePrefix("@string/")
                )
            )
        }

        //check if it binding

        else -> resString
    }

    //handle if the attr from another package
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleForeground(view: View, attributeValue: String, context: Context) {
        when {
            attributeValue.isColor() -> view.foreground =
                getColor(attributeValue, context, Color.TRANSPARENT).toDrawable()

            attributeValue.startsWith("?attr") -> {
                val attrName = attributeValue.removePrefix("?attr")
                val attrId = config.provider.getResId("attr", attrName)
                if (attrId != -1) {
                    val drawable = getResourceFromAttribute(attrId, context) {
                        ContextCompat.getDrawable(
                            context, it.resourceId
                        )
                    }
                    view.foreground = drawable
                }
            }

            attributeValue.startsWith("@") -> {
                val drawable = getDrawableFromResource(attributeValue, context)
                view.foreground = drawable
                if (drawable is AnimationDrawable) drawable.start()
            }

            attributeValue == "ripple" -> {
                val ripple = getResourceFromAttribute(
                    android.R.attr.selectableItemBackground, context
                ) { ContextCompat.getDrawable(context, it.resourceId) }
                view.foreground = ripple
                view.isClickable = true
            }

            else -> view.foreground = null
        }
    }

    //handle the attr if it was from another package
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleForegroundTint(view: View, attributeValue: String, context: Context) {
        if (attributeValue.isEmpty()) {
            view.foregroundTintList = null
            return
        }

        when {
            attributeValue.isColor() -> view.foregroundTintList =
                ColorStateList.valueOf(getColor(attributeValue, context, Color.TRANSPARENT))

            attributeValue.startsWith("?attr/") -> {
                val attrName = attributeValue.removePrefix("?attr/")
                val attrId = config.provider.getResId("attr", attrName)
                if (attrId != -1) {
                    view.foregroundTintList = getResourceFromAttribute(attrId, context) {
                        ColorStateList.valueOf(it.data)
                    }
                }
            }

            else -> throw IllegalArgumentException("Invalid tint format: $attributeValue")
        }
    }

    private inline fun <T> getResourceFromAttribute(
        attrId: Int,
        context: Context,
        callback: (TypedValue) -> T,
    ) = TypedValue().apply {
        context.theme.resolveAttribute(attrId, this, true)
    }.let { callback(it) }


    private fun getDrawableFromResource(resourceString: String, context: Context): Drawable? {
        if (!resourceString.startsWith("@")) return null

        val (type, name) = resourceString.removePrefix("@").split("/", limit = 2)
        val resId = config.provider.getResId(type, name)

        return if (type == "color") {
            ContextCompat.getColor(context, resId).toDrawable()
        } else {
            ContextCompat.getDrawable(context, resId)
        }
    }
}