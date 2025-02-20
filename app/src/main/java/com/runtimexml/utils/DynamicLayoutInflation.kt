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

    internal val viewsState = mutableListOf<ViewState>()

    // Initialization block where properties actions are created and added to ViewProperties map
    init {
        BaseViewAttributes.initializeAttributes()
    }

    //what if the file has JSONArray not JSONObject
    /**
     * Inflates a layout from a JSON or XML resource URI.
     *
     * @param context The context used to access resources.
     * @param layoutUri The URI of the JSON or XML resource.
     * @param parent The parent ViewGroup that will contain the inflated views.
     * @return The root View of the inflated layout or null if an error occurs.
     */
    @JvmStatic
    fun inflateJson(context: Context, layoutUri: Uri, parent: ViewGroup?): View? = try {
        //check if the data that inside the file are jsonObject or array and based on that choose the fun that will do it
        when (getFileExtension(context, layoutUri)?.lowercase()) {
            "json" -> context.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
                createViews(context, JSONObject(inputStream.bufferedReader().readText()), parent, true)
            }

            "xml" -> convertXml(context.contentResolver, layoutUri)?.let {
                createViews(
                    context, it, parent, true
                )
            }

            else -> null // If file type is not JSON or XML.
        }
    } catch (error: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", error)
        null //If error is encountered then return null.
    }

    //what if the jsonObject that the developer passed doesn't have container and the whole views are Views not ViewGroup
    /**
     * Inflates a layout from a JSON object.
     *
     * @param context The context used to access resources.
     * @param layout The JSON object representing the layout.
     * @param parent The parent ViewGroup that will contain the inflated views.
     * @return The root View of the inflated layout or null if an error occurs.
     */
    @JvmStatic
    fun inflateJson(context: Context, layout: JSONObject, parent: ViewGroup?): View? = try {
        val createdViews = createViews(context, layout, parent, true)
        viewsState.updateViewStates(context.getActivityName(),
            viewsState.filter { it.activityName == context.getActivityName() })
        createdViews
    } catch (error: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", error)
        null //If error is encountered then return null.
    }

    @JvmStatic
    fun inflateJson(context: Context, layout: JSONArray, parent: ViewGroup?): List<View?> = try {
        val createdViews = createViews(context, layout, parent)
        viewsState.updateViewStates(context.getActivityName(),
            viewsState.filter { it.activityName == context.getActivityName() })
        createdViews
    } catch (error: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", error)
        emptyList<View>()
    }

    /**
     * Inflates a view hierarchy from a [ViewState] definition within a given parent.
     *
     * This function attempts to create and inflate a view hierarchy based on the provided [layout].
     * It utilizes the `createViews` function to perform the actual view creation.
     *
     * If any exception occurs during the view creation process, it logs the error and returns `null`.
     * This ensures that the application doesn't crash due to potential errors in the layout definition.
     *
     * @param context The application context used for view creation.
     * @param layout The [ViewState] object defining the layout to be inflated. This object should
     *                         contain the necessary information to create the desired view hierarchy.
     * @param parent The [ViewGroup] into which the inflated view hierarchy will be added.
     *                  This acts as the parent parent for the new views.
     * @return The root [View] of the inflated hierarchy if successful, or `null` if an exception occurred.
     *
     * @throws Exception if there are problems during view creation, but these are caught internally and result in null return
     *
     * @see createViews
     * @see ViewState
     */
    internal fun inflateViewState(
        context: Context, layout: List<ViewState>, parent: ViewGroup?
    ): List<View?> = try {
        createViews(context, layout, parent)
    } catch (error: Exception) {
        Log.e("DynamicLayoutInflation", "Error inflating JSON", error)
        emptyList<View?>()
    }

    /**
     * Creates the views dynamically from a JSON object.
     * @param context The Context used to access resources.
     * @param layout The JSON object representing the view.
     * @param parent The parent ViewGroup that will contain the created views
     * @return The created View, null if the View was not created successfully
     */
    private fun createViews(
        context: Context, layout: JSONObject, parent: ViewGroup?, isFirst: Boolean
    ): View? = getViewForName(context, layout.getString("type"))?.apply {
        //Add the view to the parent
        parent?.addView(this)
        //Apply attributes from JSON to the newly created view
        val attrs = getAttrsMap(layout.getJSONObject("attributes"))
        val viewState = if (isFirst) ViewState(
            attrs[Attributes.Common.ID]!!,
            context.getActivityName(),
            layout.getString("type"),
            attrs.toJsonString()
        ) else null
        applyAttributes(this, attrs, parent)
        if (this is ViewGroup && (layout.optJSONArray("children")?.length() ?: 0) > 0) {
            //If the view has children then parse these children.
            parseChildren(context, layout.optJSONArray("children"), this)
            viewState?.children = layout.optJSONArray("children")?.toString(4)
        }
        if (isFirst) viewsState.add(viewState!!)
    }


    /**
     * Creates a list of Android Views based on a JSON array representing a UI layout.
     */
    private fun createViews(context: Context, layout: JSONArray, parent: ViewGroup?): List<View?> {
        val createdViews = mutableListOf<View?>()
        (0 until layout.length()).forEach { i ->
            val jsonObject = layout.getJSONObject(i)
            getViewForName(context, jsonObject.getString("type"))?.apply {
                //Add the view to the parent
                parent?.addView(this)
                //Apply attributes from JSON to the newly created view
                val attrs = getAttrsMap(jsonObject.getJSONObject("attributes"))
                applyAttributes(this, attrs, parent)
                val viewState = ViewState(
                    attrs["id"]!!,
                    context.getActivityName(),
                    jsonObject.getString("type"),
                    attrs.toJsonString()
                )
                if (this is ViewGroup && (jsonObject.optJSONArray("children")?.length() ?: 0) > 0) {
                    //If the view has children then parse these children.
                    parseChildren(context, jsonObject.optJSONArray("children"), this)
                    viewState.children = jsonObject.optJSONArray("children")?.toString(4)
                }
                viewsState.add(viewState)
                createdViews.add(this)
            }
        }
        return createdViews
    }

    /**
     * Creates and configures a view based on the provided layout definition.
     *
     * This function is responsible for:
     * 1. Retrieving the correct view class based on the `layout.type`.
     * 2. Adding the newly created view to the specified `parent`.
     * 3. Applying attributes defined in the `layout` to the view.
     * 4. If the view is a `ViewGroup` and has child definitions in `layout.children`, recursively parsing and adding those child views.
     *
     * @param context          The application context. Used to create new View instances.
     * @param layout The definition of the view to create, including its type, attributes, and children.
     * @param parent        The ViewGroup to which the newly created view will be added.
     * @return The newly created and configured View, or null if the view type was not recognized or an error occurred during view creation.
     * @see ViewState
     * @see getViewForName(Context, String)
     * @see applyAttributes(View, Map, ViewGroup)
     * @see parseChildren(Context, List, ViewGroup)
     */
    private fun createViews(
        context: Context, layout: List<ViewState>, parent: ViewGroup?
    ): List<View?> {
        val createdViews = mutableListOf<View?>()
        layout.forEach { layout ->
            getViewForName(context, layout.type)?.apply {
                //Add the view to the parent
                parent?.addView(this)
                //Apply attributes from JSON to the newly created view
                applyAttributes(this, layout.getAttributes(), parent)
                if (this is ViewGroup) {
                    val viewChildren = layout.retrieveChildren()
                    if (viewChildren != null && viewChildren.length() > 0) {
                        //If the view has children then parse these children.
                        parseChildren(context, layout.retrieveChildren(), this)
                    }
                }
                createdViews.add(this)
            }
        }
        return createdViews
    }

    /**
     * Parses the child views and adds them to the parent.
     * @param context The Context used to access resources.
     * @param children The JSON children of child views.
     * @param parent The parent view where the children should be added.
     */
    private fun parseChildren(
        context: Context, children: JSONArray?, parent: ViewGroup
    ) {
        children?.let {
            for (index in 0 until it.length()) {
                val childJson = it.getJSONObject(index)
                createViews(context, childJson, parent, false)
            }
        }
    }

    /**
     * Get an Android View from the type
     * @param context The context used to access the resources
     * @param type  The type of the View, such as TextView, LinearLayout.
     * @return The created view or null if an exception is found.
     */
    private fun getViewForName(context: Context, type: String): View? = try {
        val fullyQualifiedViewType = if (!type.contains(".")) "android.widget.$type" else type
        if (ViewProcessor.isRegistered(type)) ViewProcessor.createView(type, context)

        // Create a class object from the type
        Class.forName(fullyQualifiedViewType).getConstructor(Context::class.java)
            .newInstance(context) as? View
    } catch (error: Exception) {
        Log.e("DynamicLayoutInflation", "Error getting view for type", error)
        null // Returns null when an error happens.
    }

    /**
     * Get a Map of rawAttributes that can be applied to the View.
     * @param rawAttributes the rawAttributes that represents view's rawAttributes
     * @return HashMap that contains the key and values of rawAttributes.
     */
    private fun getAttrsMap(rawAttributes: JSONObject): HashMap<String, String> =
        HashMap<String, String>(rawAttributes.length()).apply {
            rawAttributes.keys().forEach {
                // Put all the rawAttributes from json object into a map.
                put(it, rawAttributes.optString(it))
            }
        }

    /**
     * Applies attributes to a given view.
     * @param view The View to apply attributes to.
     * @param attributes A map of attribute names to their values.
     * @param parent The parent ViewGroup of the view.
     */
    private fun applyAttributes(
        view: View, attributes: HashMap<String, String>, parent: ViewGroup?
    ) {
        //Inside applyAttributes Function
        val params = view.layoutParams ?: if (parent is LinearLayout) LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        else RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        Log.d(
            "DynamicLayoutInflation",
            "Before apply attributes, View: ${view.javaClass.simpleName},  width: ${params.width}, height: ${params.height}, parent: ${parent?.javaClass?.simpleName}"
        )

        AttributeRegistry.applyAttributes(view, attributes)

        Log.d(
            "DynamicLayoutInflation",
            "After applying attributes, View: ${view.javaClass.simpleName}, width: ${params.width}, height: ${params.height}"
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
     * @param view The View for which to set the delegate.
     * @param delegate  The delegate object to set.
     */
    @JvmStatic
    fun setDelegate(view: View?, delegate: Any) {
        view?.getGeneratedViewInfo()?.delegate = delegate
    }
}