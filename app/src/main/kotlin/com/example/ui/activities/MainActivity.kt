package com.example.ui.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        initialize(
            binding,
            this,
            this,
            R.style.Theme_Voyager,
            savedInstanceState,
            provider = ResourcesBridge()
        ) {
            setContentView(it?.root ?: binding.root)
        }

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
        mainViewModel.fileNameToCreate.observe(this) { fileName ->
            fileName?.let { createDocumentLauncher.launch(it) } // Launch file creation when filename is ready
        }

        mainViewModel.isFileCreated.observe(this) { isCreated ->
            if (isCreated) Log.d("MainActivity", "File created successfully!")
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

                mainViewModel.isFileCreated.value == false -> mainViewModel.convertXmlToJson(this@MainActivity)
            }
        }

        binding.showXml.setOnClickListener {
            inflateAndShowJsonView()
        }
    }

    private fun inflateAndShowJsonView() {
        val createdFileUri = mainViewModel.createdFileUri.value ?: return
        inflate(this, R.style.Theme_Voyager, createdFileUri, binding.parentLayout) { view ->
            DynamicLayoutInflation.setDelegate(view, applicationContext)
            view?.post { Log.d("MainActivity", "Inflated view: $view") }
        }
    }

    override fun onStop() {
        saveViewData()
        releaseInstance()
        super.onStop()
    }

    private fun saveViewData() {
        saveDataWithRoom()
    }

    override fun getContainerLayout(): ViewGroup = binding.parentLayout

    override fun getJsonConfiguration(): String? = null

    override fun onViewCreated(parentView: ViewGroup?) {
        // Optional callback after view inflation
    }
}