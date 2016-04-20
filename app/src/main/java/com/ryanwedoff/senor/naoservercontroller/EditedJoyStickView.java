package com.ryanwedoff.senor.naoservercontroller;

/**
 * This code is edit from:
 * JoystickView by AJ Alves is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 *
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class EditedJoyStickView extends View implements Runnable {
    public final static long DEFAULT_LOOP_INTERVAL = 100; // 100 ms

    // Variables
    private OnJoystickMoveListener onJoystickMoveListener; // Listener
    private Thread thread = new Thread(this);
    private long loopInterval = DEFAULT_LOOP_INTERVAL;
    private int xPosition = 0; // Touch x position
    private int yPosition = 0; // Touch y position
    private double centerX = 0; // Center view x position
    private double centerY = 0; // Center view y position
    private Paint mainCircle;
    private Paint secondaryCircle;
    private Paint button;
    private Paint horizontalLine;
    private Paint verticalLine;
    private int joystickRadius;
    private int buttonRadius;
    private int lastAngle = 0;

    public EditedJoyStickView(Context context) {
        super(context);
    }

    public EditedJoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public EditedJoyStickView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initJoystickView();
    }

    private void initJoystickView() {

        mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainCircle.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        secondaryCircle = new Paint();
        secondaryCircle.setColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
        secondaryCircle.setStyle(Paint.Style.STROKE);

        verticalLine = new Paint();
        verticalLine.setStrokeWidth(5);
        verticalLine.setColor(Color.WHITE);

        horizontalLine = new Paint();
        horizontalLine.setStrokeWidth(2);
        horizontalLine.setColor(ContextCompat.getColor(getContext(), R.color.primary_text));

        button = new Paint(Paint.ANTI_ALIAS_FLAG);
        button.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        button.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        xPosition = getWidth() / 2;
        yPosition = getWidth() / 2;
        int d = Math.min(xNew, yNew);
        buttonRadius = (int) (d / 2 * 0.25);
        joystickRadius = (int) (d / 2 * 0.75);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setting the measured values to resize the view to a certain width and
        // height
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

        setMeasuredDimension(d, d);

    }

    private int measure(int measureSpec) {
        int result;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        centerX = (getWidth()) / 2;
        centerY = (getHeight()) / 2;

        // painting the main circle
        canvas.drawCircle((int) centerX, (int) centerY, joystickRadius,
                mainCircle);
        // painting the secondary circle
        canvas.drawCircle((int) centerX, (int) centerY, joystickRadius / 2,
                secondaryCircle);
        // paint lines
        canvas.drawLine((float) centerX, (float) centerY, (float) centerX,
                (float) (centerY - joystickRadius), verticalLine);
        canvas.drawLine((float) (centerX - joystickRadius), (float) centerY,
                (float) (centerX + joystickRadius), (float) centerY,
                horizontalLine);
        canvas.drawLine((float) centerX, (float) (centerY + joystickRadius),
                (float) centerX, (float) centerY, horizontalLine);

        // painting the move button
        canvas.drawCircle(xPosition, yPosition, buttonRadius, button);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPosition = (int) event.getX();
        yPosition = (int) event.getY();
        double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
                + (yPosition - centerY) * (yPosition - centerY));
        if (abs > joystickRadius) {
            xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
            yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
        }
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            xPosition = (int) centerX;
            yPosition = (int) centerY;
            thread.interrupt();
            if (onJoystickMoveListener != null)
                onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());
        }
        if (onJoystickMoveListener != null
                && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
            thread = new Thread(this);
            thread.start();
            if (onJoystickMoveListener != null)
                onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());
        }
        return true;
    }

    private int getAngle() {
        double RAD = 57.2957795;
        if (xPosition > centerX) {
            if (yPosition < centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX))
                        * RAD + 90);
            } else if (yPosition > centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX)) * RAD) + 90;
            } else {
                return lastAngle = 90;
            }
        } else if (xPosition < centerX) {
            if (yPosition < centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX))
                        * RAD - 90);
            } else if (yPosition > centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX)) * RAD) - 90;
            } else {
                return lastAngle = -90;
            }
        } else {
            if (yPosition <= centerY) {
                return lastAngle = 0;
            } else {
                if (lastAngle < 0) {
                    return lastAngle = -180;
                } else {
                    return lastAngle = 180;
                }
            }
        }
    }

    private int getPower() {
        return (int) (100 * Math.sqrt((xPosition - centerX)
                * (xPosition - centerX) + (yPosition - centerY)
                * (yPosition - centerY)) / joystickRadius);
    }




    private int getDirection() {
        //int lastPower = 0;
        if (lastAngle == 0) {
            return 0;
        }
        int a = 0;
        if (lastAngle <= 0) {
            a = (lastAngle * -1) + 90;
        } else if (lastAngle > 0) {
            if (lastAngle <= 90) {
                a = 90 - lastAngle;
            } else {
                a = 360 - (lastAngle - 90);
            }
        }

        int direction = ((a + 22) / 45) + 1;

        if (direction > 8) {
            direction = 1;
        }
        return direction;
    }

    public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
                                          long repeatInterval) {
        this.onJoystickMoveListener = listener;
        this.loopInterval = repeatInterval;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    if (onJoystickMoveListener != null)
                        onJoystickMoveListener.onValueChanged(getAngle(),
                                getPower(), getDirection());
                }
            });
            try {
                Thread.sleep(loopInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public interface OnJoystickMoveListener {
        void onValueChanged(int angle, int power, int direction);
    }
}