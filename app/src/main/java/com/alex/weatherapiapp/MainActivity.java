package com.alex.weatherapiapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_getCityId = findViewById(R.id.btn_getCityId);
        Button btn_getWeatherByID = findViewById(R.id.btn_getWeatherByCityID);
        Button btn_getWeatherByCityName = findViewById(R.id.btn_getWeatherByCityName);
        EditText et_cityName = findViewById(R.id.et_cityName);
        ListView lv_weatherReports = findViewById(R.id.lv_weatherReports);
        WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);


        btn_getCityId.setOnClickListener(view -> {
            weatherDataService.getCityID(et_cityName.getText().toString(), new WeatherDataService.VolleyResponseListener() {
                @Override
                public void onResponse(String cityID) {
                    Toast.makeText(MainActivity.this, "Returned an ID of: " + cityID, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btn_getWeatherByID.setOnClickListener(view -> {
            weatherDataService.getCityForecastByID(et_cityName.getText().toString(), new WeatherDataService.ForecastByIDResponse() {
                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(List<WeatherReportModel> weatherReportModels) {

                    ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
                    lv_weatherReports.setAdapter(arrayAdapter);
                    //Toast.makeText(MainActivity.this, weatherReportModels.toString(), Toast.LENGTH_SHORT).show();

                }
            });

        });
        btn_getWeatherByCityName.setOnClickListener(view -> {
            weatherDataService.getCityForecastByName(et_cityName.getText().toString(), new WeatherDataService.GetCityForecastByNameCallback() {
                @Override
                public void onResponse(List<WeatherReportModel> weatherReportModels) {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
                    lv_weatherReports.setAdapter(arrayAdapter);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}