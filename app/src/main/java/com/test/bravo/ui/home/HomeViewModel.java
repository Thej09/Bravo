package com.test.bravo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.text.Html;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Spanned> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();

        mText.setValue(Html.fromHtml("Place Holder", Html.FROM_HTML_MODE_LEGACY));
    }

    public LiveData<Spanned> getText() {
        return mText;
    }


}