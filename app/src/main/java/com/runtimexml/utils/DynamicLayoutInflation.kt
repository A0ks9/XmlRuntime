package com.runtimexml.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.runtimexml.utils.Attributes.Common
import com.runtimexml.utils.FileHelper.convertXml
import com.runtimexml.utils.FileHelper.getFileExtension
import com.runtimexml.utils.interfaces.ViewParamAction
import com.runtimexml.utils.processors.AttributeProcessor
import com.runtimexml.utils.processors.ViewProcessor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import com.runtimexml.utils.Attributes.View as ViewAttribute

/**
 * Object that handles the dynamic inflation of layouts from JSON or XML resources.
 * It provides methods to parse the structure and apply attributes to the views, as well as adding them to the view hierarchy
 */
object DynamicLayoutInflation {
    // Constant for layout rule when there is no rule is applied.
    private const val NO_LAYOUT_RULE = -999

    // List of corner names for drawing rounded corners.
    private val CORNERS = listOf("TopLeft", "TopRight", "BottomLeft", "BottomRight")

    // A map to store different actions that can be applied to views based on properties
    private val ViewProperties = mutableMapOf<String, ViewParamAction>()

    // Initialization block where properties actions are created and added to ViewProperties map
    init {
        BaseViewAttributes.initializeAttributes()
        createViewProperties()
    }

    /**
     * Inflates a layout from a JSON or XML resource URI.
     *
     * @param context The context used to access resources.
     * @param uri The URI of the JSON or XML resource.
     * @param parent The parent ViewGroup that will contain the inflated views.
     * @return The root View of the inflated layout or null if an error occurs.
     */
    @JvmStatic
    fun inflateJson(context: Context, uri: Uri, parent: ViewGroup?): View? = try {
        when (getFileExtension(context, uri)?.lowercase()) {
            "json" -> context.contentResolver.openInputStream(uri)?.use { inputStream ->
                createViews(context, JSONObject(inputStream.bufferedReader().readText()), parent)
            }

            "xml" -> convertXml(context.contentResolver, uri)?.let {
                createViews(
                    context, it, parent
                )
            }

            else -> null // If file type is not JSON or XML.
        }
    } catch (e: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", e)
        null //If exception is encountered then return null.
    }

    /**
     * Inflates a layout from a JSON object.
     *
     * @param context The context used to access resources.
     * @param json The JSON object representing the layout.
     * @param parent The parent ViewGroup that will contain the inflated views.
     * @return The root View of the inflated layout or null if an error occurs.
     */
    @JvmStatic
    fun inflateJson(context: Context, json: JSONObject, parent: ViewGroup?): View? = try {
        createViews(context, json, parent)
    } catch (e: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", e)
        null //If exception is encountered then return null.
    }

    /**
     * Creates the views dynamically from a JSON object.
     * @param context The Context used to access resources.
     * @param json The JSON object representing the view.
     * @param parent The parent ViewGroup that will contain the created views
     * @return The created View, null if the View was not created successfully
     */
    private fun createViews(context: Context, json: JSONObject, parent: ViewGroup?): View? =
        getViewForName(context, json.getString("type"))?.apply {
            //Apply attributes from JSON to the newly created view
            applyAttributes(this, getAttrsMap(json.getJSONObject("attributes")), parent)
            //add View to parent
            parent?.addView(this)

            if (this is ViewGroup && (json.optJSONArray("children")?.length() ?: 0) > 0) {
                //If the view has children then parse these children.
                parseChildren(context, json.optJSONArray("children"), this)
            }
        }

    /**
     * Parses the child views and adds them to the parent.
     * @param context The Context used to access resources.
     * @param array The JSON array of child views.
     * @param parent The parent view where the children should be added.
     */
    private fun parseChildren(
        context: Context, array: JSONArray?, parent: ViewGroup
    ) {
        array?.let {
            for (i in 0 until it.length()) {
                val child = it.getJSONObject(i)
                createViews(context, child, parent)
            }
        }
    }

    /**
     * Get an Android View from the name
     * @param context The context used to access the resources
     * @param name  The name of the View, such as TextView, LinearLayout.
     * @return The created view or null if an exception is found.
     */
    private fun getViewForName(context: Context, name: String): View? = try {
        if (ViewProcessor.isRegistered(name)) ViewProcessor.createView(name, context)

        val modifiedName = if (!name.contains(".")) "android.widget.$name" else name
        // Create a class object from the name
        Class.forName(modifiedName).getConstructor(Context::class.java)
            .newInstance(context) as? View
    } catch (e: Exception) {
        Log.e("DynamicLayoutInflation", "Error getting view for name", e)
        null // Returns null when an exception happens.
    }

