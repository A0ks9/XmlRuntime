package com.runtimexml.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.runtimexml.utils.FileHelper.convertXml
import com.runtimexml.utils.FileHelper.getFileExtension
import com.runtimexml.utils.Utils.getGeneratedViewInfo
import com.runtimexml.utils.processors.AttributeRegistry
import com.runtimexml.utils.processors.ViewProcessor
import org.json.JSONArray
import org.json.JSONObject

/**
 * Object that handles the dynamic inflation of layouts from JSON or XML resources.
 * It provides methods to parse the structure and apply attributes to the views, as well as adding them to the view hierarchy
 */
object DynamicLayoutInflation {
    // Constant for layout rule when there is no rule is applied.
    private const val NO_LAYOUT_RULE = -999

    // List of corner names for drawing rounded corners.
    private val CORNERS = listOf("TopLeft", "TopRight", "BottomLeft", "BottomRight")

    // Initialization block where properties actions are created and added to ViewProperties map
    init {
        BaseViewAttributes.initializeAttributes()
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
    fun inflateJson(context: Context, uri: Uri, parent: ViewGroup): View? = try {
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
    fun inflateJson(context: Context, json: JSONObject, parent: ViewGroup): View? = try {
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
    private fun createViews(context: Context, json: JSONObject, parent: ViewGroup): View? =
        getViewForName(context, json.getString("type"))?.apply {
            //Add the view to the parent
            parent.addView(this)
            //Apply attributes from JSON to the newly created view
            applyAttributes(this, getAttrsMap(json.getJSONObject("attributes")), parent)

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
                put(it, jsonObject.optString(it))
            }
        }

    /**
     * Applies attributes to a given view.
     * @param view The View to apply attributes to.
     * @param attrs A map of attribute names to their values.
     * @param parent The parent ViewGroup of the view.
     */
    private fun applyAttributes(view: View, attrs: HashMap<String, String>, parent: ViewGroup) {
        //Inside applyAttributes Function
        val viewParams = view.layoutParams ?: if (parent is LinearLayout) LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        else RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        Log.d(
            "DynamicLayoutInflation",
            "Before apply attributes, View: ${view.javaClass.simpleName},  width: ${viewParams.width}, height: ${viewParams.height}, parent: ${parent.javaClass.simpleName}"
        )

        AttributeRegistry.applyAttribute(view, Attributes.Common.ID, attrs.get("id"))
        attrs.remove("id")
        AttributeRegistry.applyAttributes(view, attrs)

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
//    private fun createBackgroundDrawable(
//        view: View,
//        backgroundColor: Int,
//        pressedColor: Int,
//        attrs: Map<String, String>,
//        hasCornerRadii: Boolean,
//        hasCornerRadius: Boolean
//    ): Drawable = GradientDrawable().apply { color = ColorStateList.valueOf(backgroundColor) }
//        .let { gradientDrawable ->
//            GradientDrawable().apply { color = ColorStateList.valueOf(pressedColor) }
//                .let { pressedGradientDrawable ->
//                    if (hasCornerRadii) {
//                        val radii = FloatArray(8)
//                        for (i in 0 until CORNERS.size) {
//                            val corner = CORNERS[i]
//                            if (attrs.containsKey("cornerRadius$corner")) {
//                                val radius = attrs["cornerRadius$corner"].toString().toPixels(
//                                    view.resources.displayMetrics
//                                ) as Float
//                                radii[i * 2] = radius
//                                radii[i * 2 + 1] = radius
//                            }
//                            gradientDrawable.cornerRadii = radii
//                            pressedGradientDrawable.cornerRadii = radii
//                        }
//                    } else if (hasCornerRadius) {
//                        val radius = attrs["cornerRadius"].toString().toPixels(
//                            view.resources.displayMetrics
//                        ) as Float
//                        gradientDrawable.cornerRadius = radius
//                        pressedGradientDrawable.cornerRadius = radius
//                    }
//                    if (attrs.containsKey("borderColor")) {
//                        val borderWidth = attrs["borderWidth"] ?: "1dp"
//                        val borderWidthPixel =
//                            borderWidth.toPixels(view.resources.displayMetrics) as Int
//                        val borderColor = parseColor(view, attrs["borderColor"].toString())
//                        gradientDrawable.setStroke(borderWidthPixel, borderColor)
//                        pressedGradientDrawable.setStroke(borderWidthPixel, borderColor)
//                    }
//                    StateListDrawable().apply {
//                        if (view is Button || attrs.containsKey("pressedColor")) addState(
//                            intArrayOf(
//                                android.R.attr.state_pressed
//                            ), pressedGradientDrawable
//                        )
//                        addState(intArrayOf(), gradientDrawable)
//                    }
//                }
//        }

    /**
     * Sets the delegate object for a view.
     * @param root The View for which to set the delegate.
     * @param delegate  The delegate object to set.
     */
    @JvmStatic
    fun setDelegate(root: View?, delegate: Any) {
        root?.getGeneratedViewInfo()?.delegate = delegate
    }
}