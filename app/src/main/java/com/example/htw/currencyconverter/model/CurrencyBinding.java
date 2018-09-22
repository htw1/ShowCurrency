package com.example.htw.currencyconverter.model;

public class CurrencyBinding extends CurrencyItemsList{

    public CurrencyBinding() {
    }

    private String name;
    private Double value;

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

    public CurrencyBinding(String name, Double value){
        this.name = name;
        this.value = value;
    }
}


