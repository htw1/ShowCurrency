package com.example.htw.currencyconverter.scopes.modules;

import android.content.Context;

import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.network.FixerService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.htw.currencyconverter.model.CurrencyDeserializer;


@Module
public class PlatformModule {

    private final Context appContext;

    public PlatformModule(Context appContext){
        this.appContext = appContext;
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor loggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    public Gson gsonBuilder(){
        return new GsonBuilder()
                .registerTypeAdapter(Currency.class, new CurrencyDeserializer())
                .setLenient()
                .create();
    }


    @Provides
    @Singleton
    public OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit api(OkHttpClient okHttpClient, Gson gson){
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(FixerService.API_FIXER_IO_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
