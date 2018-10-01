package com.example.htw.currencyconverter.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    SimpleDateFormat fromServer = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat myFormat = new SimpleDateFormat("dddd, dd MMMM yyyy");

   public String getDateToFromat  (String reciveDate) throws ParseException {
       Date date = fromServer.parse(reciveDate);
       String outputDateStr =myFormat.format(date);
     return outputDateStr;
   };
}