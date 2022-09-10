package com.example.android.lifecycleweather.utils;

import android.net.Uri;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class OpenWeatherMapUtils{

    public static final String extraForecastItem = "com.example.android.lifecycleweather.utils.ForecastItem";

    private final static String baseURL = "https://api.openweathermap.org/data/2.5/forecast";
    private final static String urlString = "https://openweathermap.org/img/w/%s.png";
    private final static String param = "q";
    private final static String units = "units";
    private final static String appidParam = "appid";
    private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private final static String timeZone = "UTC";

    //Set your own APPID here.
    private final static String forecastAPPID = "2e8e98544dbfc47517daaf9b18e8fcf9";

    //This class is used as a final representation of a single forecast item. It condenses the classes below that are used for parsing the OWN JSON response with Gson.
    public static class ForecastItem implements Serializable{
        public Date dateTime;
        public String description;
        public String icon;
        public long temperature;
        public long temperatureLow;
        public long temperatureHigh;
        public long humidity;
        public long windSpeed;
        public String windDirection;
    }

    //The below several classes are used only for JSON parsing with Gson.
    static class OWMForecastResults{
        public OWMForecastListItem[] list;
    }

    static class OWMForecastListItem{
        public String dt_txt;
        public OWMForecastItemMain main;
        public OWMForecastItemWeather[] weather;
        public OWMForecastItemWind wind;
    }

    static class OWMForecastItemMain{
        public float temp;
        public float temp_min;
        public float temp_max;
        public float humidity;
    }

    static class OWMForecastItemWeather{
        public String description;
        public String icon;
    }

    static class OWMForecastItemWind{
        public float speed;
        public float deg;
    }

    public static String buildForecastURL(String forecastLocation, String temperatureUnits){
        return Uri.parse(baseURL).buildUpon()
            .appendQueryParameter(param, forecastLocation)
            .appendQueryParameter(units, temperatureUnits)
            .appendQueryParameter(appidParam, forecastAPPID)
            .build().toString();
    }

    public static String buildIconURL(String icon){
        return String.format(urlString, icon);
    }

    public static ArrayList<ForecastItem> parseForecastJSON(String forecastJSON){
        Gson gson = new Gson();
        OWMForecastResults results = gson.fromJson(forecastJSON, OWMForecastResults.class);
        if (results != null && results.list != null) {
            ArrayList<ForecastItem> forecastItems = new ArrayList<>();
            SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
            dateParser.setTimeZone(TimeZone.getTimeZone(timeZone));

            //Loop through all results parsed from JSON and condense each one into one single-level ForecastItem object.
            for (OWMForecastListItem listItem : results.list){
                ForecastItem forecastItem = new ForecastItem();

                try{
                    forecastItem.dateTime = dateParser.parse(listItem.dt_txt);
                }
                catch (ParseException e){
                    e.printStackTrace();
                    forecastItem.dateTime = null;
                }

                forecastItem.description = listItem.weather[0].description;
                forecastItem.icon = listItem.weather[0].icon;

                forecastItem.temperature = Math.round(listItem.main.temp);
                forecastItem.temperatureHigh = Math.round(listItem.main.temp_max);
                forecastItem.temperatureLow = Math.round(listItem.main.temp_min);
                forecastItem.humidity = Math.round(listItem.main.humidity);

                forecastItem.windSpeed = Math.round(listItem.wind.speed);
                forecastItem.windDirection = windAngleToDirection(listItem.wind.deg);

                forecastItems.add(forecastItem);
            }

            return forecastItems;
        }
        else {
            return null;
        }
    }

    public static String windAngleToDirection(double angleDegrees){
        if (angleDegrees >= 0 && angleDegrees < 11.25){
            return "N";
        }
        else if(angleDegrees >= 11.25 && angleDegrees < 33.75){
            return "NNE";
        }
        else if(angleDegrees >= 33.75 && angleDegrees < 56.25){
            return "NE";
        }
        else if(angleDegrees >= 56.25 && angleDegrees < 78.75){
            return "ENE";
        }
        else if(angleDegrees >= 78.75 && angleDegrees < 101.25){
            return "E";
        }
        else if(angleDegrees >= 101.25 && angleDegrees < 123.75){
            return "ESE";
        }
        else if(angleDegrees >= 123.75 && angleDegrees < 146.25){
            return "SE";
        }
        else if(angleDegrees >= 146.25 && angleDegrees < 168.75){
            return "SSE";
        }
        else if(angleDegrees >= 168.75 && angleDegrees < 191.25){
            return "S";
        }
        else if(angleDegrees >= 191.25 && angleDegrees < 213.75){
            return "SSW";
        }
        else if(angleDegrees >= 213.75 && angleDegrees < 236.25){
            return "SW";
        }
        else if(angleDegrees >= 236.25 && angleDegrees < 258.75){
            return "WSW";
        }
        else if(angleDegrees >= 258.75 && angleDegrees < 281.25){
            return "W";
        }
        else if(angleDegrees >= 281.25 && angleDegrees < 303.75){
            return "WNW";
        }
        else if(angleDegrees >= 303.75 && angleDegrees < 326.25){
            return "WNW";
        }
        else if(angleDegrees >= 326.25 && angleDegrees < 348.75){
            return "NNW";
        }
        else{
            return "N";
        }
    }
}
