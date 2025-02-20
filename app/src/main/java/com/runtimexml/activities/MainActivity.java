package com.runtimexml.activities;

import static com.runtimexml.utils.FileHelper.convertXmlToJson;
import static com.runtimexml.utils.FileHelper.getFileNameFromUri;
import static com.runtimexml.utils.interfaces.ViewHandler.init;
import static com.runtimexml.utils.interfaces.ViewHandler.saveDataWithRoom;
import static com.runtimexml.utils.interfaces.ViewHandler.saveInstanceState;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.runtimexml.databinding.ActivityMainBinding;
import com.runtimexml.utils.DynamicLayoutInflation;
import com.runtimexml.utils.JsonCast;
import com.runtimexml.utils.RoomHelper;
import com.runtimexml.utils.ViewHelper;
import com.runtimexml.utils.interfaces.ViewHandler;
import com.runtimexml.viewModel.MainViewModel;

import java.io.OutputStream;
import java.util.Objects;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity implements ViewHandler {

    private String parsedXml;
    private Uri createdFileUri;
    private boolean isFileSelected = false, isFileCreated = false;
    private final ActivityResultLauncher<String[]> openDocument = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
        if (result != null) {
            MainViewModel.setSelectedFile(result);
            isFileSelected = true;
        }
    });
    private ActivityMainBinding binding;
    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> binding.XmlParserButton.setActivated(granted));
    private ViewHelper viewHelper;
    private MainViewModel mainViewModel;
    private final ActivityResultLauncher<String> createDocument = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
        if (uri != null) writeToFile(uri);
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);


            init(this, this, savedInstanceState, editedContainer -> Unit.INSTANCE);
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);


        setupUI();
        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void setupUI() {
        // Adjust system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.XmlParserButton.setOnClickListener(v -> {
            if (!isFileSelected) {
                openDocument.launch(new String[]{"application/xml", "text/xml"});
            } else if (!isFileCreated) {
                Uri uri = mainViewModel.getSelectedFile().getValue();
                if (uri != null) {
                    parsedXml = convertXmlToJson(getContentResolver(), uri);
                    String fileName = getFileNameFromUri(uri, getContentResolver());
                    createDocument.launch(Objects.requireNonNull(fileName).replace(".xml", ".json"));
                }
            }
        });

        binding.showXml.setOnClickListener(v -> inflateAndShowJsonView());
    }

    private void inflateAndShowJsonView() {
        if (createdFileUri == null) return;
        View view = DynamicLayoutInflation.inflateJson(this, createdFileUri, binding.parentLayout);
        if (view != null) {
            DynamicLayoutInflation.setDelegate(view, getApplicationContext());
            view.post(() -> Log.d("MainActivity", "Inflated view: " + view));
        }
    }

    private void writeToFile(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                outputStream.write(parsedXml.getBytes());
                Log.d("CreateFileActivity", "Content written successfully.");
                createdFileUri = uri;
                isFileCreated = true;
                mainViewModel.EnableShowingButton(true);
            }
        } catch (Exception e) {
            Log.e("CreateFileActivity", "Error writing to file", e);
        }
    }

    @Override
    protected void onStop() {
        saveViewData();
        super.onStop();
    }

    private void saveViewData() {
        saveDataWithRoom(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceState(this, outState);
    }

    @Nullable
    @Override
    public ViewGroup getContainerLayout() {
        return binding.parentLayout;
    }

    @Nullable
    @Override
    public JsonCast getJsonConfiguration() {
        return null;
    }

    @Override
    public void onViewCreated(@Nullable ViewGroup parentView) {
    }
}
