package com.example.android.lifecycleweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.lifecycleweather.data.WeatherPreferences;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

import java.text.DateFormat;
import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastItemViewHolder>{

    private ArrayList<OpenWeatherMapUtils.ForecastItem> forecastingItems;
    private OnForecastItemClickListener forecastingItemClickListener;

    public interface OnForecastItemClickListener{
        void onForecastItemClick(OpenWeatherMapUtils.ForecastItem forecastItem);
    }

    public ForecastAdapter(OnForecastItemClickListener clickListener){
        forecastingItemClickListener = clickListener;
    }

    public void updateForecastItems(ArrayList<OpenWeatherMapUtils.ForecastItem> forecastItems){
        forecastingItems = forecastItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        if (forecastingItems != null){
            return forecastingItems.size();
        }
        else{
            return 0;
        }
    }

    @Override
    public ForecastItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ForecastItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForecastItemViewHolder holder, int position){
        holder.bind(forecastingItems.get(position));
    }

    class ForecastItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView forecastDateTV;
        private TextView forecastTempDescriptionTV;
        private ImageView weatherIconIV;

        public ForecastItemViewHolder(View itemView){
            super(itemView);
            forecastDateTV = itemView.findViewById(R.id.tvForecastDate);
            forecastTempDescriptionTV = itemView.findViewById(R.id.tvForecastTempDescription);
            weatherIconIV = itemView.findViewById(R.id.ivWeatherIcon);
            itemView.setOnClickListener(this);
        }

        public void bind(OpenWeatherMapUtils.ForecastItem forecastItem){
            String dateString = DateFormat.getDateTimeInstance().format(forecastItem.dateTime);
            String detailString = forecastTempDescriptionTV.getContext().getString(
                    R.string.forecastItemDetails, forecastItem.temperature,
                    WeatherPreferences.getTemperatureUnitsAbbr(), forecastItem.description
            );
            String iconURL = OpenWeatherMapUtils.buildIconURL(forecastItem.icon);
            forecastDateTV.setText(dateString);
            forecastTempDescriptionTV.setText(detailString);
            Glide.with(weatherIconIV.getContext()).load(iconURL).into(weatherIconIV);
        }

        @Override
        public void onClick(View v){
            OpenWeatherMapUtils.ForecastItem forecastItem = forecastingItems.get(getAdapterPosition());
            forecastingItemClickListener.onForecastItemClick(forecastItem);
        }
    }
}
