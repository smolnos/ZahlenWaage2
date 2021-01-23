package de.r3chn3n.zahlenwaage2;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCircle {
    public float RADIUS = 20;
    public float RADIUS_BORDER = 40;
    public static final int BLUE = Color.parseColor("#000ffa");
    public static final int BLUE_BORDER = Color.parseColor("#4287f5");

    public int OFFSET ;
    public float STROKE_WIDTH ; //6F ;

    private float x;
    private float y;
    private Paint myPaint;
    private Paint myPaintBorder;
    private Paint myText;
    private String text = "x";
    private Rect mBounds = new Rect();
    private String color;
    private float scale = 0;
    private String myScale;

    float devicePixelsWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    float devicePixelsHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    MyCircle() {
        if (devicePixelsWidth < devicePixelsHeight) {
            scale = devicePixelsWidth / 20;
        } else {
            scale = devicePixelsHeight / 20;
        }
        OFFSET = (int) (scale );
        RADIUS = scale -scale / 5;
        RADIUS_BORDER = scale ;
        STROKE_WIDTH = scale / 5; //6F ;

        myPaint = new Paint();
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        myPaint.setStrokeWidth(STROKE_WIDTH);
        myPaint.setColor(BLUE);
        myPaint.setAlpha(250);
        myPaint.setAntiAlias(true);
        myPaintBorder = new Paint();
        myPaintBorder.setStyle(Paint.Style.FILL);
        myPaintBorder.setColor(BLUE_BORDER);
        myPaintBorder.setAlpha(250);
        myPaintBorder.setAntiAlias(true);
        myText = new Paint();
        myText.setColor(Color.WHITE);
        myText.setTextAlign(Paint.Align.LEFT);
        myText.setTextSize(RADIUS_BORDER * 4/3);
        myText.getTextBounds(text, 0, text.length(), mBounds);

    }

}
