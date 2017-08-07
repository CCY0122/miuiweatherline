# miuiweatherline
仿小米MIUI8天气24小时预报折线图控件
<br/>源码解析博客地址<br/>
[http://blog.csdn.net/ccy0122/article/details/76464825](http://blog.csdn.net/ccy0122/article/details/76464825)<br/>
<br/>效果预览<br/>
![image1](https://github.com/CCY0122/miuiweatherline/blob/master/someImages/complete1.png)
![image2](https://github.com/CCY0122/miuiweatherline/blob/master/someImages/weatherGif%20_1.gif)
<br/>使用方法：<br/>
xml：<br/>
```
    <com.example.ccy.miuiweatherline.MiuiWeatherView
        android:id="@+id/weather"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:line_interval="60dp" （可选，相邻点距离）
        app:min_point_height="60dp"  （可选，折线最低高度）
        app:background_color="#ffffff"/>  （可选，背景色）
```
<br/>code:<br/>
使用`com.example.ccy.miuiweatherline.WeatherBean`作为元数据:<br/>
 ```
 weatherView = (MiuiWeatherView) findViewById(R.id.weather);
  List<WeatherBean> data = new ArrayList<>();
  //add your WeatherBean to data
  WeatherBean b1 = new WeatherBean(WeatherBean.SUN,20,"05:00");
  WeatherBean b2 = new WeatherBean(WeatherBean.RAIN,22,"日出","05:30");
  //b3、b4...bn
  data.add(b1);
  data.add(b2);
  weatherView.setData(data);
  ```



