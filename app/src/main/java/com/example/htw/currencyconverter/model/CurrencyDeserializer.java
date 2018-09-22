package com.example.htw.currencyconverter.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CurrencyDeserializer implements JsonDeserializer<Currency> {

    private Gson gson = new Gson();
    private final String BASE_FIELD = "base";
    private final String DATE_FIELD = "date";
    private final String RATES_FIELD = "rates";

    @Override
    public Currency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Currency(
            gson.fromJson(json.getAsJsonObject().get(BASE_FIELD), String.class),
            gson.fromJson(json.getAsJsonObject().get(DATE_FIELD), String.class),
            rates(gson.fromJson(json.getAsJsonObject().get(RATES_FIELD), JsonObject.class))
        );
    }

    private Map<String,Double> rates(@NonNull JsonObject jsonObject){

        HashMap result = new HashMap();

        for(Map.Entry<String,JsonElement> parameter : jsonObject.entrySet()){
            result.put(parameter.getKey(),parameter.getValue().getAsDouble());
        }

        return result;
    }
}
