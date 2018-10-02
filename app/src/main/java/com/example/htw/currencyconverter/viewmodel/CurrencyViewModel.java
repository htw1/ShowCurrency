package com.example.htw.currencyconverter.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.htw.currencyconverter.CurrencyApp;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.network.FixerService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CurrencyViewModel extends ViewModel {



    private MutableLiveData<Currency> currencyData;
    private MutableLiveData<Currency> currencyOldData;

    public ObservableField<String> getServerDate() {

        return serverDate;
    }

    ObservableField<String> serverDate;
    private FixerService fixerService;
    private SimpleDateFormat stringDataFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String actualData;

    @Inject
    Retrofit api;

    public CurrencyViewModel() {
        CurrencyApp.appComponent.inject(this);
        fixerService = api.create(FixerService.class);
        loadDataFromServer();
    }

    public LiveData<Currency> getOldCurrency()
    {
        if (currencyOldData == null) {
            currencyOldData = new MutableLiveData<>();
        }
        return currencyOldData;
    }

    public LiveData<Currency> getActualCurrency()
    {
        if (currencyData == null) {
            currencyData = new MutableLiveData<>();
        }
        return currencyData;
    }



    private void loadDataFromServer(){
        fixerService.getProjectList().enqueue(new Callback<Currency>() {
            @Override
            public void onResponse(@NonNull Call<Currency> call, @NonNull Response<Currency> response) {
                Log.w("currentList_response", response.toString());
                serverDate =  new ObservableField<>(response.body().getDate());
                actualData = response.body().getDate();
                currencyData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Currency> call, @NonNull Throwable t) {
                Log.w("onFailure", call.toString());
                currencyData.setValue(null);
            }
        });
    }

    public void loadOldDataFromServer(){
        actualData = decrementDate();
        if(actualData != null) {
            fixerService.getOldProjectList(actualData).enqueue(new Callback<Currency>() {
                @Override
                public void onResponse(@NonNull Call<Currency> call, @NonNull Response<Currency> response) {
                    Log.w("OldList_call", call.toString());
                    Log.w("OldList_response", response.toString());
                    currencyOldData.setValue(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<Currency> call, @NonNull Throwable t) {
                    Log.w("OldList_onFailure_call", call.toString());
                    Log.w("OldList_onFailure", t.toString());
                    currencyOldData.setValue(null);
                }
            });
        }
    }



    private String decrementDate() {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(stringDataFormat.parse(actualData));
            c.add(Calendar.DATE, -1);
            return  stringDataFormat.format(c.getTime());
        }catch (ParseException e){
            return null;
        }
    }


}
