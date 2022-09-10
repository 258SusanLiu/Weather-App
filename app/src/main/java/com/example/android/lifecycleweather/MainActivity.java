package com.example.android.lifecycleweather;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.lifecycleweather.data.WeatherPreferences;
import com.example.android.lifecycleweather.utils.NetworkUtils;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnForecastItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView forecastLocationTV;
    private RecyclerView forecastItemRV;
    private ProgressBar loadingIndicatorPB;
    private TextView loadingErrorMessageTV;
    private ForecastAdapter forecastAdapterFA;
    private ForecastViewModel forecastViewModelFVM;
    private SharedPreferences preferancesSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);

        preferancesSP = PreferenceManager.getDefaultSharedPreferences(this);

        WeatherPreferences.setLocation(preferancesSP.getString(getString(R.string.prefLocationKey), getString(R.string.prefLocationDefault)));
        WeatherPreferences.setUnits(preferancesSP.getString(getString(R.string.prefTempUnitKey), getString(R.string.prefTempUnitsDefault)));

        forecastLocationTV = findViewById(R.id.tvForecastLocation);
        forecastLocationTV.setText(WeatherPreferences.getLocation());

        loadingIndicatorPB = findViewById(R.id.pbLoadingIndicator);
        loadingErrorMessageTV = findViewById(R.id.tvLoadingErrorMessage);
        forecastItemRV = findViewById(R.id.rvForecastItems);

        forecastAdapterFA = new ForecastAdapter(this);
        forecastItemRV.setAdapter(forecastAdapterFA);
        forecastItemRV.setLayoutManager(new LinearLayoutManager(this));
        forecastItemRV.setHasFixedSize(true);

        forecastViewModelFVM = ViewModelProviders.of(this, new ViewModelFactory(loadingIndicatorPB, getURL())).get(ForecastViewModel.class);
        forecastViewModelFVM.getSearchResults().observe(this, new Observer<String>(){
            @Override
            public void onChanged(@Nullable String s){
                loadingIndicatorPB.setVisibility(View.INVISIBLE);
                if(s != null){
                    loadingErrorMessageTV.setVisibility(View.INVISIBLE);
                    forecastItemRV.setVisibility(View.VISIBLE);
                    ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(s);
                    forecastAdapterFA.updateForecastItems(forecastItems);
                }
                else{
                    forecastItemRV.setVisibility(View.INVISIBLE);
                    loadingErrorMessageTV.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        forecastViewModelFVM.updateURL(getURL());
        forecastLocationTV.setText(WeatherPreferences.getLocation());
    }

    @Override
    public void onForecastItemClick(OpenWeatherMapUtils.ForecastItem forecastItem){
        Intent intent = new Intent(this, ForecastItemDetailActivity.class);
        intent.putExtra(OpenWeatherMapUtils.extraForecastItem, forecastItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.actionLocation){
            showForecastLocation();
            return true;
        }
        else if(item.getItemId() == R.id.actionSettings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    public String getURL(){
        String location = preferancesSP.getString(getString(R.string.prefLocationKey), getString(R.string.prefLocationDefault));
        String units = preferancesSP.getString(getString(R.string.prefTempUnitKey), getString(R.string.prefTempUnitsDefault));
        WeatherPreferences.setLocation(location);
        WeatherPreferences.setUnits(units);

        String openWeatherMapForecastURL = OpenWeatherMapUtils.buildForecastURL(
            location,
            units
        );
        return openWeatherMapForecastURL;
    }


    public void showForecastLocation() {
        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
            .appendQueryParameter("q", WeatherPreferences.getLocation()).build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    class OpenWeatherMapForecastTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            loadingIndicatorPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params){
            String openWeatherMapURL = params[0];
            String forecastJSON = null;
            try{
                forecastJSON = NetworkUtils.doHTTPGet(openWeatherMapURL);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return forecastJSON;
        }

        @Override
        protected void onPostExecute(String forecastJSON){
            loadingIndicatorPB.setVisibility(View.INVISIBLE);
            if(forecastJSON != null){
                loadingErrorMessageTV.setVisibility(View.INVISIBLE);
                forecastItemRV.setVisibility(View.VISIBLE);
                ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(forecastJSON);
                forecastAdapterFA.updateForecastItems(forecastItems);
            }
            else{
                forecastItemRV.setVisibility(View.INVISIBLE);
                loadingErrorMessageTV.setVisibility(View.VISIBLE);
            }
        }
    }
}
