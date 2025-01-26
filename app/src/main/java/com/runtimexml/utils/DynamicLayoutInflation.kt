package com.runtimexml.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.runtimexml.utils.DimensionConvertor.stringToDimension
import com.runtimexml.utils.DimensionConvertor.stringToDimensionPixelSize
import com.runtimexml.utils.UtilsKt.convertXml
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

object DynamicLayoutInflation {

    private val CORNERS = listOf("TopLeft", "TopRight", "BottomLeft", "BottomRight")

    @JvmStatic
    fun inflateJson(context: Context, uri: Uri, parent: ViewGroup?): View? = try {
        convertXml(context.contentResolver, uri)?.let { createViews(context, it, parent) }
    } catch (e: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", e)
        null
    }

    private fun createViews(context: Context, json: JSONObject, parent: ViewGroup?): View? {
        val view = getViewForName(context, json.getString("type")) ?: return null
        val children = json.optJSONArray("children")
        applyAttributes(view, getAttrsMap(json.getJSONObject("attributes")), parent)
        if ((children?.length() ?: 0) > 0 && view is ViewGroup) {
            parseChildren(context, children, view)
        }
        return view
    }

    private fun parseChildren(
        context: Context, array: JSONArray?, parent: ViewGroup
    ) {
        array?.let {
            for (i in 0 until it.length()) {
                val child = it.getJSONObject(i)
                createViews(context, child, parent)?.let { childView ->
                    parent.addView(childView)
                }
            }
        }
    }

    private fun getViewForName(context: Context, name: String): View? = try {
        val modifiedName = if (!name.contains(".")) "android.widget.$name" else name
        val c = Class.forName(modifiedName)
        c.getConstructor(Context::class.java).newInstance(context) as? View
    } catch (e: Exception) {
        Log.e("DynamicLayoutInflation", "Error getting view for name", e)
        null
    }

    private fun getAttrsMap(jsonObject: JSONObject): HashMap<String, String> {
        return HashMap<String, String>(jsonObject.length()).apply {
            for (name in jsonObject.keys()) {
                val attrName = name.removePrefix("android:")
                val attrValue = jsonObject.optString(name)
                put(attrName, attrValue)
            }
        }
    }

