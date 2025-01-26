package com.runtimexml.viewModel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public static MutableLiveData<String> buttonText = new MutableLiveData<>("Choose File");
    public static MutableLiveData<Uri> selectedFile = new MutableLiveData<>();

    public MutableLiveData<String> getButtonText() {
        return buttonText;
    }

    public LiveData<Uri> getSelectedFile() {
        return selectedFile;
    }

    public static void setSelectedFile(@NonNull Uri file) {
        selectedFile.setValue(file);
        buttonText.setValue("Convert File");
    }
}
