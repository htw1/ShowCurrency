package com.example.htw.currencyconverter.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.htw.currencyconverter.CurrencyApp;
import com.example.htw.currencyconverter.callback.OnlineChecker;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.network.FixerService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CurrencyViewModel extends ViewModel {

    Context context;
    private MutableLiveData<Currency> currencyData;
    private MutableLiveData<Currency> currencyOldData;
    private FixerService fixerService;
    private SimpleDateFormat stringDataFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String actualData;

    @Inject
    Retrofit api;

    @Inject
    OkHttpClient okHttpClient;

    @Inject
    OnlineChecker onlineChecker;

    public void getCurrencyViewModel  () {
         loadDataFromServer();

    }


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
                if (response.isSuccessful()){
                    actualData = response.body().getDate();
                    currencyData.setValue(response.body());
                }



            }

            @Override
            public void onFailure(@NonNull Call<Currency> call, @NonNull Throwable t) {
                Log.w("Retro_onFailure", t.getMessage());
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

                    currencyOldData.setValue(response.body());
                    Log.w("OldList_call", call.toString());
                    Log.w("OldList_response", response.toString());


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