    private fun applyAttributes(view: View?, attrs: HashMap<String, String>, parent: ViewGroup?) {
        if (view == null) return
        val viewParams = view.layoutParams
        var layoutRule = 0
        var marginLeft = 0
        var marginRight = 0
        var marginTop = 0
        var marginBottom = 0
        var paddingLeft = 0
        var paddingRight = 0
        var paddingTop = 0
        var paddingBottom = 0
        var hasCornerRadius = false
        var hasCornerRadii = false

        for ((key, value) in attrs) {
            ViewProperties.applyProperty(view, key, value, parent, attrs)
            if (ViewProperties.viewRunnables.containsKey(key)) continue

            when {
                key.startsWith("cornerRadius") -> {
                    hasCornerRadius = true
                    hasCornerRadii = !key.equals("cornerRadius", ignoreCase = true)
                    continue
                }

                key == "id" -> {
                    val id = parseID(value)
                    if (parent != null) {
                        val generatedView: GeneratedView = parent.getGeneratedViewInfo()
                        val newID = View.generateViewId()
                        view.id = newID
                        generatedView.viewID[id] = newID
                    }
                }

                key in listOf("width", "layout_width") -> {
                    viewParams?.width = when (value) {
                        "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                        else -> stringToDimensionPixelSize(
                            value, view.resources.displayMetrics, parent, true
                        )
                    }
                }

                key in listOf("height", "layout_height") -> {
                    viewParams?.height = when (value) {
                        "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                        else -> stringToDimensionPixelSize(
                            value, view.resources.displayMetrics, parent, false
                        )
                    }
                }

                key == "layout_gravity" -> {
                    val gravity = parseGravity(value)
                    when (parent) {
                        is LinearLayout -> (viewParams as LinearLayout.LayoutParams).gravity =
                            gravity

                        is FrameLayout -> (viewParams as FrameLayout.LayoutParams).gravity = gravity
                    }
                }

                key == "layout_weight" && parent is LinearLayout -> {
                    (viewParams as LinearLayout.LayoutParams).weight = value.toFloat()
                }

                key == "layout_below" -> {
                    layoutRule = RelativeLayout.BELOW
                }

                key == "layout_above" -> {
                    layoutRule = RelativeLayout.ABOVE
                }

                key == "layout_toLeftOf" -> {
                    layoutRule = RelativeLayout.LEFT_OF
                }

                key == "layout_toRightOf" -> {
                    layoutRule = RelativeLayout.RIGHT_OF
                }

                key == "layout_alignBottom" -> {
                    layoutRule = RelativeLayout.ALIGN_BOTTOM
                }

                key == "layout_alignTop" -> {
                    layoutRule = RelativeLayout.ALIGN_TOP
                }

                key == "layout_alignLeft" -> {
                    layoutRule = RelativeLayout.ALIGN_LEFT
                }

                key == "layout_alignStart" -> {
                    layoutRule = RelativeLayout.ALIGN_START
                }

                key == "layout_alignRight" -> {
                    layoutRule = RelativeLayout.ALIGN_RIGHT
                }

                key == "layout_alignEnd" -> {
                    layoutRule = RelativeLayout.ALIGN_END
                }

                key == "layout_alignParentBottom" -> layoutRule = RelativeLayout.ALIGN_PARENT_BOTTOM

                key == "layout_alignParentTop" -> layoutRule = RelativeLayout.ALIGN_PARENT_TOP
                key == "layout_alignParentLeft" -> layoutRule = RelativeLayout.ALIGN_PARENT_LEFT

                key == "layout_alignParentStart" -> layoutRule = RelativeLayout.ALIGN_PARENT_START

                key == "layout_alignParentRight" -> layoutRule = RelativeLayout.ALIGN_PARENT_RIGHT

                key == "layout_alignParentEnd" -> layoutRule = RelativeLayout.ALIGN_PARENT_END

                key == "layout_centerHorizontal" -> layoutRule = RelativeLayout.CENTER_HORIZONTAL

                key == "layout_centerVertical" -> layoutRule = RelativeLayout.CENTER_VERTICAL

                key == "layout_centerInParent" -> layoutRule = RelativeLayout.CENTER_IN_PARENT
                key == "layout_margin" -> {
                    val margin = stringToDimensionPixelSize(value, view.resources.displayMetrics)
                    marginLeft = margin
                    marginRight = margin
                    marginTop = margin
                    marginBottom = margin
                }

                key in listOf("layout_marginLeft", "layout_marginStart") -> marginLeft =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics, parent, true)


