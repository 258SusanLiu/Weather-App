package com.example.android.lifecycleweather.utils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils{

    private static final OkHttpClient httpClient = new OkHttpClient();

    public static String doHTTPGet(String url) throws IOException{
        Request calling = new Request.Builder().url(url).build();
        Response receiving = httpClient.newCall(calling).execute();

        try{
            return receiving.body().string();
        }
        finally{
            receiving.close();
        }
    }
}
