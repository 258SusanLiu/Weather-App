package com.example.android.lifecycleweather;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.lifecycleweather.data.WeatherPreferences;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

import java.text.DateFormat;

public class ForecastItemDetailActivity extends AppCompatActivity {

    private TextView dateTV;
    private TextView tempDescriptionTV;
    private TextView lowHighTempTV;
    private TextView windTV;
    private TextView humidityTV;
    private ImageView weatherIconTV;

    private OpenWeatherMapUtils.ForecastItem forecastItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_item_detail);

        dateTV = findViewById(R.id.tvDate);
        tempDescriptionTV = findViewById(R.id.tvTempDescription);
        lowHighTempTV = findViewById(R.id.tvLowHighTemp);
        windTV = findViewById(R.id.tvWind);
        humidityTV = findViewById(R.id.tvHumidity);
        weatherIconTV = findViewById(R.id.ivWeatherIcon);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(OpenWeatherMapUtils.extraForecastItem)) {
            forecastItem = (OpenWeatherMapUtils.ForecastItem)intent.getSerializableExtra(
                OpenWeatherMapUtils.extraForecastItem
            );
            fillInLayout(forecastItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.actionShare){
            shareForecast();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void shareForecast() {
        if (forecastItem != null) {
            String dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(forecastItem.dateTime);
            String shareText = getString(R.string.forecastItemShareText,
                WeatherPreferences.getLocation(), dateString,
                forecastItem.temperature, WeatherPreferences.getTemperatureUnitsAbbr(),
                forecastItem.description, forecastItem.temperatureHigh,
                forecastItem.temperatureLow, forecastItem.humidity);
            ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(shareText)
                .setChooserTitle(R.string.shareChooseTitle).startChooser();
        }
    }

    private void fillInLayout(OpenWeatherMapUtils.ForecastItem forecastItem) {
        String dateString = DateFormat.getDateTimeInstance().format(forecastItem.dateTime);
        String detailString = getString(R.string.forecastItemDetails, forecastItem.temperature,
            WeatherPreferences.getTemperatureUnitsAbbr(), forecastItem.description);
        String lowHighTempString = getString(R.string.forecastItemLowHighTemp,
            forecastItem.temperatureLow, forecastItem.temperatureHigh,
            WeatherPreferences.getTemperatureUnitsAbbr());

        String windString = getString(R.string.forecastItemWind, forecastItem.windSpeed,
            forecastItem.windDirection);
        String humidityString = getString(R.string.forecastItemHumidity, forecastItem.humidity);
        String iconURL = OpenWeatherMapUtils.buildIconURL(forecastItem.icon);

        dateTV.setText(dateString);
        tempDescriptionTV.setText(detailString);
        lowHighTempTV.setText(lowHighTempString);
        windTV.setText(windString);
        humidityTV.setText(humidityString);
        Glide.with(this).load(iconURL).into(weatherIconTV);
    }
}