    /**
     * Get a Map of attributes that can be applied to the View.
     * @param jsonObject the jsonObject that represents view's attributes
     * @return HashMap that contains the key and values of attributes.
     */
    private fun getAttrsMap(jsonObject: JSONObject): HashMap<String, String> =
        HashMap<String, String>(jsonObject.length()).apply {
            jsonObject.keys().forEach {
                // Put all the attributes from json object into a map.
                put(it.removePrefix("android:").removePrefix("app:"), jsonObject.optString(it))
            }
        }

    /**
     * Applies attributes to a given view.
     * @param view The View to apply attributes to.
     * @param attrs A map of attribute names to their values.
     * @param parent The parent ViewGroup of the view.
     */
    private fun applyAttributes(view: View, attrs: HashMap<String, String>, parent: ViewGroup?) {
        //Inside applyAttributes Function
        val viewParams = view.layoutParams ?: if (parent is LinearLayout) LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        else RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        Log.d(
            "DynamicLayoutInflation",
            "Before apply attributes, View: ${view.javaClass.simpleName},  width: ${viewParams.width}, height: ${viewParams.height}, parent: ${parent?.javaClass?.simpleName}"
        )

        //Iterate through each attributes
        for ((key, value) in attrs) {

            //First try to find the ViewParamAction in ViewProperties map.
            ViewProperties[key]?.apply(view, value, parent, attrs)
            //If a viewAction exists in the viewProperties map then skip the rest of the checks.
            if (ViewProperties.containsKey(key)) continue
            AttributeProcessor.applyAttribute(view, key, value)
        }

        Log.d(
            "DynamicLayoutInflation",
            "After applying attributes, View: ${view.javaClass.simpleName}, width: ${viewParams.width}, height: ${viewParams.height}"
        )
    }

    /**
     * Creates a background Drawable with optional corners and border
     * @param view  The view in which the background will be drawn.
     * @param backgroundColor the color of the background.
     * @param pressedColor  the pressed color of background, mainly for buttons.
     * @param attrs  The map contains all attributes.
     * @param hasCornerRadii if it contains  corner radius for each corners.
     * @param hasCornerRadius if it contains radius for all corner.
     * @return The created drawable object.
     */
    private fun createBackgroundDrawable(
        view: View,
        backgroundColor: Int,
        pressedColor: Int,
        attrs: Map<String, String>,
        hasCornerRadii: Boolean,
        hasCornerRadius: Boolean
    ): Drawable = GradientDrawable().apply { color = ColorStateList.valueOf(backgroundColor) }
        .let { gradientDrawable ->
            GradientDrawable().apply { color = ColorStateList.valueOf(pressedColor) }
                .let { pressedGradientDrawable ->
                    if (hasCornerRadii) {
                        val radii = FloatArray(8)
                        for (i in 0 until CORNERS.size) {
                            val corner = CORNERS[i]
                            if (attrs.containsKey("cornerRadius$corner")) {
                                val radius = attrs["cornerRadius$corner"].toString().toPixels(
                                    view.resources.displayMetrics
                                ) as Float
                                radii[i * 2] = radius
                                radii[i * 2 + 1] = radius
                            }
                            gradientDrawable.cornerRadii = radii
                            pressedGradientDrawable.cornerRadii = radii
                        }
                    } else if (hasCornerRadius) {
                        val radius = attrs["cornerRadius"].toString().toPixels(
                            view.resources.displayMetrics
                        ) as Float
                        gradientDrawable.cornerRadius = radius
                        pressedGradientDrawable.cornerRadius = radius
                    }
                    if (attrs.containsKey("borderColor")) {
                        val borderWidth = attrs["borderWidth"] ?: "1dp"
                        val borderWidthPixel =
                            borderWidth.toPixels(view.resources.displayMetrics) as Int
                        val borderColor = parseColor(view, attrs["borderColor"].toString())
                        gradientDrawable.setStroke(borderWidthPixel, borderColor)
                        pressedGradientDrawable.setStroke(borderWidthPixel, borderColor)
                    }
                    StateListDrawable().apply {
                        if (view is Button || attrs.containsKey("pressedColor")) addState(
                            intArrayOf(
                                android.R.attr.state_pressed
                            ), pressedGradientDrawable
                        )
                        addState(intArrayOf(), gradientDrawable)
                    }
                }
        }

