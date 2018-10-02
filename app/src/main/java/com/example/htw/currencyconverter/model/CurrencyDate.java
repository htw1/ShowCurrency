package com.example.htw.currencyconverter.model;

public class CurrencyDate extends CurrencyItemsList{



    private String date;

    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public CurrencyDate(String date){
        this.date = date;
    }
}