                key in listOf("layout_marginRight", "layout_marginEnd") -> marginRight =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics, parent, true)

                key == "layout_marginTop" -> marginTop = stringToDimensionPixelSize(
                    value, view.resources.displayMetrics, parent, false
                )

                key == "layout_marginBottom" -> marginBottom = stringToDimensionPixelSize(
                    value, view.resources.displayMetrics, parent, false
                )

                key == "padding" -> {
                    val padding = stringToDimensionPixelSize(value, view.resources.displayMetrics)
                    paddingLeft = padding
                    paddingRight = padding
                    paddingTop = padding
                    paddingBottom = padding
                }

                key in listOf("paddingStart", "paddingLeft") -> paddingLeft =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics)

                key in listOf("paddingEnd", "paddingRight") -> paddingRight =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics)

                key == "paddingTop" -> paddingTop =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics)

                key == "paddingBottom" -> paddingBottom =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics)
            }

            if (layoutRule != -999 && parent is RelativeLayout) {
                val anchor = if (listOf(
                        "layout_below",
                        "layout_above",
                        "layout_toLeftOf",
                        "layout_toRightOf",
                        "layout_alignBottom",
                        "layout_alignTop",
                        "layout_alignLeft",
                        "layout_alignStart",
                        "layout_alignRight",
                        "layout_alignEnd"
                    ).contains(key)
                ) {
                    getViewID(parent, parseID(value))
                } else -1

                (viewParams as RelativeLayout.LayoutParams).apply {
                    if (anchor > -1) addRule(layoutRule, anchor)
                    else if (value.equals("true", ignoreCase = true)) addRule(layoutRule)
                }
            }
            if (attrs.containsKey("background") || attrs.containsKey("borderColor")) {
                val bgValue = attrs["background"]
                if (bgValue?.startsWith("@drawable/") == true) {
                    view.background = getDrawable(view, bgValue.removePrefix("@drawable/"))
                } else if (bgValue?.startsWith("#") == true || bgValue?.startsWith("@color/") == true) {
                    val backgroundColor = parseColor(view, bgValue)
                    if (view is Button || attrs.containsKey("pressedColor")) {
                        val pressedColor =
                            attrs["pressedColor"]?.let { parseColor(view, it) } ?: adjustBrightness(
                                backgroundColor,
                                0.9f
                            )
                        view.background = createBackgroundDrawable(
                            view,
                            backgroundColor,
                            pressedColor,
                            attrs,
                            hasCornerRadii,
                            hasCornerRadius
                        )
                    } else {
                        view.background = createBackgroundDrawable(
                            view,
                            backgroundColor,
                            backgroundColor,
                            attrs,
                            hasCornerRadii,
                            hasCornerRadius
                        )
                    }
                }
            }
        }

        if (viewParams is ViewGroup.MarginLayoutParams) viewParams.setMargins(
            marginLeft,
            marginTop,
            marginRight,
            marginBottom
        )
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        view.layoutParams = viewParams
    }

    private fun createBackgroundDrawable(
        view: View,
        backgroundColor: Int,
        pressedColor: Int,
        attrs: Map<String, String>,
        hasCornerRadii: Boolean,
        hasCornerRadius: Boolean
    ): Drawable {
        val gradientDrawable =
            GradientDrawable().apply { color = ColorStateList.valueOf(backgroundColor) }
        val pressedGradientDrawable =
            GradientDrawable().apply { color = ColorStateList.valueOf(pressedColor) }

        if (hasCornerRadii) {
            val radii = FloatArray(8)
            for (i in 0 until CORNERS.size) {
                val corner = CORNERS[i]
                if (attrs.containsKey("cornerRadius$corner")) {
                    val radius = stringToDimension(
                        attrs["cornerRadius$corner"].toString(),
                        view.resources.displayMetrics
                    )
                    radii[i * 2] = radius
                    radii[i * 2 + 1] = radius
                }
                gradientDrawable.cornerRadii = radii
                pressedGradientDrawable.cornerRadii = radii
            }
        } else if (hasCornerRadius) {
            val radius =
                stringToDimension(attrs["cornerRadius"].toString(), view.resources.displayMetrics)
            gradientDrawable.cornerRadius = radius
            pressedGradientDrawable.cornerRadius = radius
        }
        if (attrs.containsKey("borderColor")) {
            val borderWidth = attrs["borderWidth"] ?: "1dp"
            val borderWidthPixel =
                stringToDimensionPixelSize(borderWidth, view.resources.displayMetrics)

            val borderColor = parseColor(view, attrs["borderColor"].toString())

            gradientDrawable.setStroke(borderWidthPixel, borderColor)
            pressedGradientDrawable.setStroke(borderWidthPixel, borderColor)
        }
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), pressedGradientDrawable)
            addState(intArrayOf(), gradientDrawable)
        }
    }

    private fun parseColor(view: View?, string: String?): Int = when {
        string == null -> throw IllegalStateException("Color of background cannot be null")
        string.length == 4 && string.startsWith("#") -> Color.parseColor("#${string[1]}${string[1]}${string[2]}${string[2]}${string[3]}${string[3]}")
        else -> Color.parseColor(string)
    }

    private fun adjustBrightness(color: Int, amount: Float): Int {
        return (color and 0xFF0000 shr 16) * amount.toInt() shl 16 or (color and 0x00FF00 shr 8) * amount.toInt() shl 8 or (color and 0x0000FF) * amount.toInt()
    }

    private fun getDrawable(view: View, name: String): Drawable? {
        val resources = view.resources
        val resId = resources.getIdentifier(name, "drawable", view.context.packageName)
        return ResourcesCompat.getDrawable(resources, resId, null)
    }

    private fun parseGravity(value: String): Int {
        var gravity = Gravity.NO_GRAVITY
        value.lowercase().split("[|]".toRegex()).forEach { part ->
            gravity = gravity or when (part) {
                "center" -> Gravity.CENTER
                "left" -> Gravity.LEFT
                "start" -> Gravity.START
                "right" -> Gravity.RIGHT
                "end" -> Gravity.END
                "top" -> Gravity.TOP
                "bottom" -> Gravity.BOTTOM
                "center_horizontal" -> Gravity.CENTER_HORIZONTAL
                "center_vertical" -> Gravity.CENTER_VERTICAL
                else -> Gravity.NO_GRAVITY // Default value
            }
        }
        return gravity
    }

    private fun parseID(id: String): String = id.removePrefix("@+id/").removePrefix("@id/")

    private fun getClickListener(parent: ViewGroup?, methodName: String): View.OnClickListener =
        View.OnClickListener { view ->
            var root = parent
            var generatedView: GeneratedView? = null
            while (root != null && (root.parent is ViewGroup)) {
                if (root.tag is GeneratedView) {
                    generatedView = root.tag as GeneratedView
                    if (generatedView.delegate != null) break
                }
                root = root.parent as? ViewGroup
            }
            generatedView?.delegate?.let { delegate ->
                invokeMethod(delegate, methodName, false, view)
            }
        }


    private fun invokeMethod(delegate: Any?, methodName: String, withView: Boolean, view: View?) {
        var args: Array<Any>? = null
        var finalMethod = methodName
        if (methodName.endsWith(")")) {
            val parts = methodName.split("[(]".toRegex(), 2)
            finalMethod = parts[0]
            try {
                val argText = parts[1].replace("&quot;", "\"")
                val arr = JSONArray("[${argText.substring(0, argText.length - 1)}]")
                args = Array(arr.length()) { arr.get(it) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (withView && view != null) {
            args = arrayOf(view)
        }
        val klass = delegate?.javaClass
        try {
            val argClasses = args?.map { arg ->
                var argClass = arg::class.java
                if (argClass == Integer::class.java) {
                    argClass = Int::class.java
                }
                argClass
            }?.toTypedArray()

            val method = if (argClasses == null) {
                klass?.getMethod(finalMethod)
            } else {
                klass?.getMethod(finalMethod, *argClasses)
            }
            method?.invoke(delegate, *(args ?: emptyArray()))

        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            if (!withView && !methodName.endsWith(")")) {
                invokeMethod(delegate, methodName, true, view)
            }
        }
    }

    private fun setDelegate(root: View, delegate: Any) {
        val generatedView = root.getGeneratedViewInfo()
        generatedView.delegate = delegate
    }

    private fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }

    private fun getViewID(root: View, id: String): Int {
        if (root !is ViewGroup) return -1
        return (root.tag as? GeneratedView)?.viewID?.get(id) ?: root.childrenSequence()
            .mapNotNull { getViewID(it, id).takeIf { it > -1 } }.firstOrNull() ?: -1

    }

    private fun ViewGroup.childrenSequence(): Sequence<View> = object : Sequence<View> {
        override fun iterator(): Iterator<View> = object : Iterator<View> {
            private var index = 0
            override fun hasNext(): Boolean = index < childCount

            override fun next(): View = getChildAt(index++)
        }
    }

    fun View.findViewByIdString(id: String): View? {
        val idNum = getViewID(this, id)
        return if (idNum < 0) null else findViewById(idNum)
    }

    class GeneratedView {
        var viewID = HashMap<String, Int>()
        var delegate: Any? = null
        var bgDrawable: GradientDrawable? = null
    }

}