package com.example.htw.currencyconverter.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Currency {

    @SerializedName("base")
    @Expose
    private String base;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("rates")
    @Expose
    private Map<String,Double> rates;

    public Currency(String base, String date, Map<String,Double> rates){
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Map<String,Double> getRates() {
        return rates;
    }

    public double getRate(String code) {
        return rates.get(code);
    }
}
