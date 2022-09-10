package com.example.android.lifecycleweather;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.ProgressBar;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ProgressBar loadingIndicatorPBV;
    private String urlS;

    public ViewModelFactory(ProgressBar loadingIndicatorPB, String url){
        loadingIndicatorPBV = loadingIndicatorPB;
        urlS = url;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ForecastViewModel(loadingIndicatorPBV, urlS);
    }
}
