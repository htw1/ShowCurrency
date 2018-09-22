package com.example.htw.currencyconverter.ui;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.example.htw.currencyconverter.R;
import com.example.htw.currencyconverter.model.CurrencyBinding;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("ShowCurrency ");

        }

        if (savedInstanceState == null){

            MainListFragment fragment = new MainListFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, MainListFragment.KEY_MAIN_FRAGMENT_ID )
                    .commit();
        }

    }
    public void showDeatail(CurrencyBinding project) {
        DetailListFragment detailFragment = DetailListFragment.fragmentToFragment(project.getName());

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("project")
                .replace(R.id.fragment_container, detailFragment, null)
                .commit();
    }





}
