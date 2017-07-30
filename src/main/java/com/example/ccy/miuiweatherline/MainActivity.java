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
        WeatherBean b1 = new WeatherBean(WeatherBean.SUN,18,"04:00");
        WeatherBean b2 = new WeatherBean(WeatherBean.SUN,21,"05:00");
        WeatherBean b3 = new WeatherBean(WeatherBean.CLOUDY,20,"日出","05:20");
        WeatherBean b4 = new WeatherBean(WeatherBean.RAIN,19,"06:00");
        WeatherBean b5 = new WeatherBean(WeatherBean.RAIN,16,"07:00");
        WeatherBean b6 = new WeatherBean(WeatherBean.SUN_CLOUD,18,"123:456");
        WeatherBean b7 = new WeatherBean(WeatherBean.RAIN,20,"13:00");
        WeatherBean b8 = new WeatherBean(WeatherBean.THUNDER,21,"14:00");
        WeatherBean b9 = new WeatherBean(WeatherBean.THUNDER,20,"15:00");
        WeatherBean b10 = new WeatherBean(WeatherBean.THUNDER,20,"16:00");
        WeatherBean b11 = new WeatherBean(WeatherBean.SNOW,18,"17:00");
        data.add(b1);
        data.add(b2);
        data.add(b3);
        data.add(b4);
        data.add(b5);
        data.add(b6);
        data.add(b7);
        data.add(b8);
        data.add(b9);
        data.add(b10);
        data.add(b11);

        weatherView.setData(data);
    }
}
