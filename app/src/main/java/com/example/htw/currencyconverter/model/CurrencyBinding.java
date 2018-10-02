package com.example.htw.currencyconverter.model;

import java.util.List;

public class CurrencyBinding extends CurrencyItemsList{



    public CurrencyBinding(String name) {
        this.name=name;
    }



    @Override
    public String toString() {
        return "CurrencyBinding{" +
                "name='" + name + '\'' +
                '}';
    }


    private String name;
    private Double value;
    private String date;

    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() { return date; }

    public String getValue() { return value.toString(); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Double value) {
        this.value = value;
    }


    public CurrencyBinding(String name, Double value, String date ){
        this.name = name;
        this.value = value;
        this.date = date;
    }
}