    /**
     * Parses a color string into an int color value.
     * @param view Used for context.
     * @param string A string that represents a color, can be hex or a resource.
     * @return the parsed color in Int.
     * @throws IllegalStateException if the color string is null.
     */
    private fun parseColor(view: View?, string: String?): Int = when {
        string == null -> throw IllegalStateException("Color of background cannot be null")
        string.length == 4 && string.startsWith("#") -> Color.parseColor("#${string[1]}${string[1]}${string[2]}${string[2]}${string[3]}${string[3]}")
        else -> Color.parseColor(string)
    }

    /**
     * Adjusts the brightness of a given color.
     * @param color The int color value.
     * @param amount The amount that should be used for brightness adjustment.
     * @return  The adjusted color value.
     */
    private fun adjustBrightness(color: Int, amount: Float): Int =
        (color and 0xFF0000 shr 16) * amount.toInt() shl 16 or (color and 0x00FF00 shr 8) * amount.toInt() shl 8 or (color and 0x0000FF) * amount.toInt()

    /**
     * Get a drawable by name, from resources.
     * @param view used for accessing resources.
     * @param name The name of the drawable (without prefix).
     * @return The Drawable object.
     */
    @SuppressLint("DiscouragedApi")
    private fun getDrawable(view: View, name: String): Drawable? = view.resources.run {
        ResourcesCompat.getDrawable(
            this, getIdentifier(name, "drawable", view.context.packageName), null
        )
    }

    /**
     *  Parses gravity attributes from a string.
     * @param value The string to parse.
     * @return The combined Gravity value (e.g. Gravity.START or Gravity.TOP)
     */
    private fun parseGravity(value: String): Int {
        var gravity = Gravity.NO_GRAVITY
        value.lowercase().split("[|]".toRegex()).forEach {
            gravity = gravity or when (it) {
                "center" -> Gravity.CENTER
                "left", "start" -> Gravity.START
                "right", "end" -> Gravity.END
                "top" -> Gravity.TOP
                "bottom" -> Gravity.BOTTOM
                "center_horizontal" -> Gravity.CENTER_HORIZONTAL
                "center_vertical" -> Gravity.CENTER_VERTICAL
                else -> Gravity.NO_GRAVITY
            }
        }
        return gravity
    }

    /**
     * Parses ID string by removing prefix
     * @param id The string id to parse.
     * @return the parsed id.
     */
    private fun parseID(id: String): String = id.removePrefix("@+id/")

    /**
     * Get a click listener.
     * @param parent The parent view, for obtaining delegate.
     * @param methodName The name of method to call when the button is clicked.
     * @return The OnClickListener.
     */
    private fun getClickListener(parent: ViewGroup?, methodName: String): View.OnClickListener =
        View.OnClickListener { view ->
            (parent?.getGeneratedViewInfo()?.delegate)?.let {
                invokeMethod(it, methodName, false, view)
            }
        }

