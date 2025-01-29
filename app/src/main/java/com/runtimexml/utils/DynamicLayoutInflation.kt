package com.runtimexml.utils

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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.runtimexml.utils.DimensionConvertor.stringToDimension
import com.runtimexml.utils.DimensionConvertor.stringToDimensionPixelSize
import com.runtimexml.utils.UtilsKt.convertXml
import com.runtimexml.utils.UtilsKt.getFileExtension
import com.runtimexml.utils.interfaces.ViewParamAction
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

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
                put(it.removePrefix("android:"), jsonObject.optString(it))
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


        var layoutRule = NO_LAYOUT_RULE
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
            //Check which key and value we need to handle.
            when {
                key.startsWith("cornerRadius") -> {
                    //If the key is about cornerRadius, then we just extract boolean of hasCornerRadius and hasCornerRadii,
                    //and continue the loop to next attribute.
                    hasCornerRadius = true
                    hasCornerRadii = !key.equals("cornerRadius", ignoreCase = true)
                }

                key == "id" -> parent?.getGeneratedViewInfo()?.let { generatedView ->
                    //If the key is id, then generate new ID for it, and add it to GeneratedView data class.
                    view.id =
                        View.generateViewId().also { generatedView.viewID[parseID(value)] = it }
                }

                key in listOf("width", "layout_width") -> {
                    //Set the width of the layoutParams for the view.
                    viewParams.width = when (value) {
                        "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                        else -> stringToDimensionPixelSize(
                            value, view.resources.displayMetrics, parent, true
                        )
                    }
                    Log.d(
                        "DynamicLayoutInflation",
                        "width set : ${view.javaClass.simpleName}, value: $value,width: ${viewParams.width}"
                    )
                }

                key in listOf("height", "layout_height") -> {
                    //Set the height of the layoutParams for the view.
                    viewParams.height = when (value) {
                        "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                        else -> stringToDimensionPixelSize(
                            value, view.resources.displayMetrics, parent, false
                        )
                    }
                    Log.d(
                        "DynamicLayoutInflation",
                        "height set: ${view.javaClass.simpleName}, value: $value, height: ${viewParams.height}"
                    )

                }

                key == "layout_gravity" -> when (parent) {
                    // set the gravity of the layoutParams
                    is LinearLayout -> (viewParams as LinearLayout.LayoutParams).gravity =
                        parseGravity(value)

                    is FrameLayout -> (viewParams as FrameLayout.LayoutParams).gravity =
                        parseGravity(value)
                }

                key == "layout_weight" && parent is LinearLayout -> {
                    //Set the weight of the layoutParams
                    (viewParams as LinearLayout.LayoutParams).weight = value.toFloat()
                }

                key == "layout_below" -> layoutRule = RelativeLayout.BELOW
                key == "layout_above" -> layoutRule = RelativeLayout.ABOVE
                key == "layout_toLeftOf" -> layoutRule = RelativeLayout.LEFT_OF
                key == "layout_toRightOf" -> layoutRule = RelativeLayout.RIGHT_OF
                key == "layout_alignBottom" -> layoutRule = RelativeLayout.ALIGN_BOTTOM
                key == "layout_alignTop" -> layoutRule = RelativeLayout.ALIGN_TOP
                key == "layout_alignLeft" -> layoutRule = RelativeLayout.ALIGN_LEFT
                key == "layout_alignStart" -> layoutRule = RelativeLayout.ALIGN_START
                key == "layout_alignRight" -> layoutRule = RelativeLayout.ALIGN_RIGHT
                key == "layout_alignEnd" -> layoutRule = RelativeLayout.ALIGN_END
                key == "layout_alignParentBottom" -> layoutRule = RelativeLayout.ALIGN_PARENT_BOTTOM
                key == "layout_alignParentTop" -> layoutRule = RelativeLayout.ALIGN_PARENT_TOP
                key == "layout_alignParentLeft" -> layoutRule = RelativeLayout.ALIGN_PARENT_LEFT
                key == "layout_alignParentStart" -> layoutRule = RelativeLayout.ALIGN_PARENT_START
                key == "layout_alignParentRight" -> layoutRule = RelativeLayout.ALIGN_PARENT_RIGHT
                key == "layout_alignParentEnd" -> layoutRule = RelativeLayout.ALIGN_PARENT_END
                key == "layout_centerHorizontal" -> layoutRule = RelativeLayout.CENTER_HORIZONTAL
                key == "layout_centerVertical" -> layoutRule = RelativeLayout.CENTER_VERTICAL
                key == "layout_centerInParent" -> layoutRule = RelativeLayout.CENTER_IN_PARENT
                key == "layout_margin" -> stringToDimensionPixelSize(
                    value, view.resources.displayMetrics
                ).let {
                    marginLeft = it
                    marginRight = it
                    marginTop = it
                    marginBottom = it
                }

                key in listOf("layout_marginLeft", "layout_marginStart") -> marginLeft =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics, parent, true)

                key in listOf("layout_marginRight", "layout_marginEnd") -> marginRight =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics, parent, true)

                key == "layout_marginTop" -> marginTop =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics, parent, false)

                key == "layout_marginBottom" -> marginBottom =
                    stringToDimensionPixelSize(value, view.resources.displayMetrics, parent, false)

                key == "padding" -> stringToDimensionPixelSize(
                    value, view.resources.displayMetrics
                ).let {
                    paddingLeft = it
                    paddingRight = it
                    paddingTop = it
                    paddingBottom = it
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
        }
        Log.d(
            "DynamicLayoutInflation",
            "After applying attributes, View: ${view.javaClass.simpleName}, width: ${viewParams.width}, height: ${viewParams.height}"
        )
        (viewParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            marginLeft, marginTop, marginRight, marginBottom
        )
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        view.layoutParams = viewParams // Set the layout params
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
                        val radius = stringToDimension(
                            attrs["cornerRadius"].toString(), view.resources.displayMetrics
                        )
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
    private fun parseID(id: String): String = id.removePrefix("@+id/").removePrefix("@id/")

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
    private fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }

    /**
     *  Retrieves the view id of a view inside the viewGroup.
     *  If the view does not exists, then returns -1
     *  @param root The root view that we are search inside
     *  @param id The id to search for.
     *  @return The integer Id of the view if present, or -1 if not found
     */
    private fun getViewID(root: View, id: String): Int {
        if (root !is ViewGroup) return -1
        return (root.tag as? GeneratedView)?.viewID?.get(id) ?: root.childrenSequence()
            .mapNotNull { getViewID(it, id).takeIf { it > -1 } }.firstOrNull() ?: -1
    }

    /**
     * Get a sequence of view's children for iteration.
     * @return Sequence of the children.
     */
    private fun ViewGroup.childrenSequence(): Sequence<View> = object : Sequence<View> {
        override fun iterator(): Iterator<View> = object : Iterator<View> {
            private var index = 0
            override fun hasNext(): Boolean = index < childCount
            override fun next(): View = getChildAt(index++)
        }
    }

    /**
     * Tries to find a view from root View based on String ID
     *
     * @param id String id that should match with the ID of View
     * @return The view which is found with the string id, otherwise null.
     */
    fun View.findViewByIdString(id: String): View? {
        val idNum = getViewID(this, id)
        return if (idNum < 0) null else findViewById(idNum)
    }

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
                    stringToDimension(value, view.resources.displayMetrics)
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
                            stringToDimensionPixelSize(it, view.resources.displayMetrics)
                        } ?: 0
                    } else if (imageName.startsWith("@drawable/")) {
                        val drawableName = imageName.substring("@drawable/".length)
                        view.setImageDrawable(getDrawable(view, drawableName))
                    }
                }
            }
            this["visibility"] = ViewParamAction { view, value, _, _ ->
                view?.visibility = when (value.lowercase()) {
                    "gone" -> View.GONE
                    "invisible" -> View.INVISIBLE
                    else -> View.VISIBLE
                }
            }
            this["clickable"] = ViewParamAction { view, value, _, _ ->
                view?.isClickable = value.equals("true", ignoreCase = true)
            }
            this["tag"] = ViewParamAction { view, value, _, _ ->
                if (view?.tag == null) view?.tag = value
            }
            this["onClick"] = ViewParamAction { view, value, parent, _ ->
                view?.setOnClickListener(getClickListener(parent, value))
            }
        }
    }

    private data class GeneratedView(
        var viewID: HashMap<String, Int> = HashMap(),
        var delegate: Any? = null,
        var bgDrawable: GradientDrawable? = null
    )
}