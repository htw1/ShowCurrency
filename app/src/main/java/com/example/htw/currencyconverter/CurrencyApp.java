package com.example.htw.currencyconverter;

import android.app.Application;

import com.example.htw.currencyconverter.scopes.modules.PlatformModule;
import com.example.htw.currencyconverter.scopes.components.AppComponent;
import com.example.htw.currencyconverter.scopes.components.DaggerAppComponent;

public class CurrencyApp extends Application {

    public static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .platformModule(new PlatformModule(this))
                .build();
    }
}
