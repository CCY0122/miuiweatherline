package com.example.ccy.miuiweatherline;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

/**
 * Created by ccy on 2017-07-28.
 */

public class ParentMyV extends HorizontalScrollView {

    private MyV v;
    public ParentMyV(Context context) {
        this(context, null);
    }

    public ParentMyV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParentMyV(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        v = new MyV(context);
        addView(v);

    }

    @Override
    public void fling(int velocityX) {
        super.fling(velocityX);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        Log.d("ccy","onScrollChanged");
        Log.d("ccy","parent scrollx = " + getScrollX());
        Log.d("ccy","parent left = " + getLeft());
        v.invalidate();
        setHorizontalScrollBarEnabled(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("ccy","parent ondraw");
    }

    @Override
    public void invalidate() {
        super.invalidate();
//        Log.d("ccy","parent invalidate");
    }
}
