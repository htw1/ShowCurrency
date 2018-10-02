package com.example.htw.currencyconverter.ui;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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

import com.example.htw.currencyconverter.viewmodel.CurrencyViewModel;

import io.reactivex.annotations.NonNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailListFragment extends Fragment implements LifecycleOwner {

    private static final String KEY = "name";
    CurrencyBindingDetailItem binding;
    private LifecycleRegistry mLifecycleRegistry;
    private CurrencyViewModel viewModel;
    String outputDateStr;

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
        String stringFromBundle = getBundleString(bundle,KEY,"default");
        Double doubleBundle = Double.valueOf(stringFromBundle);
        binding.setCurrencyBindingItemDetail(new CurrencyBinding(stringFromBundle));
        binding.getCurrencyBindingItemDetail().setValue(doubleBundle);

        viewModel = ViewModelProviders
                .of(getActivity())
                .get(CurrencyViewModel.class);
        observeViewModelDate(viewModel);


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

    private void observeViewModelDate(CurrencyViewModel currencyViewModel) {
        currencyViewModel.getOldCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {
                binding.setCurrencyItemDetailDate(new CurrencyDate(currency.getDate()));
                bindServerDate(binding.dateText,currency.getDate());

            }
        });

    }

    @BindingAdapter("bindFriendlyServerDate")
    public static void bindServerDate(@NonNull TextView textView, String reciveDate) {
        /*Parse string data and set it in another format for your textView*/

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





    public static DetailListFragment  fragmentToFragment (String currentCurrencyStatus) {
        String currentCurrencyStatusSave =  currentCurrencyStatus.toString();
        DetailListFragment fragment = new DetailListFragment();
        Bundle args = new Bundle();
        args.putString(KEY, currentCurrencyStatusSave);
        //Log.w("getBundleString", args.toString() );
        fragment.setArguments(args);
        return fragment;
    }



}
