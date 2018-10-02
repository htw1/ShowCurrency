package com.example.htw.currencyconverter.ui;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.htw.currencyconverter.R;
import com.example.htw.currencyconverter.databinding.CurrencyBindingDetailItem;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.model.CurrencyBinding;
import com.example.htw.currencyconverter.model.CurrencyDate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.annotations.NonNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailListFragment extends Fragment implements LifecycleOwner {

    private static final String KEY_VALUE = "name";
    private static final String KEY_DATE ="date" ;

    CurrencyBindingDetailItem binding;
    private LifecycleRegistry mLifecycleRegistry;

    public DetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_list, container, false);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        String stringDateFromBundle = getBundleString(bundle,KEY_DATE,"error" );
        String stringValueFromBundle = getBundleString(bundle,KEY_VALUE,"error");

        //VALUE
        Double doubleBundle = Double.valueOf(stringValueFromBundle);
        binding.setCurrencyBindingItemDetail(new CurrencyBinding(stringValueFromBundle));
        binding.getCurrencyBindingItemDetail().setValue(doubleBundle);
        //DATA
        binding.setCurrencyItemDetailDate(new CurrencyDate(stringDateFromBundle));
        binding.getCurrencyItemDetailDate().setDate(stringDateFromBundle);

    }
    public String getBundleString(Bundle b, String key, String def)
    {
        String value = "";
        b.toString();
        value = b.getString(key);
        //Log.w("getBundleString", value );
        if (value == null)
            value = def;
        return value;
    }

    @BindingAdapter("bindFriendlyServerDate")
    public static void bindServerDate(@NonNull TextView textView, String reciveDate) {

        SimpleDateFormat fromServer = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("EEEE,  d MMMM yyyy");
        Date date = null;

        try {
            date = fromServer.parse(reciveDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr =myFormat.format(date);
        textView.setText(outputDateStr);
    }

    @BindingAdapter("roundingText")
    public static void roundingText (@NonNull TextView textView, String valueSting){

        Double valueDouble = Double.parseDouble(valueSting.replaceAll(" ","."));
        double roundValue = Math.round(valueDouble*100)/100.0d;
        textView.setText(String.valueOf(roundValue));

    }

    public static DetailListFragment  fragmentToFragment (String currentCurrencyStatus, String outputDateStr) {

        String currentCurrencyStatusSave =  currentCurrencyStatus;
        String actualDate = outputDateStr;
        DetailListFragment fragment = new DetailListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_DATE,actualDate);
        args.putString(KEY_VALUE, currentCurrencyStatusSave);
        Log.w("getBundleString", args.toString() );
        fragment.setArguments(args);
        return fragment;
    }



}