    /**
     * Invokes a method using reflection.
     * @param delegate The object that contains the method.
     * @param methodName The name of method.
     * @param withView  If it needs to pass View as a parameter.
     * @param view The view, if needs to be passed as parameter.
     */
    private fun invokeMethod(delegate: Any?, methodName: String, withView: Boolean, view: View?) {
        var args: Array<Any>? = null
        var finalMethod = methodName
        if (methodName.endsWith(")")) {
            val parts = methodName.split("[(]".toRegex(), 2)
            finalMethod = parts[0]
            try {
                val argText = parts[1].replace("&quot;", "\"")
                val arr = JSONArray("[$argText]".dropLast(1))
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
                if (argClass == Integer::class.java) argClass = Int::class.java
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

    /**
     * Sets the delegate object for a view.
     * @param root The View for which to set the delegate.
     * @param delegate  The delegate object to set.
     */
    @JvmStatic
    fun setDelegate(root: View?, delegate: Any) {
        root?.getGeneratedViewInfo()?.delegate = delegate
    }

    /**
     * Gets or creates a `GeneratedView` associated with a View.
     * If the View has a tag of type `GeneratedView` it returns that,
     * otherwise it creates a new `GeneratedView` instance and stores it as a tag.
     *
     * @return The generated view or the tag value as `GeneratedView`
     */
    internal fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }


    /**
     * Creates View properties actions that will be used for setting View properties.
     */
    private fun createViewProperties() {
        ViewProperties.apply {
            this["scaleType"] = ViewParamAction { view, value, _, _ ->
                if (view is ImageView) {
                    view.scaleType = when (value.lowercase()) {
                        "center" -> ImageView.ScaleType.CENTER
                        "center_crop" -> ImageView.ScaleType.CENTER_CROP
                        "center_inside" -> ImageView.ScaleType.CENTER_INSIDE
                        "fit_center" -> ImageView.ScaleType.FIT_CENTER
                        "fit_end" -> ImageView.ScaleType.FIT_END
                        "fit_start" -> ImageView.ScaleType.FIT_START
                        "fit_xy" -> ImageView.ScaleType.FIT_XY
                        "matrix" -> ImageView.ScaleType.MATRIX
                        else -> ImageView.ScaleType.CENTER
                    }
                }
            }
            this["orientation"] = ViewParamAction { view, value, _, _ ->
                if (view is LinearLayout) {
                    view.orientation = if (value.equals(
                            "vertical", ignoreCase = true
                        )
                    ) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
                }
            }
            this["text"] = ViewParamAction { view, value, _, _ ->
                if (view is TextView) view.text = value
            }
            this["textSize"] = ViewParamAction { view, value, _, _ ->
                if (view is TextView) view.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    value.toPixels(view.resources.displayMetrics) as Float
                )
            }
            this["textColor"] = ViewParamAction { view, value, _, _ ->
                if (view is TextView) view.setTextColor(parseColor(view, value))
            }
            this["textStyle"] = ViewParamAction { view, value, _, _ ->
                if (view is TextView) {
                    var typeFace = when (value.lowercase()) {
                        "normal" -> Typeface.NORMAL
                        "bold" -> Typeface.BOLD
                        "italic" -> Typeface.ITALIC
                        else -> Typeface.NORMAL
                    }
                    view.setTypeface(null, typeFace)
                }
            }
            this["textAlignment"] = ViewParamAction { view, value, _, _ ->
                view?.textAlignment = when (value.lowercase()) {
                    "center" -> View.TEXT_ALIGNMENT_CENTER
                    "right", "end" -> View.TEXT_ALIGNMENT_TEXT_END
                    else -> View.TEXT_ALIGNMENT_TEXT_START
                }
            }
            this["ellipsize"] = ViewParamAction { view, value, _, _ ->
                if (view is TextView) {
                    view.ellipsize = when (value.lowercase()) {
                        "start" -> TextUtils.TruncateAt.START
                        "middle" -> TextUtils.TruncateAt.MIDDLE
                        "marquee" -> TextUtils.TruncateAt.MARQUEE
                        else -> TextUtils.TruncateAt.END
                    }
                }
            }
            this["singleLine"] = ViewParamAction { view, _, _, _ ->
                if (view is TextView) view.setSingleLine()
            }
            this["hint"] = ViewParamAction { view, value, _, _ ->
                if (view is EditText) view.hint = value
            }
            this["inputType"] = ViewParamAction { view, value, _, _ ->
                if (view is TextView) {
                    var inputType = when (value.lowercase()) {
                        "textemailaddress" -> 0 or InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        "number" -> 0 or InputType.TYPE_CLASS_NUMBER
                        "phone" -> 0 or InputType.TYPE_CLASS_PHONE
                        else -> 0
                    }
                    if (inputType > 0) view.inputType = inputType
                }
            }
            this["gravity"] = ViewParamAction { view, value, _, _ ->
                val gravity = parseGravity(value)
                when (view) {
                    is TextView -> view.gravity = gravity
                    is LinearLayout -> view.gravity = gravity
                    is RelativeLayout -> view.gravity = gravity
                }
            }
            this["src"] = ViewParamAction { view, value, _, attrs ->
                if (view is ImageView) {
                    var imageName = value
                    if (imageName.startsWith("//")) imageName = "http:$imageName"
                    if (imageName.startsWith("http")) {
                        attrs["cornerRadius"]?.let {
                            it.toPixels(view.resources.displayMetrics) as Int
                        } ?: 0
                    } else if (imageName.startsWith("@drawable/")) {
                        val drawableName = imageName.substring("@drawable/".length)
                        view.setImageDrawable(getDrawable(view, drawableName))
                    }
                }
            }
            this[Common.VISIBILITY] = ViewParamAction { view, value, _, _ ->
                view?.visibility = when (value.lowercase()) {
                    "gone" -> View.GONE
                    "invisible" -> View.INVISIBLE
                    else -> View.VISIBLE
                }
            }
            this[Common.CLICKABLE] = ViewParamAction { view, value, _, _ ->
                view?.isClickable = value.equals("true", ignoreCase = true)
            }
            this[Common.TAG] = ViewParamAction { view, value, _, _ ->
                if (view?.tag == null) view?.tag = value
            }
            this[ViewAttribute.VIEW_ON_CLICK] = ViewParamAction { view, value, parent, _ ->
                view?.setOnClickListener(getClickListener(parent, value))
            }
        }
    }
}