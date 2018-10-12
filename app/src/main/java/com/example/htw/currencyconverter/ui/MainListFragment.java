package com.example.htw.currencyconverter.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.htw.currencyconverter.R;
import com.example.htw.currencyconverter.adapter.CurrencyRecyclerAdapter;
import com.example.htw.currencyconverter.callback.ClickCallback;

import com.example.htw.currencyconverter.callback.OnlineChecker;
import com.example.htw.currencyconverter.databinding.MainFragment;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.model.CurrencyBinding;

import com.example.htw.currencyconverter.viewmodel.CurrencyViewModel;

import javax.inject.Inject;


public class MainListFragment extends Fragment implements LifecycleOwner {

    @Inject
    OnlineChecker onlineChecker;


    public static final String KEY_MAIN_FRAGMENT_ID = "MainListFragment";
    MainFragment binding;
    private RecyclerView.LayoutManager mLayoutManager;
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
            mLayoutManager = new LinearLayoutManager(this.getContext());
            currencyRecyclerAdapter = new CurrencyRecyclerAdapter(clickCallback);
            binding.recyclerView.setLayoutManager(mLayoutManager);
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
                binding.setIsLoading(false);

                if(currency!=null){
                    binding.swiperefresh.setRefreshing(false);
                    currencyRecyclerAdapter.setProjectList(currency);
                }else
                binding.recyclerView.setVisibility(View.GONE);
                binding.noConnection.setVisibility(View.VISIBLE);
                binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        boolean connection=isNetworkAvailable();
                        if(connection){
                            if (binding.swiperefresh.isRefreshing()){
                                binding.swiperefresh.setRefreshing(false);
                            }
                            binding.noConnection.setVisibility(View.GONE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            currencyRecyclerAdapter.setProjectList(currency);

                        }else {
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                            binding.swiperefresh.setRefreshing(false);

                            if (connection){
                               /* binding.noConnection.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.VISIBLE);*/
                                currencyRecyclerAdapter.setProjectList(currency);
                            }

                        }



                    }
                });
                if (binding.swiperefresh.isRefreshing()){
                    binding.swiperefresh.setRefreshing(false);
                }


            }
        });


        currencyViewModel.getOldCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {
                binding.setIsLoading(true);
/*                ProgressBarAnimation anim = new ProgressBarAnimation(binding.progressBar,  0.0f,1.0f);
                anim.setDuration(2000);
                binding.progressBar.startAnimation(anim);*/
                Handler myHandler = new Handler();
                myHandler.postDelayed(
                        ()-> {
                            currencyRecyclerAdapter.notifyDataSetChanged();
                            currencyRecyclerAdapter.updateProjectList(currency);
                            binding.setIsLoading(false);

                            myHandler.postDelayed(
                                    ()->{Toast.makeText(getActivity(), "New day !", Toast.LENGTH_SHORT).show();
                                    },700);

                        },1000);

                isLoadingActive = false;
            }
        });
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager=(ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null;

    }

    public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

    private final ClickCallback clickCallback = new ClickCallback() {

        @Override
        public void onClick(CurrencyBinding currencyName) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ((MainActivity) getActivity()).showDeatail(currencyName);

            }
        }
    };
    @BindingAdapter("visibleGone")
        public static void showHide(View view, boolean show) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }

}