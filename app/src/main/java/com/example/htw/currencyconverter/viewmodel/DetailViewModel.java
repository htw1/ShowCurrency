package com.example.htw.currencyconverter.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.htw.currencyconverter.model.Currency;

import javax.inject.Inject;

public class DetailViewModel extends ViewModel {

    private MutableLiveData<Currency> currencyData;
    private MutableLiveData<Currency> currencyOldData;


}
