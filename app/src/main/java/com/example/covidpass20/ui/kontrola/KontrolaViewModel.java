package com.example.covidpass20.ui.kontrola;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KontrolaViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public KontrolaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is kontrola fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}