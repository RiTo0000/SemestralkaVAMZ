package com.example.covidpass20.ui.os_udaje;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OsUdajeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OsUdajeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is os. udaje fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}