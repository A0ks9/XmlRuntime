package com.example.ui.activities

import android.Manifest
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.voyager.core.Voyager
import com.voyager.core.view.utils.ViewExtensions.findViewByIdString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Main activity of the application that demonstrates the Voyager XML runtime capabilities.
 * This activity allows users to:
 * 1. Select XML files from device storage
 * 2. Render the selected XML layouts dynamically
 * 3. View theme resources and attributes
 */
class MainActivity : AppCompatActivity() {

    // View binding for the activity layout
    private lateinit var binding: ActivityMainBinding
    // ViewModel for managing UI state and business logic
    private val mainViewModel: MainViewModel by viewModel()
    // Voyager instance for XML rendering
    private val voyager: Voyager by inject { parametersOf(this) }
    // Composite disposable for managing RxJava subscriptions
    private val disposables = CompositeDisposable()
    // Activity result launchers for file selection and permissions
    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display
        enableEdgeToEdge()
        // Set the app theme
        setTheme(R.style.Theme_Voyager)
        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Log theme resources for debugging
        logViewThemeResources(binding.XmlParserButton)

        // Set up data binding
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        // Initialize activity result launchers and UI
        setupActivityResultLaunchers()
        setupUI()

        // Request storage permissions for Android 10 and below
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    /**
     * Helper function to get color from theme attributes
     */
    @ColorInt
    fun Context.getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    /**
     * Logs theme resources and attributes for a given view
     * Used for debugging theme-related issues
     */
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
                    TypedValue.TYPE_DIMENSION -> styleAttributes.append(
                        "Attribute(${attr}): ${
                            typedValue.getDimension(
                                context.resources.displayMetrics
                            )
                        }, "
                    )

                    TypedValue.TYPE_FLOAT -> styleAttributes.append("Attribute(${attr}): ${typedValue.float}, ")
                    TypedValue.TYPE_INT_COLOR_ARGB8, TypedValue.TYPE_INT_COLOR_RGB8, TypedValue.TYPE_INT_COLOR_ARGB4, TypedValue.TYPE_INT_COLOR_RGB4 -> styleAttributes.append(
                        "Attribute(${attr}): ${typedValue.data}, "
                    )

                    else -> styleAttributes.append("Attribute(${attr}): Unknown type, ")
                }
            }
        }

        // Log all the attributes and the view information
        Log.d(
            "Theme",
            "View: ${view::class.java.simpleName}, Text Color: $textColor, Primary Color: $primaryColor, Background Color: $backgroundColor, Theme Resource ID: $themeResourceId, Style Attributes: $styleAttributes"
        )
        println("View: ${view::class.java.simpleName}, Text Color: $textColor, Primary Color: $primaryColor, Background Color: $backgroundColor, Theme Resource ID: $themeResourceId, Style Attributes: $styleAttributes")
    }

    /**
     * Sets up activity result launchers for file selection and permissions
     */
    private fun setupActivityResultLaunchers() {
        // Launcher for opening XML files
        openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let {
                    mainViewModel.setSelectedFileUri(it)
                }
            }

        // Launcher for requesting storage permissions
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val readGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                val writeGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                binding.XmlParserButton.isActivated = readGranted && writeGranted
            }
    }

    /**
     * Sets up the UI components and their interactions
     */
    private fun setupUI() {
        // Handle system window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up XML parser button click listener
        binding.XmlParserButton.setOnClickListener {
            if (mainViewModel.isFileSelected.value != true) {
                // Open file picker if no file is selected
                openDocumentLauncher.launch(arrayOf("application/xml", "text/xml"))
            } else {
                // Render the selected XML file
                val renderFile = mainViewModel.selectedFile.value
                    ?: throw IllegalStateException("Selected file URI is null")

                renderFile.let { uri ->
                    // Use RxJava to handle XML rendering asynchronously
                    val disposableRender = voyager.renderRx(uri).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({ renderedView ->
                            binding.parentLayout.addView(renderedView)
                            setupCustomUI()
                        }, { error ->
                            Log.e(
                                "MainActivity",
                                "Error rendering XML, Details: ${error.message}",
                                error
                            )
                        })
                    disposables.add(disposableRender)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up RxJava subscriptions
        disposables.clear()
    }

    private fun setupCustomUI() {
        binding.parentLayout.findViewByIdString("showXml")?.setOnClickListener {
            Toast.makeText(this@MainActivity, "HELL YEAH !", Toast.LENGTH_SHORT).show()
        }
    }
}
