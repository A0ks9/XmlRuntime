package com.runtimexml.viewModel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public static MutableLiveData<String> buttonText = new MutableLiveData<>("Choose File");
    public static MutableLiveData<String> showXmlText = new MutableLiveData<>("Show Xml");
    public static MutableLiveData<Boolean> enableShowing = new MutableLiveData<>(false);
    public static MutableLiveData<Uri> selectedFile = new MutableLiveData<>();

    public void setShowXmlText(String text) {
        showXmlText.setValue(text);
    }

    public void EnableShowingButton(boolean enable) {
        enableShowing.setValue(enable);
    }

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
