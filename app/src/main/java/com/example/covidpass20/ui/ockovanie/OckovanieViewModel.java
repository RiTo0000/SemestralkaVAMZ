package com.example.covidpass20.ui.ockovanie;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OckovanieViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OckovanieViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ockovanie fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}