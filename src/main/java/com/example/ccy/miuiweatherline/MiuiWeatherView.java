package com.example.ccy.miuiweatherline;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

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

    private static int DEFAULT_BULE = 0XFF00BFFF;
    private static int DEFAULT_GRAY = Color.GRAY;

    private int backgroundColor;
    private int minViewHeight; //控件的最低高度
    private int minPointHeight;//折线最低点的高度
    private int lineInterval; //折线线段长度
    private float pointRadius; //折线点的半径
    private float textSize; //字体大小
    private float pointGap; //折线单位高度差
    private int defaultPadding; //折线坐标图四周留出来的偏移量

    private int viewHeight;
    private int viewWidth;
    private int screenWidth;
    private int screenHeight;

    private Paint linePaint; //线画笔
    private Paint textPaint; //文字画笔
    private Paint circlePaint; //圆点画笔

    private List<WeatherBean> data = new ArrayList<>(); //元数据
    private List<Pair<Integer, String>> weatherDatas = new ArrayList<>();  //对元数据中天气分组后的集合
    private List<Float> dashDatas = new ArrayList<>(); //不同天气之间虚线的x坐标集合
    private List<PointF> points = new ArrayList<>(); //折线拐点的集合
    private int maxTemperature;//元数据中的最高和最低温度
    private int minTemperature;

    private VelocityTracker velocityTracker;
    private Scroller scroller;


    public MiuiWeatherView(Context context) {
        this(context, null);
    }

    public MiuiWeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiuiWeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关硬件加速
        scroller = new Scroller(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MiuiWeatherView);
        minPointHeight = (int) ta.getDimension(R.styleable.MiuiWeatherView_min_point_height, dp2pxF(context, 60));
        lineInterval = (int) ta.getDimension(R.styleable.MiuiWeatherView_line_interval, dp2pxF(context, 60));
        backgroundColor = ta.getColor(R.styleable.MiuiWeatherView_background_color,Color.WHITE);
        ta.recycle();

        setBackgroundColor(backgroundColor);

        initSize(context);

        initPaint(context);

    }

    /**
     * 初始化默认数据
     */
    private void initSize(Context c) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        minViewHeight = 3 * minPointHeight;
        pointRadius = dp2pxF(c, 2.5f);
        textSize = sp2pxF(c, 10);
        defaultPadding = (int) (0.5 * minPointHeight);

        Log.d("ccy", "minPointHeight = " + minPointHeight);
        Log.d("ccy", "minViewHeight = " + minViewHeight);
        calculatePontGap();
    }

    /**
     * 计算折线单位高度差
     */
    private void calculatePontGap() {
        int lastMaxTem = -100000;
        int lastMinTem = 100000;
        for (WeatherBean bean : data) {
            if (bean.temperature > lastMaxTem) {
                maxTemperature = bean.temperature;
                lastMaxTem = bean.temperature;
            }
            if (bean.temperature < lastMinTem) {
                minTemperature = bean.temperature;
                lastMinTem = bean.temperature;
            }
        }
        Log.d("ccy", "maxTem = " + maxTemperature + ";minTem = " + minTemperature);
        float gap = (maxTemperature - minTemperature) * 1.0f;
        gap = (gap == 0.0f ? 1.0f : gap);  //保证分母不为0
        pointGap = (viewHeight - minPointHeight - 2 * defaultPadding) / gap;
        Log.d("ccy", "pointGap = " + pointGap);
    }

    private void initPaint(Context c) {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(dp2px(c, 1));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStrokeWidth(dp2pxF(c, 1));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize(getContext());
    }

    /**
     * 唯一公开方法，用于设置元数据
     *
     * @param data
     */
    public void setData(List<WeatherBean> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        this.data = data;
        weatherDatas.clear();
        points.clear();
        dashDatas.clear();

        calculatePontGap();
        initWeatherMap();

        requestLayout();
        invalidate();
    }

    /**
     * 根据元数据中连续相同的天气数做分组,
     * pair中的first值为连续相同天气的天数，second值为对应天气
     */
    private void initWeatherMap() {
        weatherDatas.clear();
        String lastWeather = "";
        int count = 0;
        for (int i = 0; i < data.size(); i++){
            WeatherBean bean = data.get(i);
            if (i == 0) {
                lastWeather = bean.weather;
            }
            if (bean.weather != lastWeather) {
                Pair<Integer, String> pair = new Pair<>(count, lastWeather);
                weatherDatas.add(pair);
                count = 1;
            } else {
                count++;
            }
            lastWeather = bean.weather;

            if(i == data.size()-1){
                Pair<Integer,String> pair = new Pair<>(count,lastWeather);
                weatherDatas.add(pair);
            }
        }

        Log.d("ccy", "weatherMap.size = " + weatherDatas.size());
        for (int i = 0; i < weatherDatas.size(); i++) {
            int c = weatherDatas.get(i).first;
            String w = weatherDatas.get(i).second;
            Log.d("ccy","weatherMap i =" + i + ";count = " + c+";weather = " + w);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            viewHeight = Math.max(heightSize, minViewHeight);
        } else {
            viewHeight = minViewHeight;
        }

        int totalWidth = 0;
        if (data.size() > 1) {
            totalWidth = 2 * defaultPadding + lineInterval * (data.size() - 1);
        }
        viewWidth = Math.max(screenWidth, totalWidth);  //默认控件最小宽度为屏幕宽度

        setMeasuredDimension(viewWidth, viewHeight);
        Log.d("ccy", "viewHeight = " + viewHeight + ";viewWidth = " + viewWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data.isEmpty()) {
            return;
        }

        drawAxis(canvas);

        drawLinesAndPoints(canvas);

        drawTemperature(canvas);

        drawWeatherDash(canvas);

        drawWeatherIcon(canvas);

    }

    /**
     * 画时间轴
     *
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {
        canvas.save();
        linePaint.setColor(DEFAULT_GRAY);
        linePaint.setStrokeWidth(dp2px(getContext(), 1));

        canvas.drawLine(defaultPadding,
                viewHeight - defaultPadding,
                getMeasuredWidth() - defaultPadding,
                viewHeight - defaultPadding,
                linePaint);

        float centerY = viewHeight - defaultPadding + dp2pxF(getContext(), 15);
        float centerX;
        for (int i = 0; i < data.size(); i++) {
            String text = data.get(i).time;
            centerX = defaultPadding + i * lineInterval;
            Paint.FontMetrics m = textPaint.getFontMetrics();
            canvas.drawText(text, 0, text.length(), centerX, centerY - (m.ascent + m.descent) / 2, textPaint);
        }
        canvas.restore();
    }

    /**
     * 画折线和它拐点的园
     *
     * @param canvas
     */
    private void drawLinesAndPoints(Canvas canvas) {
        canvas.save();
        linePaint.setColor(DEFAULT_BULE);
        linePaint.setStrokeWidth(dp2pxF(getContext(), 1));
        linePaint.setStyle(Paint.Style.STROKE);

        Path linePath = new Path(); //用于绘制折线
        points.clear();
        int baseHeight = defaultPadding + minPointHeight;
        float centerX;
        float centerY;
        for (int i = 0; i < data.size(); i++) {
            int tem = data.get(i).temperature;
            tem = tem - minTemperature;
            centerY = (int) (viewHeight - (baseHeight + tem * pointGap));
            centerX = defaultPadding + i * lineInterval;
            points.add(new PointF(centerX, centerY));
            if (i == 0) {
                linePath.moveTo(centerX, centerY);
            } else {
                linePath.lineTo(centerX, centerY);
            }
        }
        canvas.drawPath(linePath, linePaint); //画出折线

        //接下来画折线拐点的园
        float x, y;
        for (int i = 0; i < points.size(); i++) {
            x = points.get(i).x;
            y = points.get(i).y;

            //先画一个颜色为背景颜色的实心园覆盖掉折线拐角
            circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            circlePaint.setColor(backgroundColor);
            canvas.drawCircle(x, y,
                    pointRadius + dp2pxF(getContext(), 1),
                    circlePaint);
            //再画出正常的空心园
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(DEFAULT_BULE);
            canvas.drawCircle(x, y,
                    pointRadius,
                    circlePaint);
        }
        canvas.restore();
    }

    /**
     * 画温度描述值
     * @param canvas
     */
    private void drawTemperature(Canvas canvas){
        canvas.save();

        textPaint.setTextSize(1.2f * textSize); //字体放大一丢丢
        float centerX;
        float centerY;
        String text;
        for (int i = 0; i < points.size(); i++) {
            text = data.get(i).temperatureStr;
            centerX = points.get(i).x;
            centerY = points.get(i).y - dp2pxF(getContext(),15);
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            canvas.drawText(text,
                    centerX,
                    centerY - (metrics.ascent + metrics.descent),
                    textPaint);
        }
        textPaint.setTextSize(textSize);
        canvas.restore();
    }

    /**
     * 画不同天气之间的虚线
     * @param canvas
     */
    private void drawWeatherDash(Canvas canvas){
        canvas.save();
        linePaint.setColor(DEFAULT_GRAY);
        linePaint.setStrokeWidth(dp2pxF(getContext(),0.5f));
        linePaint.setAlpha(0xcc);

        //设置画笔画出虚线
        float[] f ={dp2pxF(getContext(),5),dp2pxF(getContext(),1)};  //两个值分别为循环的实线长度、空白长度
        PathEffect pathEffect = new DashPathEffect(f,0);
        linePaint.setPathEffect(pathEffect);

        dashDatas.clear();
        int interval = -1 ;
        float startX,startY,endX,endY;
        endY = viewHeight - defaultPadding;

        //0坐标点有可能是没有虚线的，我们要手动画上
        if(weatherDatas.get(0).first > 1){   //分组中第一组的相同天数就大于1天时
            canvas.drawLine(defaultPadding,
                    points.get(0).y + pointRadius + dp2pxF(getContext(),2),
                    defaultPadding,
                    endY,
                    linePaint);
            dashDatas.add((float) defaultPadding);
        }

        for (int i = 0; i < weatherDatas.size(); i++) {
            interval += weatherDatas.get(i).first;
            startX =endX = defaultPadding +  interval*lineInterval;
            startY = points.get(interval).y + pointRadius + dp2pxF(getContext(),2);
            dashDatas.add(startX);
            canvas.drawLine(startX,startY,endX,endY,linePaint);
        }

        linePaint.setPathEffect(null);
        linePaint.setAlpha(0xff);
        canvas.restore();
    }

    /**
     * 画天气图标和它下方文字
     * 若相邻虚线都在屏幕内，图标的x位置即在两虚线的中间
     * 若有一条虚线在屏幕外，图标的x位置即在屏幕边沿到另一条虚线的中间
     * 若两条都在屏幕外，图标x位置即屏幕中间
     * @param canvas
     */
    private void drawWeatherIcon(Canvas canvas){
        canvas.save();
        textPaint.setTextSize(0.8f * textSize); //字体缩小一丢丢

        boolean leftUsedScreenLeft = false;
        boolean rightUsedScreenRight = false;

        float iconWidth = lineInterval / 3.0f;  //默认天气图标边长为折线间距的1/3
        int scrollX = getScrollX();  //范围控制在0 ~ viewWidth-screenWidth
        float left,right;
        float iconX,iconY;
        float textY;     //文字的x坐标跟图标是一样的，无需额外声明
        iconY = viewHeight - (defaultPadding + minPointHeight / 2.0f);
        textY = iconY + iconWidth/2.0f + dp2pxF(getContext(),10);
        for (int i = 0; i < dashDatas.size()-1; i++) {
            left = dashDatas.get(i);
            right = dashDatas.get(i+1);

            //以下校正的情况为：两条虚线都在屏幕内或只有一条在屏幕内

            if(left < scrollX &&    //仅左虚线在屏幕外
                    right < scrollX+screenWidth){
                left = scrollX;
                leftUsedScreenLeft = true;
            }
            if(right > scrollX+screenWidth &&  //仅右虚线在屏幕外
                    left > scrollX){
                right = scrollX+screenWidth;
                rightUsedScreenRight = true;
            }

            if(right - left > iconWidth){    //经过上述校正之后左右距离还大于图标宽度
                iconX = (right - left) / 2.0f;
            }else{                          //经过上述校正之后左右距离小于图标宽度，则贴着在屏幕内的虚线
                if (leftUsedScreenLeft){
                    iconX = right- iconWidth / 2.0f;
                }else{
                    iconX = left + iconWidth / 2.0f;
                }
            }

            //以下校正的情况为：两条虚线都在屏幕之外

            if(right < scrollX ){  //两条都在屏幕左侧
                iconX = right - iconWidth / 2.0f;
            }
            if(left > scrollX + screenWidth ){   //两条都在屏幕右侧
                iconX = left + iconWidth / 2.0f;
            }

            //经过上述校正之后可以得到图标和文字的绘制区域
            RectF iconRect = new RectF(iconX - iconWidth/2.0f,
                    iconY - iconWidth/2.0f,
                    iconX + iconWidth/2.0f,
                    iconY + iconWidth/2.0f);

            Bitmap icon = getWeatherIcon(weatherDatas.get(i).second);

            canvas.drawBitmap(icon,null,iconRect,null);  //画图标
            canvas.drawText(weatherDatas.get(i).second, //画图标下方文字
                    iconX,
                    textY,
                    textPaint);

            leftUsedScreenLeft = rightUsedScreenRight = false; //重置标志位
        }

        textPaint.setTextSize(textSize);
        canvas.restore();
    }

    /**
     * 根据天气类型获取天气图标
     * @return
     */
    private Bitmap getWeatherIcon(String weather) {
        Bitmap bmp;
        switch (weather){
            case WeatherBean.SUN:
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.sun);
                break;
            case WeatherBean.CLOUDY:
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.cloudy);
                break;
            case WeatherBean.RAIN:
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.rain);
                break;
            case WeatherBean.SNOW:
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.snow);
                break;
            case WeatherBean.SUN_CLOUD:
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.sun_cloud);
                break;
            case WeatherBean.THUNDER:
                default:
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.thunder);
                break;
        }
        return bmp;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            postInvalidate();
        }
    }

    //工具类
    public static int dp2px(Context c, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context c, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

    public static float dp2pxF(Context c, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static float sp2pxF(Context c, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }
}
