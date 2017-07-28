package com.example.ccy.miuiweatherline;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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


    public int weather;
    public int temperature;
    public Date date;

    public WeatherBean(int weather, int temperature, Date date) {
        this.weather = weather;
        this.temperature = temperature;
        this.date = date;
    }

//    /**
//     * 判断是否是整点时分
//     * @param date
//     * @return
//     */
//    public static boolean isAtHour(Date date){
//        SimpleDateFormat f = new SimpleDateFormat("mmss");
//        String time = f.format(date);
//        return "0000".equals(f);
//    }

}
