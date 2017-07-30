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
        WeatherBean b1 = new WeatherBean(1,18,"10:00");
        WeatherBean b2 = new WeatherBean(1,21,"11:00");
        WeatherBean b3 = new WeatherBean(2,20,"日落","12:00");
        WeatherBean b4 = new WeatherBean(3,19,"13:00");
        WeatherBean b5 = new WeatherBean(3,22,"14:00");
        data.add(b1);
        data.add(b2);
        data.add(b3);
        data.add(b4);
        data.add(b5);
        weatherView.setData(data);
    }
}
