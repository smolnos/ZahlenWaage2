package de.r3chn3n.zahlenwaage2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaintView extends View {

    private final int OFFSET = 30;
    private float mcNewPositionX = 30;
    private float mcNewPositionY = 30;
    private float mcOnTouchX;
    private float mcOnTouchY;
    private float moveX;
    private float moveY;
    int indexMyCircles = 0;
    int indexOptionalCircles = 0;
    boolean changed = false;
    boolean touchedEvent = false;
    private List<MyCircle> myCircles = new ArrayList<>();
    private List<MyCircle> optionalCircles = new ArrayList<>();
    float pxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    float pxHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private  final int DIFFX = (int) (pxWidth * 1 / 10);
    private  final int DIFFY = (int) (pxHeight * 1 / 10);

    private final int optionalY = DIFFY;
    private final int optionalX = DIFFX;

    private final float startRectX = DIFFX;
    private final float endRectX = pxWidth - DIFFX;

    private final float startRectY = pxHeight - DIFFY * 2;
    private final float endRectY = pxHeight -  DIFFY;

    private MyScale myScale1 = new MyScale();
    private MyScale myScale2 = new MyScale();
    private MyScales myScales = new MyScales();


    private float startY = pxHeight - DIFFY * 2;
    private float endYMin = DIFFY * 3;
    private float endYNormal = pxHeight  / 2;
    private float endYMax = pxHeight - DIFFY * 3;
    String scale1 = "scale1";
    String scale2 = "scale2";

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        myScale1.setStartY(startY);
        myScale1.setEndY(endYNormal);
        myScale1.setStartX(DIFFX * 3);
        myScale1.setEndX(DIFFX * 3);

        myScale2.setStartX(pxWidth - DIFFX * 3);
        myScale2.setEndX(pxWidth - DIFFX * 3);
        myScale2.setStartY(startY);
        myScale2.setEndY(endYNormal);

        myScales.setMyScale1(myScale1);
        myScales.setMyScale2(myScale2);

        myScales.setStartRectX(startRectX);
        myScales.setEndRectX(endRectX);

        myScales.setStartRectY(startRectY);
        myScales.setEndRectY(endRectY);


        MyCircle myCircle = new MyCircle();
        myCircle.setText("X");
        myCircle.setX(optionalX);
        myCircle.setY(optionalY );
        optionalCircles.add(myCircle);
        for (int i = 1; i <= 10; i++) {
            myCircle = new MyCircle();
            myCircle.setText(String.valueOf(i));
            myCircle.setX((float) (optionalX + DIFFX / 4. * 3. * i));
            myCircle.setY(optionalY );
            optionalCircles.add(myCircle);

        }
    }

    public PaintView(Context context) {
        super(context);
    }

    /**
     * Depending on the event, a new circle is added (if there was no circle before), the color is
     * changed (if there was a circle before) or the shape is changed (if the duration of the touch
     * is longer than 2 seconds), the circle is deleted (if this circle is no longer on the screen),
     * or the circle is moved
     * @param event touch event
     * @return boolean always return true
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isCircleNotPresent(event) ) {
                        if (isCircleInOptionalCircles(event)) {
                            addNewCircle(event);
                            touchedEvent = true;
                        }
                } else {
                    touchedEvent = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (circleOutOfScreen()) {
                    deleteCircle();
                }
                touchedEvent = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchedEvent) {
                    mcNewPositionX = mcOnTouchX + event.getX() - moveX;
                    mcNewPositionY = mcOnTouchY + event.getY() - moveY;
                    moveCircle(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * check if new touch is whithin a optional circle. If so, set myIdCircle to this circle
     * and return that circle is already present, i.e. true. If no, just return false that circle is
     * not in optional circles
     * @return boolean true if circle is not in list optionalCircles
     */
    private boolean isCircleInOptionalCircles(MotionEvent event) {
        moveX = event.getX();
        moveY = event.getY();

        boolean circleInOptionalCircle = false;
        int i = 0;
        for (MyCircle mc : optionalCircles) {
            if (isEventInCircle(mc, event)) {
                circleInOptionalCircle = true;
                mcOnTouchX = mc.getX();
                mcOnTouchY =  mc.getY();
                indexOptionalCircles = i; //index of myCircles of that instance myCircle which should be
                // changed later (position, color, deletion)
                break;
            }
            i++;
        }
        return circleInOptionalCircle;
    }

    private boolean isTouchStillInScreen() {
        return mcNewPositionX <= mcOnTouchX + myCircles.get(indexMyCircles).RADIUS * 2 / 3 && mcNewPositionX >= mcOnTouchX - myCircles.get(indexMyCircles).RADIUS * 2 / 3
                && mcNewPositionY <= mcOnTouchY + myCircles.get(indexMyCircles).RADIUS * 2 / 3 && mcNewPositionY >= mcOnTouchY - myCircles.get(indexMyCircles).RADIUS * 2 / 3;
    }

    /**
     * check if current event touched a circle
     * @param myCircle instance of class Mycircle taken from list myCircles
     * @param event instance of class Motionevent
     * @return true if event with point (eventX, eventY) is within circle having center (myCircleX,
     * myCircleY) and radius RADIUS
     */
    private boolean isEventInCircle(MyCircle myCircle, MotionEvent event) {
        return event.getX() <= myCircle.getX() + myCircle.RADIUS + OFFSET / 2. && event.getX() >= myCircle.getX()
                - myCircle.RADIUS -  OFFSET / 2. && event.getY() <= myCircle.getY() + myCircle.RADIUS  + OFFSET / 2. &&
                event.getY() >= myCircle.getY() - myCircle.RADIUS -  OFFSET / 2.;
    }

    /**
     * check if new touch is whithin a circle. If so, set myIdCircle to this circle
     * and return that circle is already present, i.e. false. If no, just return true that circle is
     * not present
     * @return boolean true if circle is not in list myCircles
     */
    private boolean isCircleNotPresent(MotionEvent event) {
        moveX = event.getX();
        moveY = event.getY();

        boolean circleNotPresent = true;
        int i = 0;
        for (MyCircle mc : myCircles) {
            if (isEventInCircle(mc, event)) {
                circleNotPresent = false;
                mcOnTouchX = mc.getX();
                mcOnTouchY =  mc.getY();
                indexMyCircles = i; //index of myCircles of that instance myCircle which should be
                // changed later (position, color, deletion)
                break;
            }
            i++;
        }
        return circleNotPresent;
    }

    /**
     * create a new instance of the class MyCircle and add this with new paramter of the event to
     * the list myCircles. Set myIdCircle on the position of this new instantiated object
     */
    private void addNewCircle(MotionEvent event) {
        MyCircle newCircle = new MyCircle();
        newCircle.setX(moveX);
        newCircle.setY(moveY);
        newCircle.setText(optionalCircles.get(indexOptionalCircles).getText());
        myCircles.add(newCircle);
        mcOnTouchX = moveX;
        mcOnTouchY = moveY;
        indexMyCircles = myCircles.size() - 1;
        mcNewPositionX = mcOnTouchX + event.getX() - moveX;
        mcNewPositionY = mcOnTouchY + event.getY() - moveY;
    }

    /**
     * set new position due to the new event to this circle
     * @param event paramer of the new event needed to compute new position of this circle
     */
    private void moveCircle(MotionEvent event) {
        mcNewPositionX = mcOnTouchX + event.getX() - moveX;
        mcNewPositionY = mcOnTouchY + event.getY() - moveY;
        myCircles.get(indexMyCircles).setX(mcNewPositionX);
        myCircles.get(indexMyCircles).setY(mcNewPositionY);
    }

    /**
     * check if the given point is out of the canvas
     * @return true if x value or y value are below offset or greater than width/ height
     */
    private boolean circleOutOfScreen() {
        return mcNewPositionX <= OFFSET || mcNewPositionX >= getWidth() - OFFSET
                || mcNewPositionY <= OFFSET || mcNewPositionY >= getHeight() - OFFSET;
    }

    /**
     * delete circles if center is outside the screen
     */
    private void deleteCircle() {
        if (myCircles.isEmpty()) return;
        myCircles.remove(indexMyCircles);
        if (myCircles.isEmpty()) return;
        mcNewPositionX = myCircles.get(myCircles.size() - 1).getX();
        mcNewPositionY = myCircles.get(myCircles.size() - 1).getY();
    }

    /**
     * second draw all points twice with different radius to simulate border of point
     * if myVariable in myCircle has the value X, a rectangle is drawn instead of a circle
     * @param canvas canvas on which the rectangle and points are drawn
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (MyCircle optionalCircle : optionalCircles) {
            drawText(canvas, optionalCircle);
        }

        myScales.getMyScale1().setSum(0);
        myScales.getMyScale1().setContainsX(false);
        myScales.getMyScale2().setSum(0);
        myScales.getMyScale2().setContainsX(false);
        for (MyCircle myCircle : myCircles) {
            if (isInScale(myScales.getMyScale1(), myCircle)) {
                computeSum(myCircle, myScales.getMyScale1());
                myCircle.setMyScale(scale1);
                myCircle.getPlaySound().stopSound();
            } else if (isInScale(myScales.getMyScale2(), myCircle)){
                computeSum(myCircle, myScales.getMyScale2());
                myCircle.setMyScale(scale2);
                myCircle.getPlaySound().stopSound();
            } else {
                myCircle.setY(myCircle.getY() + 1);
                myCircle.setMyScale("");
                if (myCircle.getPlaySound().isRunning()) {
                    if (myCircle.getX() <= 0 || myCircle.getX()  >= getWidth()
                            || myCircle.getY()  >= getHeight() || touchedEvent) {
                        myCircle.getPlaySound().stopSound();
                    }
                } else {
                    if (!myCircle.getPlaySound().isRunning() && !(myCircle.getX() <= 0 || myCircle.getX()  >= getWidth()
                            || myCircle.getY()  >= getHeight() || touchedEvent)) {
                        myCircle.getPlaySound().playSound();
                    } else {
                        myCircle.getPlaySound().stopSound();
                    }
                }
            }
        }

        drawRectangle(canvas);
        rescaleMyScales();
        drawScale(canvas, myScales.getMyScale1());
        drawScale(canvas, myScales.getMyScale2());
        myScales.getMyScale2().setContainsX(false);
        for (int i = 0; i < myCircles.size(); i++) {
            drawText(canvas, myCircles.get(i));
        }
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void rescaleMyScales() {
        int diff = (myScales.getMyScale1().getSum() - myScales.getMyScale2().getSum()) * (DIFFY / 5);
        float newYmax = (float) Math.min(endYMax, endYNormal + Math.abs(diff) / 2.);
        float newYmin = (float) Math.max(endYMin, endYNormal - Math.abs(diff) / 2.);
        if (diff == 0 ||
                myScales.getMyScale1().isContainsX() ||
                myScales.getMyScale2().isContainsX() ) {
            setNewYValuesForMyCircles(endYNormal, endYNormal);
        } else if (diff > 0) {
            setNewYValuesForMyCircles(newYmax, newYmin);
        } else {
            setNewYValuesForMyCircles(newYmin, newYmax);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNewYValuesForMyCircles(float newYScale1, float newYScale2) {
        myScales.getMyScale1().setEndY(newYScale1);
        myScales.getMyScale2().setEndY(newYScale2);
        for (int i = 0; i < myCircles.size(); i++) {
            if (scale1.equals(myCircles.get(i).getMyScale())) {
                myCircles.get(i).setY((float) (newYScale1 - DIFFY / 2.));
            } else if (scale2.equals(myCircles.get(i).getMyScale())) {
                myCircles.get(i).setY((float) (newYScale2 - DIFFY / 2.));
            }
        }
    }

    private void computeSum(MyCircle myCircle, MyScale myScale) {
        if ("X".equals(myCircle.getText())) {
            myScale.setContainsX(true);
        } else {
            int val = Integer.parseInt(myCircle.getText());
            myScale.setSum(myScale.getSum() + val);
        }
    }


    private boolean isInScale(MyScale myScale, MyCircle myCircle) {
        float dotX = myCircle.getX();
        float dotY = myCircle.getY();
        return dotX >= myScale.getStartX() - DIFFX * 1.5 &&
                dotX <= myScale.getStartX() + DIFFX * 1.5 &&
                dotY <= myScale.getEndY() &&
                dotY >= myScale.getEndY() - DIFFY / 2.;

    }

    private void drawScale(Canvas canvas, MyScale myScale) {
        //center vertical line
        canvas.drawLine(myScale.getStartX(),
                myScale.getStartY(),
                myScale.getEndX(),
                myScale.getEndY(),
                myScales.getMyPaintBorder());

        //horizontal line
        canvas.drawLine((float) (myScale.getStartX() - DIFFX * 1.5),
                myScale.getEndY(),
                (float) (myScale.getEndX() + DIFFX * 1.5),
                myScale.getEndY(),
                myScales.getMyPaintBorder());

        //left vertical line
        canvas.drawLine((float) (myScale.getStartX() - DIFFX * 1.5),
                myScale.getEndY(),
                (float) (myScale.getEndX() - DIFFX * 1.5),
                myScale.getEndY() - DIFFY,
                myScales.getMyPaintBorder());

        //right vertical line
        canvas.drawLine((float) (myScale.getStartX() + DIFFX * 1.5),
                myScale.getEndY(),
                (float) (myScale.getEndX() + DIFFX * 1.5),
                myScale.getEndY() - DIFFY,
                myScales.getMyPaintBorder());
    }

    private void drawRectangle(Canvas canvas) {
        RectF r = new RectF(myScales.getStartRectX(),
                myScales.getStartRectY(),
                myScales.getEndRectX(),
                myScales.getEndRectY());
        int cornerRadius = 35;
        canvas.drawRoundRect(r, cornerRadius, cornerRadius, myScales.getMyPaint());    // fill
        canvas.drawRoundRect(r, cornerRadius, cornerRadius, myScales.getMyPaintBorder());
    }


    private void drawText(Canvas canvas, MyCircle myCircle) {

        // Calculate x and y for text so it's centered.
        float x = myCircle.getX() - myCircle.getMBounds().centerX();
        float y =  myCircle.getY() - myCircle.getMBounds().centerY();
        canvas.drawCircle(myCircle.getX(), myCircle.getY(), myCircle.RADIUS_BORDER, myCircle.getMyPaintBorder());
        canvas.drawCircle(myCircle.getX(), myCircle.getY(), myCircle.RADIUS, myCircle.getMyPaint());
        if ("10".equals(myCircle.getText())) {
            x -= 1.5*myCircle.getMBounds().centerX();
        } else {
            x -= 0.3*myCircle.getMBounds().centerX();
        }
        canvas.drawText(myCircle.getText(), x, y, myCircle.getMyText());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeIndices(List<MyCircle> other, List<Integer> indices)
    {
        indices.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(i->other.remove(i.intValue()));
    }

    public void deleteCircles() {
        indexMyCircles = 0;
        myCircles.clear();
    }
}