package com.example.android.lifecycleweather.data;

import com.example.android.lifecycleweather.R;

public class WeatherPreferences{
    private static String areaLocation = "";
    private static String tempUnits = "";
    private static String tempUnitsAbbr = "";

    public static void setLocation(String location){
        areaLocation = location;
    }

    public static void setUnits(String units){
        tempUnits = units;

        if(tempUnits.equals("imperial")){
            tempUnitsAbbr = "F\u00B0";
        }
        else if(tempUnits.equals("metric")){
            tempUnitsAbbr = "C\u00B0";
        }
        else if(tempUnits.equals("kelvin")){
            tempUnitsAbbr = "K\u00B0";
        }
    }

    public static String getLocation(){
        return areaLocation;
    }

    public static String getTemperatureUnits(){
        return tempUnits;
    }

    public static String getTemperatureUnitsAbbr(){
        return tempUnitsAbbr;
    }
}
