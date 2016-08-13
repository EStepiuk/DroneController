package ua.stepiukyevhen.dronecontroller.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import ua.stepiukyevhen.dronecontroller.ControllerActivity;
import ua.stepiukyevhen.dronecontroller.geometry.Circle;
import ua.stepiukyevhen.dronecontroller.geometry.Dot;

/**
 * @author : Yevhen Stepiuk
 */
public class ControllerView extends SurfaceView implements Runnable {

    private int width, height;

    private Thread runningThread = new Thread(this);

    private Paint paint;

    private Circle pitchRollCircle;
    private Circle throttleYawCircle;

    private Dot pitchRollPointer;
    private Dot throttleYawPointer;

    private int throttleYawPointerIndex = -1;
    private int pitchRollPointerIndex = -1;

    private ControllerActivity activity;

    /**
     * Calculates and returns Throttle, Yaw, Pitch, Roll
     * values for ardu-pilot
     * @return int[]
     */
    public int[] getTYPR() {
        return new int[] {
                (int)(1000 + ((float)(throttleYawCircle.getCenterY() + throttleYawCircle.getRadius() - throttleYawPointer.getY()) / (2 * throttleYawCircle.getRadius())) * 1000),
                (int)(1500 + ((float)(throttleYawCircle.getCenterX() - throttleYawPointer.getX()) / throttleYawCircle.getRadius()) * 500),
                (int)(1500 + ((float)(pitchRollCircle.getCenterY() - pitchRollPointer.getY()) / pitchRollCircle.getRadius()) * 500),
                (int)(1500 + ((float)(pitchRollCircle.getCenterX() - pitchRollPointer.getX()) / pitchRollCircle.getRadius()) * 500)
        };
    }

    public ControllerView(Context context) {
        super(context);

        activity = (ControllerActivity) context;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        pitchRollCircle = new Circle(width - width / 4, height / 2, height / 3);
        throttleYawCircle = new Circle(width / 4, height / 2, height / 3);
        throttleYawPointer = new Dot(
                throttleYawCircle.getCenterX(),
                throttleYawCircle.getCenterY() + throttleYawCircle.getRadius()
        );
        pitchRollPointer = new Dot(
                pitchRollCircle.getCenterX(),
                pitchRollCircle.getCenterY()
        );
    }


    /**
     * Main View lifecycle
     */
    @Override
    public void run() {
        while (true) {
            draw();
//            activity.rewriteTYPR(getTYPR());
        }
    }

    /**
     * Drawing controllers, pointers and TYPR values
     */
    private void draw() {
        if (!getHolder().getSurface().isValid()) {
            return;
        }

        Canvas canvas = getHolder().lockCanvas();

        canvas.drawARGB(0xff, 0xff, 0xff, 0xff);
        drawCircles(canvas);
        drawPointers(canvas);
        canvas.drawText("Throttle = " + getTYPR()[0], 10, 20, paint);
        canvas.drawText("Yaw = " + getTYPR()[1], 10, 40, paint);
        canvas.drawText("Pitch = " + getTYPR()[2], 10, 60, paint);
        canvas.drawText("Roll = " + getTYPR()[3], 10, 80, paint);

        getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawPointers(Canvas canvas) {
        Paint pointerPaint = new Paint(paint);
        pointerPaint.setStrokeWidth(4f);
        canvas.drawCircle(
                throttleYawPointer.getX(),
                throttleYawPointer.getY(),
                40f,
                pointerPaint
        );
        canvas.drawCircle(
                pitchRollPointer.getX(),
                pitchRollPointer.getY(),
                40f,
                pointerPaint
        );
    }

    private void drawCircles(Canvas canvas) {
        canvas.drawCircle(
                pitchRollCircle.getCenterX(),
                pitchRollCircle.getCenterY(),
                pitchRollCircle.getRadius(),
                paint
        );
        canvas.drawCircle(
                throttleYawCircle.getCenterX(),
                throttleYawCircle.getCenterY(),
                throttleYawCircle.getRadius(),
                paint
        );
        Paint dotPaint = new Paint(paint);
        dotPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(
                pitchRollCircle.getCenterX(),
                pitchRollCircle.getCenterY(),
                3f,
                dotPaint
        );
        canvas.drawCircle(
                throttleYawCircle.getCenterX(),
                throttleYawCircle.getCenterY(),
                3f,
                dotPaint
        );
    }

    /**
     * Handling touch event
     * @param event - event to handle
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                handleActionPointerDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                handleActionPointerUp(event);
                break;

            case MotionEvent.ACTION_UP:
                handleActionUp(event);

        }

        return true;
    }

    private void handleActionUp(MotionEvent event) {
        if (throttleYawPointerIndex == 0) {
            throttleYawPointerIndex = -1;
            throttleYawPointer.setX(throttleYawCircle.getCenterX());
        } else if (pitchRollPointerIndex == 0) {
            pitchRollPointerIndex = -1;
            pitchRollPointer.setX(pitchRollCircle.getCenterX());
            pitchRollPointer.setY(pitchRollCircle.getCenterY());
        }
    }

    private void handleActionPointerUp(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        if (pointerIndex == throttleYawPointerIndex) {
            throttleYawPointerIndex = -1;
            pitchRollPointerIndex = 0;
            throttleYawPointer.setX(throttleYawCircle.getCenterX());
        } else if (pointerIndex == pitchRollPointerIndex) {
            pitchRollPointerIndex = -1;
            throttleYawPointerIndex = 0;
            pitchRollPointer.setX(pitchRollCircle.getCenterX());
            pitchRollPointer.setY(pitchRollCircle.getCenterY());
        }
    }

    private void handleActionMove(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            Log.d("TAG", "pointerId = " + pointerId);
            Dot dot = new Dot((int) event.getX(pointerId), (int)event.getY(pointerId));
            if (pointerId == throttleYawPointerIndex && throttleYawCircle.isInside(dot)) {
                throttleYawPointer.setX(dot.getX());
                throttleYawPointer.setY(dot.getY());
            } else if (pointerId == pitchRollPointerIndex && pitchRollCircle.isInside(dot)) {
                pitchRollPointer.setX(dot.getX());
                pitchRollPointer.setY(dot.getY());
            }
        }
    }

    private void handleActionPointerDown(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        Dot dot = new Dot((int) event.getX(pointerIndex), (int)event.getY(pointerIndex));

        if (throttleYawPointerIndex == -1 && throttleYawCircle.isInside(dot)) {
            throttleYawPointerIndex = pointerIndex;
            throttleYawPointer.setX(dot.getX());
            throttleYawPointer.setY(dot.getY());
        } else if (pitchRollPointerIndex == -1 && pitchRollCircle.isInside(dot)) {
            pitchRollPointerIndex = pointerIndex;
            pitchRollPointer.setX(dot.getX());
            pitchRollPointer.setY(dot.getY());
        }
    }

    private void handleActionDown(MotionEvent event) {
        Dot dot = new Dot((int) event.getX(), (int)event.getY());

        if (throttleYawCircle.isInside(dot)) {
            throttleYawPointerIndex = 0;
            throttleYawPointer.setX(dot.getX());
            throttleYawPointer.setY(dot.getY());
        } else if (pitchRollCircle.isInside(dot)) {
            pitchRollPointerIndex = 0;
            pitchRollPointer.setX(dot.getX());
            pitchRollPointer.setY(dot.getY());
        }
    }

    public void onResume() {
        runningThread.start();
    }

    public void onPause() {
        try {
            runningThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
