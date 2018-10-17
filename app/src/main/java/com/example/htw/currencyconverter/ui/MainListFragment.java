package com.example.htw.currencyconverter.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentFilter;
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

import com.example.htw.currencyconverter.network.FixerService;
import com.example.htw.currencyconverter.receivers.NetworkStateChangeReceiver;
import com.example.htw.currencyconverter.viewmodel.CurrencyViewModel;
import com.example.htw.currencyconverter.network.ConnectivityCheckerUtil.ConnectionLiveData;
import com.example.htw.currencyconverter.network.ConnectivityCheckerUtil.ConnectionModel;

import javax.inject.Inject;

import retrofit2.Retrofit;


public class MainListFragment extends Fragment implements LifecycleOwner {

    @Inject
    OnlineChecker onlineChecker;

    @Inject
    Retrofit api;

    private FixerService fixerService;
    public static final String KEY_MAIN_FRAGMENT_ID = "MainListFragment";
    MainFragment binding;
    private RecyclerView.LayoutManager mLayoutManager;
    CurrencyRecyclerAdapter currencyRecyclerAdapter;
    private LifecycleRegistry mLifecycleRegistry;
    private CurrencyViewModel viewModel;
    private Boolean isLoadingActive = false;

    public static final int MobileData = 2;
    public static final int WifiData = 1;

    boolean connectionState;

    public MainListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getContext());

        connectionLiveData.observe(getActivity(), new Observer<ConnectionModel>() {
            @Override
            public void onChanged(@Nullable ConnectionModel connection) {
                connectionState = connection.getIsConnected();


                /* every time connection state changes, we'll be notified and can perform action accordingly */
                if (connection.getIsConnected()) {
                    switch (connection.getType()) {
                        case WifiData:
                            viewModel.getCurrencyViewModel();
                            if(connectionState==false)
                            Toast.makeText(getActivity(), String.format("Wifi turned ON"), Toast.LENGTH_SHORT).show();
                            break;
                        case MobileData:
                            viewModel.getCurrencyViewModel();
                            if(connectionState==false)
                            Toast.makeText(getActivity(), String.format("Mobile data turned ON"), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    if(connectionState==false)
                    Toast.makeText(getActivity(), String.format("Connection state change"), Toast.LENGTH_SHORT).show();

                }



            }
        });

        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);


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
        // LOADING ACTUAL LIST
        currencyViewModel.getActualCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {

                boolean enableConnection=isNetworkAvailable();
                binding.setIsLoading(false);
                if(currency!=null ||connectionState ==true ){
                    currencyRecyclerAdapter.setProjectList(currency);
                    currencyRecyclerAdapter.notifyDataSetChanged();
                    binding.swiperefresh.setRefreshing(false);
                    binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            binding.swiperefresh.setRefreshing(false);
                        }
                    });
                    //Toast.makeText(getActivity(), "if", Toast.LENGTH_SHORT).show();
                    loadOnlineView();
                    //Toast.makeText(getActivity(), "Internet OK", Toast.LENGTH_SHORT).show();

                    currencyRecyclerAdapter.notifyDataSetChanged();
                    currencyRecyclerAdapter.setProjectList(currency);

                }else{
                    loadOfflineView();
                binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        binding.swiperefresh.setRefreshing(false);
                        Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                }


            }
        });

        // LOADING UPDATED (DATE) LIST
        currencyViewModel.getOldCurrency().observe(getActivity(), new Observer<Currency>() {
            @Override
            public void onChanged(@Nullable Currency currency) {


/*                ProgressBarAnimation anim = new ProgressBarAnimation(binding.progressBar,  0.0f,1.0f);
                anim.setDuration(2000);
                binding.progressBar.startAnimation(anim);*/
                if (currency !=null) {
                    Handler myHandler = new Handler();
                    myHandler.postDelayed(
                            () -> {

                                currencyRecyclerAdapter.notifyDataSetChanged();
                                currencyRecyclerAdapter.updateProjectList(currency);
                                binding.setIsLoading(false);

                                myHandler.postDelayed(
                                        () -> {
                                                Toast.makeText(getActivity(), "New day !", Toast.LENGTH_SHORT).show();
                                        }, 700);

                            }, 1000);

                } // fin if (currency !=null) {
                    isLoadingActive = false;
                    binding.setIsLoading(true);
                    //Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();


/*                            binding.setIsLoading(false);
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                            loadOfflineView();
                            binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    binding.swiperefresh.setRefreshing(false);

                                }
                            });

                        //}

                        currencyRecyclerAdapter.notifyDataSetChanged();
                        //if(currency!=null)
                        currencyRecyclerAdapter.updateProjectList(currency);*/

            }
        });

    }

    private void loadOfflineView() {
        binding.noConnection.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);

    }
    private void loadOnlineView() {
        binding.noConnection.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager=(ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
       // Log.v("NetworkStatus",networkInfo.toString() );
        return (networkInfo !=null && networkInfo.isConnected());

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