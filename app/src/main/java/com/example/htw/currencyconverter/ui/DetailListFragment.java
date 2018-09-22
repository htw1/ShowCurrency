package com.example.htw.currencyconverter.ui;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.htw.currencyconverter.R;
import com.example.htw.currencyconverter.databinding.CurrencyBindingDetailItem;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.viewmodel.CurrencyViewModel;
import com.example.htw.currencyconverter.viewmodel.DetailViewModel;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailListFragment extends Fragment implements LifecycleOwner {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private static final String KEY_DETAIL_ID = "project_id";
    CurrencyBindingDetailItem binding;
    private LifecycleRegistry mLifecycleRegistry;
    private CurrencyViewModel viewModel;


    public DetailListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_currency_main_list, container, false);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final DetailViewModel viewModel1 = ViewModelProviders.of(this, viewModelFactory)
                .get(DetailViewModel.class);

        viewModel1.setProjectID(getArguments().getString(KEY_DETAIL_ID));

        viewModel = ViewModelProviders
                .of(getActivity())
                .get(CurrencyViewModel.class);

        observeViewModel(viewModel);
        observeViewModel1(viewModel1);


    }

    private void observeViewModel1(final DetailViewModel viewModel1) {
        // Observe project data
        viewModel.getActualCurrency().observe(this, new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {
                viewModel1.setProject(currency);
            }
        });
    }

    private void observeViewModel(CurrencyViewModel currencyViewModel) {

        currencyViewModel.getActualCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {

            }
        });
    }

    public static DetailListFragment  fragmentToFragment (String projectID) {
        DetailListFragment fragment = new DetailListFragment();
        Bundle args = new Bundle();

        args.putString(KEY_DETAIL_ID, projectID);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

}
