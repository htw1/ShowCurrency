package com.example.htw.currencyconverter.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.htw.currencyconverter.model.Currency;

import javax.inject.Inject;

public class DetailViewModel extends AndroidViewModel {
    private static final String TAG = DetailViewModel.class.getName();
    private static final MutableLiveData ABSENT = new MutableLiveData();
    {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    private final LiveData<Currency> projectObservable;
    private final MutableLiveData<String> projectID;

    public ObservableField<Currency> project = new ObservableField<>();

    @Inject
    public DetailViewModel(@NonNull CurrencyViewModel viewModel, @NonNull Application application) {
        super(application);

        this.projectID = new MutableLiveData<>();

        projectObservable = Transformations.switchMap(projectID, input -> {
            if (input.isEmpty()) {
                Log.i(TAG, "DetailViewModel projectID is absent!!!");
                return ABSENT;
            }

            Log.i(TAG,"DetailViewModel projectID is " + projectID.getValue());

            return viewModel.getActualCurrency();
        });
    }

    public LiveData<Currency> getObservableProject() {
        return projectObservable;
    }

    public void setProject(Currency project) {
        this.project.set(project);
    }

    public void setProjectID(String projectID) {
        this.projectID.setValue(projectID);
    }
}
