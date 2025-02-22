package com.runtimexml.ui.activities

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.runtimexml.databinding.ActivityMainBinding
import com.runtimexml.ui.viewModels.MainViewModel
import com.runtimexml.utils.DynamicLayoutInflation
import com.runtimexml.utils.JsonCast
import com.runtimexml.utils.interfaces.ViewHandler
import com.runtimexml.utils.interfaces.ViewHandler.Companion.initialize
import com.runtimexml.utils.interfaces.ViewHandler.Companion.saveDataWithRoom
import com.runtimexml.utils.interfaces.ViewHandler.Companion.saveInstanceState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), ViewHandler {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel() // Inject ViewModel using Koin
    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        initialize(binding, this, this, savedInstanceState) { newBinding ->
            val b = newBinding ?: binding
            setContentView(b.root)
        }

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        setupActivityResultLaunchers()
        setupObservers()
        setupUI()

        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setupActivityResultLaunchers() {
        openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let { mainViewModel.setSelectedFileUri(it) }
            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                binding.XmlParserButton.isActivated = granted
            }

        createDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
                uri?.let { mainViewModel.writeToFile(it, contentResolver) }
            }
    }

    private fun setupObservers() {
        mainViewModel.fileNameToCreate.observe(this) { fileName ->
            fileName?.let { createDocumentLauncher.launch(it) } // Launch file creation when filename is ready
        }

        mainViewModel.isFileCreated.observe(this) { isCreated ->
            // Update UI based on file creation status if needed
        }

        mainViewModel.enableShowing.observe(this) { enableShowing ->
            // Update UI if needed based on showing button enable state
        }
    }


    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.XmlParserButton.setOnClickListener {
            if (mainViewModel.isFileSelected.value == false) {
                openDocumentLauncher.launch(arrayOf("application/xml", "text/xml"))
            } else if (mainViewModel.isFileCreated.value == false) {
                mainViewModel.convertXmlToJson(contentResolver) // Trigger XML conversion in ViewModel
            }
        }

        binding.showXml.setOnClickListener {
            inflateAndShowJsonView()
        }
    }

    private fun inflateAndShowJsonView() {
        val createdFileUri = mainViewModel.createdFileUri.value ?: return
        val view = DynamicLayoutInflation.inflateJson(this, createdFileUri, binding.parentLayout)
        view?.let {
            DynamicLayoutInflation.setDelegate(it, applicationContext)
            it.post { Log.d("MainActivity", "Inflated view: $it") }
        }
    }

    override fun onStop() {
        saveViewData()
        super.onStop()
    }

    private fun saveViewData() {
        // Trigger save view data in ViewModel if needed, or directly call ViewHelper.saveDataWithRoom(this)
        saveDataWithRoom(this) // Or delegate to ViewModel if you want ViewModel to control this logic
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstanceState(this, outState) // Or delegate to ViewModel
    }

    override fun getContainerLayout(): ViewGroup {
        return binding.parentLayout
    }

    override fun getJsonConfiguration(): JsonCast? {
        return null // Or return your JsonCast configuration if needed
    }

    override fun onViewCreated(parentView: ViewGroup?) {
        // Optional callback after view inflation
    }
}