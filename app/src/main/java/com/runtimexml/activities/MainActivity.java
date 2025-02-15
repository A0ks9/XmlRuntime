package com.runtimexml.activities;

import static com.runtimexml.utils.FileHelper.convertXmlToJson;
import static com.runtimexml.utils.FileHelper.getFileNameFromUri;

import android.Manifest;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.runtimexml.databinding.ActivityMainBinding;
import com.runtimexml.utils.DynamicLayoutInflation;
import com.runtimexml.viewModel.MainViewModel;

import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String ParsedXml;
    private Uri createdFileUri;
    private boolean selectFile = true, createdFile = false;
    private final ActivityResultLauncher<String[]> openDocument = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
        if (result != null) {
            MainViewModel.setSelectedFile(result); // Handle the selected file
            selectFile = !selectFile;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.runtimexml.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);

        final ActivityResultLauncher<String> createDocument = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
            if (uri != null) writeToFile(uri, ParsedXml, mainViewModel);
        });
        final ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), binding.XmlParserButton::setActivated);
        // Request permission to read external storage
        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        binding.XmlParserButton.setOnClickListener(v -> {
            if (selectFile) openDocument.launch(new String[]{"application/xml", "text/xml"});
            else if (!createdFile) {
                Uri uri = mainViewModel.getSelectedFile().getValue();
                assert uri != null;
                ParsedXml = convertXmlToJson(getContentResolver(), uri);
                createDocument.launch(Objects.requireNonNull(getFileNameFromUri(uri, getContentResolver())).replace(".xml", ".json"));
            }
        });

        binding.showXml.setOnClickListener(v -> {
            View view = DynamicLayoutInflation.inflateJson(this, createdFileUri, binding.parentLayout);
            DynamicLayoutInflation.setDelegate(view, getApplicationContext());
            assert view != null;
            view.post(() -> android.util.Log.d("MainActivity", "Inflated view: " + view));
        });
    }

    private void writeToFile(Uri uri, String content, MainViewModel mainViewModel) {
        try {
            ContentResolver resolver = getContentResolver();
            OutputStream outputStream = resolver.openOutputStream(uri);

            if (outputStream != null) {
                outputStream.write(content.getBytes());
                outputStream.close();
                Log.d("CreateFileActivity", "Content written successfully.");
                createdFileUri = uri;
                createdFile = true;
                mainViewModel.EnableShowingButton(true);
            }
        } catch (Exception e) {
            Log.e("CreateFileActivity", "Error writing to file", e);
        }
    }
}