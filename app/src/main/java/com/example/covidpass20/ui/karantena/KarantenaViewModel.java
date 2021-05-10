package com.example.covidpass20.ui.karantena;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KarantenaViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public KarantenaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is karantena fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}