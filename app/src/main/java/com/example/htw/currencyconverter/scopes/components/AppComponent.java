package com.example.htw.currencyconverter.scopes.components;

import com.example.htw.currencyconverter.scopes.modules.PlatformModule;
import com.example.htw.currencyconverter.ui.MainListFragment;
import com.example.htw.currencyconverter.viewmodel.CurrencyViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PlatformModule.class})
public interface AppComponent {
    void inject(CurrencyViewModel viewModel);
}
