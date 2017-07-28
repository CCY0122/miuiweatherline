package com.example.ccy.miuiweatherline;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ccy on 2017-07-28.
 */

public class MiuiWeatherView extends View {

    private static int DEFAULT_BULE= 0XFF00BFFF;
    private static int DEFAULT_GRAY = Color.GRAY;

    private int minViewHeight; //控件最低高度
    private int minPointHeight;//折线最低高度
    private int lineInterval; //折线线段长度
    private float pointRadius; //折线点的半径
    private float textSize; //字体大小
    private float pointGap; //折线单位高度差
    private int defaultPadding; //折线起始点默认的偏移

    private int viewHeight;
    private int viewWidth;
    private int screenWidth;
    private int screenHeight;

    private Paint linePaint; //线画笔
    private Paint textPaint; //文字画笔

    private List<WeatherBean> data = new ArrayList<>(); //元数据
    private List<Map<Integer,Integer>> weatherDatas = new ArrayList<>();  //对元数据中天气分组后的集合
    private int maxTemperature;//元数据中的最高和最低温度
    private int minTemperature;



    public MiuiWeatherView(Context context) {
        this(context, null);
    }

    public MiuiWeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiuiWeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setLayerType(View.LAYER_TYPE_SOFTWARE,null); //关硬件加速

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.MiuiWeatherView);
        minPointHeight = (int) ta.getDimension(R.styleable.MiuiWeatherView_min_point_height,sp2pxF(context,50));
        lineInterval = (int) ta.getDimension(R.styleable.MiuiWeatherView_line_interval,sp2pxF(context,50));
        ta.recycle();

        initSize(context);

        initPaint(context);

    }

    /**
     * 初始化默认数据
     */
    private void initSize(Context c){
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        minViewHeight = 3 * minPointHeight;
        pointRadius = dp2pxF(c,2);
        textSize = sp2pxF(c,10);
        defaultPadding = (1/2) * minPointHeight;

        Log.d("ccy","minPointHeight = "+minPointHeight);
        calculatePontGap();
    }

    /**
     * 计算折线单位高度差
     */
    private void calculatePontGap(){
        int lastMaxTem = -100000;
        int lastMinTem = 100000;
        for(WeatherBean bean : data){
            if(bean.temperature > lastMaxTem){
                maxTemperature = bean.temperature;
                lastMaxTem = bean.temperature;
            }
            if(bean.temperature < lastMinTem){
                minPointHeight = bean.temperature;
                lastMinTem = bean.temperature;
            }
        }
        pointGap = (maxTemperature - minTemperature)*1.0f / (viewHeight - minPointHeight - 2*defaultPadding);
        Log.d("ccy","pointGap = " + pointGap);
    }

    private void initPaint(Context c){
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(dp2px(c,1));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize(getContext());
    }

    /**
     * 唯一公开方法，用于设置元数据
     * @param data
     */
    public void setData(List<WeatherBean> data){
        if(data == null || data.isEmpty()){
            return;
        }
        this.data = data;
        calculatePontGap();
        initWeatherMap();

        requestLayout();
        invalidate();
    }

    /**
     * 根据元数据中连续相同的天气数做分组,
     * key为连续相同天气的天数，values为对应天气
     */
    private void initWeatherMap() {
        int lastWeather = -1;
        int count = 0;
        for(WeatherBean bean : data){
            if(lastWeather == -1){
                lastWeather = bean.weather;
            }

            if(bean.weather != lastWeather){
               weatherMap.put(count,lastWeather);
                count = 1;
            }else {
                count++;
            }
            lastWeather = bean.weather;
        }
        if(weatherMap.isEmpty()){  //若遍历结束了该集合还是空，说明元数据里天气全部一样
            weatherMap.put(data.size(),lastWeather);
        }

        Log.d("ccy","weatherMap.size = " + weatherMap.size());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.EXACTLY){
            viewHeight = Math.max(heightSize,minViewHeight);
        }else {
            viewHeight = minViewHeight;
        }

        int totalWidth = 0;
        if(data.size() > 1){
            totalWidth =  2*defaultPadding + lineInterval*(data.size()-1);
        }
        viewWidth = Math.max(screenWidth,totalWidth);  //默认控件最小宽度为屏幕宽度

        setMeasuredDimension(viewWidth,viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(data.isEmpty()){return;}

        drawTimeLine(canvas);

    }

    /**
     * 画时间轴
     * @param canvas
     */
    private void drawTimeLine(Canvas canvas){
        canvas.save();
        linePaint.setColor(DEFAULT_GRAY);
        canvas.drawLine(defaultPadding,viewHeight-defaultPadding,
                getMeasuredWidth()-defaultPadding,viewHeight-defaultPadding,linePaint);

        for (int i = 0; i < data.size(); i++) {
            SimpleDateFormat f = new SimpleDateFormat("hh:mm");
            String time = f.format(data.get(i).date);
            int centerX = defaultPadding + i*lineInterval;
            int centerY = viewHeight-defaultPadding + dp2px(getContext(),10);
            Paint.FontMetrics m = textPaint.getFontMetrics();
            canvas.drawText(time,0,time.length(),centerX,centerY-(m.ascent+m.descent)/2,textPaint);
        }
        canvas.restore();
    }
















    //工具类
    public static int dp2px(Context c, float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,c.getResources().getDisplayMetrics());
    }
    public static int sp2px(Context c,float sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,c.getResources().getDisplayMetrics());
    }
    public static float dp2pxF(Context c,float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,c.getResources().getDisplayMetrics());
    }
    public static float sp2pxF(Context c,float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,c.getResources().getDisplayMetrics());
    }
}
