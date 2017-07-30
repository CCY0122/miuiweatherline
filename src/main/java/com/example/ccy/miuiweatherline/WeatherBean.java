package com.example.ccy.miuiweatherline;

/**
 * Created by ccy on 2017-07-28.
 */

public class WeatherBean {

    public static final int SUN = 1; //晴
    public static final int CLOUDY =2; //阴
    public static final int SNOW = 3; //雪
    public static final int RAIN = 4; //雨
    public static final int SUN_CLOUD = 5; //多云
    public static final int THUNDER = 6; //雷



    public int weather;  //天气，取值为上面6种
    public int temperature; //温度值
    public String temperatureStr; //温度的描述值
    public String time; //时间值

    public WeatherBean(int weather, int temperature,String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.time = time;
//        this.temperatureStr = temperature + "\u0026\u0023\u0031\u0037\u0036\u003b";
        this.temperatureStr = temperature + "°";
    }

    public WeatherBean(int weather, int temperature, String temperatureStr, String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.temperatureStr = temperatureStr;
        this.time = time;
    }




}
