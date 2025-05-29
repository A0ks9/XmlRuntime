package com.example.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.R
import com.example.databinding.ActivityMainBinding
import com.example.ui.viewModels.MainViewModel
import com.voyager.resources.ResourcesBridge
import com.voyager.utils.DynamicLayoutInflation
import com.voyager.utils.DynamicLayoutInflation.inflate
import com.voyager.utils.interfaces.ViewHandler
import com.voyager.utils.interfaces.ViewHandler.Companion.initialize
import com.voyager.utils.interfaces.ViewHandler.Companion.saveDataWithRoom
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), ViewHandler {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel() // Inject ViewModel using Koin
    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>

    // References to dynamically inflated views
    private var dynamicTextView: TextView? = null
    private var dynamicEditText: android.widget.EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.Theme_Voyager)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initialize(
            binding,
            this,
            this,
            R.style.Theme_Voyager,
            savedInstanceState,
            provider = ResourcesBridge()
        ) {}

        setContentView(binding.root)

        logViewThemeResources(binding.XmlParserButton)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        setupActivityResultLaunchers()
        setupObservers()
        setupUI()

        // Request necessary permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) { // For Android 10 and below
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    @ColorInt
    fun Context.getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    // Function to get and log the theme resources of a view
    fun logViewThemeResources(view: View) {
        val context = view.context

        // Get the theme resource ID
        val themeResourceId = context.resources.configuration.uiMode

        // Get text color (if the view supports it)
        val textColor = when (view) {
            is TextView -> view.currentTextColor
            else -> null
        }

        // Get primary color
        val primaryColor = context.getColorFromAttr(android.R.attr.colorPrimary)

        // Get background color
        val backgroundColor = (view.background as? ColorDrawable)?.color

        // Create a string to hold the list of all attributes
        val styleAttributes = StringBuilder()

        // Attributes to check for
        val attributes = intArrayOf(
            android.R.attr.textColor,
            android.R.attr.colorPrimary,
            android.R.attr.background,
            android.R.attr.textSize,
            android.R.attr.fontFamily,
            android.R.attr.colorAccent,
            android.R.attr.colorControlNormal,
            android.R.attr.colorControlActivated,
            android.R.attr.colorButtonNormal,
            android.R.attr.buttonStyle,
            android.R.attr.editTextStyle,
            android.R.attr.spinnerStyle,
            android.R.attr.buttonStyleSmall,
            android.R.attr.colorPrimaryDark,
            android.R.attr.actionModeBackground,
            android.R.attr.actionModeCloseDrawable,
            android.R.attr.alertDialogTheme,
            android.R.attr.windowBackground
        )

        // Loop through the attributes and resolve their values
        for (attr in attributes) {
            val typedValue = TypedValue()
            val resolved = context.theme.resolveAttribute(attr, typedValue, true)

            if (resolved) {
                // Check the type of the attribute and handle accordingly
                when (typedValue.type) {
                    TypedValue.TYPE_STRING -> styleAttributes.append("Attribute(${attr}): ${typedValue.string}, ")
                    TypedValue.TYPE_DIMENSION -> styleAttributes.append("Attribute(${attr}): ${typedValue.getDimension(context.resources.displayMetrics)}, ")
                    TypedValue.TYPE_FLOAT -> styleAttributes.append("Attribute(${attr}): ${typedValue.float}, ")
                    TypedValue.TYPE_INT_COLOR_ARGB8, TypedValue.TYPE_INT_COLOR_RGB8, TypedValue.TYPE_INT_COLOR_ARGB4, TypedValue.TYPE_INT_COLOR_RGB4 ->
                        styleAttributes.append("Attribute(${attr}): ${typedValue.data}, ")
                    else -> styleAttributes.append("Attribute(${attr}): Unknown type, ")
                }
            }
        }

        // Log all the attributes and the view information
        Log.d("Theme", "View: ${view::class.java.simpleName}, Text Color: $textColor, Primary Color: $primaryColor, Background Color: $backgroundColor, Theme Resource ID: $themeResourceId, Style Attributes: $styleAttributes")
        println("View: ${view::class.java.simpleName}, Text Color: $textColor, Primary Color: $primaryColor, Background Color: $backgroundColor, Theme Resource ID: $themeResourceId, Style Attributes: $styleAttributes")
    }

    private fun setupActivityResultLaunchers() {
        openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let {
                    mainViewModel.setSelectedFileUri(it)
                }
            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val readGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                val writeGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                binding.XmlParserButton.isActivated = readGranted && writeGranted
            }

        createDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
                uri?.let {  // Persist URI permissions
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    mainViewModel.writeToFile(it, contentResolver)
                }
            }

    }

    private fun setupObservers() {
        mainViewModel.requestCreateFileEvent.observe(this) { fileName ->
            fileName?.let {
                createDocumentLauncher.launch(it) // Launch file creation when filename is ready
                mainViewModel.creationRequestHandled() // Notify ViewModel that event has been handled
            }
        }

        mainViewModel.isFileCreated.observe(this) { isCreated ->
            if (isCreated) Log.d("MainActivity", "File created successfully!")
        }

        // Observer for dynamic TextView data
        mainViewModel.dynamicTextViewData.observe(this) { text ->
            dynamicTextView?.text = text
            Log.d("MainActivity", "Dynamic TextView updated: $text")
        }

        // Observer for dynamic EditText data
        mainViewModel.dynamicEditTextData.observe(this) { text ->
            // Only update if the text is different to avoid infinite loops with TextWatcher
            if (dynamicEditText?.text.toString() != text) {
                dynamicEditText?.setText(text)
                Log.d("MainActivity", "Dynamic EditText updated from ViewModel: $text")
            }
        }
    }

    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.XmlParserButton.setOnClickListener {
            when {
                mainViewModel.isFileSelected.value == false -> openDocumentLauncher.launch(
                    arrayOf(
                        "application/xml", "text/xml"
                    )
                )

                mainViewModel.isFileCreated.value == false -> mainViewModel.convertXmlToJson(this.contentResolver)
            }
        }

        binding.showXml.setOnClickListener {
            inflateAndShowJsonView()
        }
    }

    private fun inflateAndShowJsonView() {
        val createdFileUri = mainViewModel.createdFileUri.value ?: return
        inflate(this, R.style.Theme_Voyager, createdFileUri, binding.parentLayout) { inflatedView ->
            // Assuming DynamicLayoutInflation.setDelegate is for other potential specific view interactions.
            // For standard views like EditText and TextView, direct manipulation is more straightforward here.
            DynamicLayoutInflation.setDelegate(inflatedView, applicationContext) // Keep if it serves other purposes

            inflatedView?.let { viewGroup ->
                // Find and setup Dynamic TextView
                this.dynamicTextView = viewGroup.findViewWithTag<TextView>("myDynamicTextView")
                this.dynamicTextView?.let { tv ->
                    tv.text = mainViewModel.dynamicTextViewData.value
                    Log.d("MainActivity", "Dynamic TextView initialized with: ${tv.text}")
                } ?: Log.e("MainActivity", "Dynamic TextView with tag 'myDynamicTextView' not found.")

                // Find and setup Dynamic EditText
                this.dynamicEditText = viewGroup.findViewWithTag<android.widget.EditText>("myDynamicEditText")
                this.dynamicEditText?.let { et ->
                    et.setText(mainViewModel.dynamicEditTextData.value)
                    et.addTextChangedListener(object : android.text.TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: android.text.Editable?) {
                            val newText = s.toString()
                            // Only update ViewModel if the text actually changed,
                            // and if it's different from what ViewModel currently holds,
                            // to prevent potential loops if ViewModel observation also sets text.
                            if (mainViewModel.dynamicEditTextData.value != newText) {
                                mainViewModel.updateDynamicEditTextData(newText)
                                Log.d("MainActivity", "Dynamic EditText new text sent to ViewModel: $newText")
                            }
                        }
                    })
                    Log.d("MainActivity", "Dynamic EditText initialized and TextWatcher attached. Initial text: ${et.text}")
                } ?: Log.e("MainActivity", "Dynamic EditText with tag 'myDynamicEditText' not found.")

                Log.d("MainActivity", "Inflated view hierarchy processed for dynamic elements.")
            }
            inflatedView?.post { Log.d("MainActivity", "Inflated view: $inflatedView") }
        }
    }

    override fun onStop() {
        saveViewData()
        super.onStop()
    }

    private fun saveViewData() {
        saveDataWithRoom(this)
    }

    override fun getContainerLayout(): ViewGroup = binding.parentLayout

    override fun getJsonConfiguration(): String? = null

    override fun onViewCreated(parentView: ViewGroup?) {
        // Optional callback after view inflation
    }
}