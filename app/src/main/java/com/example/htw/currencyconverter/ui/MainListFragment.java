package com.example.htw.currencyconverter.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.htw.currencyconverter.R;
import com.example.htw.currencyconverter.adapter.CurrencyRecyclerAdapter;
import com.example.htw.currencyconverter.callback.ClickCallback;
import com.example.htw.currencyconverter.databinding.MainFragment;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.model.CurrencyBinding;
import com.example.htw.currencyconverter.viewmodel.CurrencyViewModel;

public class MainListFragment extends Fragment implements LifecycleOwner {

    public static final String KEY_MAIN_FRAGMENT_ID = "MainListFragment";
    MainFragment binding;




    CurrencyRecyclerAdapter currencyRecyclerAdapter;
    private LifecycleRegistry mLifecycleRegistry;
    private CurrencyViewModel viewModel;
    private Boolean isLoadingActive = false;

    public MainListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_currency_main_list, container, false);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);


        currencyRecyclerAdapter = new CurrencyRecyclerAdapter(clickCallback);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerView.setAdapter(currencyRecyclerAdapter);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(!isLoadingActive) {
                        isLoadingActive = true;
                        viewModel.loadOldDataFromServer();
                    }
                }
            }
        });

        // Create and set the adapter for the RecyclerView.
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders
                .of(getActivity())
                .get(CurrencyViewModel.class);

        observeViewModel(viewModel);

    }

    private void observeViewModel(CurrencyViewModel currencyViewModel) {

        currencyViewModel.getActualCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {
                currencyRecyclerAdapter.setProjectList(currency);
            }
        });

        currencyViewModel.getOldCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {
                currencyRecyclerAdapter.updateProjectList(currency);
                isLoadingActive = false;
            }
        });
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }



    private final ClickCallback clickCallback = new ClickCallback() {
        @Override
        public void onClick(CurrencyBinding element) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ((MainActivity) getActivity()).showDeatail(element);

            }
        }
    };







}
