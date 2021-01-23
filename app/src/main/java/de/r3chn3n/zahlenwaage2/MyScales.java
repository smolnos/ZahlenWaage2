package de.r3chn3n.zahlenwaage2;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;

import lombok.Data;

@Data
public class MyScales {

    public float RADIUS = 20;
    public float RADIUS_BORDER = 40;
    public static final int BLUE = Color.parseColor("#000ffa");
    public static final int BLUE_BORDER = Color.parseColor("#4287f5");

    private float scale;
    MyScale myScale1;
    MyScale myScale2;
    Paint myPaint;
    Paint myPaintBorder;
    float devicePixelsWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    float devicePixelsHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    private float startRectX;
    private float endRectX;
    private float endRectY;
    private float startRectY;

    public MyScales() {
        scale = devicePixelsHeight / 100;
        myPaint = new Paint();
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setColor(BLUE);
        myPaint.setAlpha(225);
        myPaintBorder = new Paint();
        myPaintBorder.setStyle(Paint.Style.STROKE);
        myPaintBorder.setStrokeWidth(scale);
        myPaintBorder.setColor(BLUE_BORDER);
        myPaintBorder.setAlpha(250);
    }
}
