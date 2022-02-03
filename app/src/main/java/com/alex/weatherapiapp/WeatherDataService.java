package com.alex.weatherapiapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";
    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener) {
        String url = QUERY_FOR_CITY_ID + cityName;
        cityID = "";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject cityInfo = response.getJSONObject(0);
                cityID = cityInfo.getString("woeid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            volleyResponseListener.onResponse(cityID);

        }, error -> volleyResponseListener.onError("Something wrong"));
        RequestSingleton.getInstance(context).addToRequestQueue(request);

    }

    public void getCityForecastByID(String cityID, ForecastByIDResponse forecastByIDResponse) {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                try {
                    JSONArray consolidatedWeatherList = response.getJSONArray("consolidated_weather");
                    for (int i = 0; i < consolidatedWeatherList.length(); i++) {

                        JSONObject firstDayFromAPI = (JSONObject) consolidatedWeatherList.get(i);

                        WeatherReportModel oneDay = new WeatherReportModel(
                                firstDayFromAPI.getInt("id"),
                                firstDayFromAPI.getString("weather_state_name"),
                                firstDayFromAPI.getString("weather_state_abbr"),
                                firstDayFromAPI.getString("wind_direction_compass"),
                                firstDayFromAPI.getString("created"),
                                firstDayFromAPI.getString("applicable_date"),
                                (float) firstDayFromAPI.getDouble("min_temp"),
                                (float) firstDayFromAPI.getDouble("max_temp"),
                                (float) firstDayFromAPI.getDouble("the_temp"),
                                (float) firstDayFromAPI.getDouble("wind_speed"),
                                (float) firstDayFromAPI.getDouble("wind_direction"),
                                firstDayFromAPI.getInt("air_pressure"),
                                firstDayFromAPI.getInt("humidity"),
                                (float) firstDayFromAPI.getDouble("visibility"),
                                firstDayFromAPI.getInt("predictability")
                        );
                        weatherReportModels.add(oneDay);
                    }
                    forecastByIDResponse.onResponse(weatherReportModels);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show());
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback){
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityID) {
                getCityForecastByID(cityID, new ForecastByIDResponse() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }

    public interface ForecastByIDResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public interface GetCityForecastByNameCallback{
        void onError(String message);
        void onResponse(List<WeatherReportModel>weatherReportModels);
    }

}
