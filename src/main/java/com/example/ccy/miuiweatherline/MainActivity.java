package com.example.ccy.miuiweatherline;

import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MiuiWeatherView weatherView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        weatherView = (MiuiWeatherView) findViewById(R.id.weather);

        List<WeatherBean> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            WeatherBean b = new WeatherBean(1,20,new Date(System.currentTimeMillis()));
        }
        weatherView.setData(data);
    }
}
