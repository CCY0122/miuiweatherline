package com.example.ccy.miuiweatherline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ccy on 2017-07-28.
 */

public class MyV extends View {

    private int screenWidth;
    private Paint paint;

    public MyV(Context context) {
        this(context,null);
    }

    public MyV(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MyV(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(dp2px(8));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(2*screenWidth,dp2px(100));
    }
    public int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }

    @Override
    public void invalidate() {
        super.invalidate();
//        Log.d("ccy", "invalidate");
        Log.d("ccy","scrollx = " + getScrollX());
        Log.d("ccy","left = " + getLeft());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("ccy","ondraw");
        paint.setColor(0xffff0000);
        paint.setStrokeWidth(dp2px(1));
        Rect r = new Rect(dp2px(10),dp2px(10),dp2px(60),dp2px(60));
        canvas.drawRect(r,paint);
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(),R.drawable.cloudy,o);
//        float widthRatio = (float)o.outWidth / dp2px(50);
//        float heightRatio = (float)o.outHeight / dp2px(50);
//        Log.d("ccy","px = " + dp2px(50));
//        Log.d("ccy", " w = "+o.outWidth+";h = " + o.outHeight);
//        Log.d("ccy","wr = " + widthRatio + ";hr = " + heightRatio);
//        if(widthRatio > 1 ||heightRatio > 1){
//            o.inSampleSize = (int) Math.max(widthRatio,heightRatio);
//        }else{
//            o.inSampleSize = 1;
//        }
//        o.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.sun);
        Log.d("ccy","final w = " + b.getWidth() + ";h = " + b.getHeight());
//        Log.d("ccy","final w1 = " + o.outWidth + ";h1 = " + o.outHeight);
        Matrix matrix = new Matrix();
        float wr = (float)dp2px(50) / b.getWidth();
        float hr = (float)dp2px(50) / b.getHeight();
        matrix.postScale(wr,hr);
        canvas.drawBitmap(b,null,r,null);
    }
}
