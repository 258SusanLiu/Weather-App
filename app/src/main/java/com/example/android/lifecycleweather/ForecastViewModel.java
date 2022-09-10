package com.example.android.lifecycleweather;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.lifecycleweather.utils.NetworkUtils;
import java.io.IOException;

public class ForecastViewModel extends ViewModel {

    private final static String TAG = ForecastViewModel.class.getSimpleName();
    private MutableLiveData<String> JSONSearchResults;
    private String forecastURL;
    private ProgressBar forecastLoadingIndicatorPB;

    public ForecastViewModel(ProgressBar loadingIndicatorPB, String url){
        JSONSearchResults = new MutableLiveData<String>();
        forecastURL = url;
        forecastLoadingIndicatorPB = loadingIndicatorPB;
        loadSearchResults();
    }

    private void loadSearchResults(){
        new AsyncTask<Void, Void, String>(){
            protected void onPreExecute(){
                super.onPreExecute();
                forecastLoadingIndicatorPB.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... voids){
                String forecastJSON = null;
                try{
                    forecastJSON = NetworkUtils.doHTTPGet(forecastURL);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                return forecastJSON;
            }

            @Override
            protected void onPostExecute(String forecastJSON){
                JSONSearchResults.setValue(forecastJSON);
            }
        }.execute();
    }

    public void updateURL(String url){
        if(!forecastURL.equals(url)){
            Log.d(TAG, "updateURL: i dont get this at all");
            forecastURL = url;
            loadSearchResults();
        }
        else{
            if(JSONSearchResults.getValue() == ""){
                loadSearchResults();
            }
        }
    }
    public LiveData<String> getSearchResults(){
        return JSONSearchResults;
    }
}
