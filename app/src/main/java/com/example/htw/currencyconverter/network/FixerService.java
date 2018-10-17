package com.example.htw.currencyconverter.network;

import com.example.htw.currencyconverter.model.Currency;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FixerService {

    String API_FIXER_IO_URL = "http://data.fixer.io/api/";

    @GET("latest?access_key=9a56b78f60c20614986266c8bce163f7")
    Call<Currency>  getProjectList();

    @GET("{data}?access_key=9a56b78f60c20614986266c8bce163f7")
    Call<Currency> getOldProjectList(@Path(value = "data", encoded = true) String data);

}



