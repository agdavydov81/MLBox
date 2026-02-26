package io.github.agdavydov81.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint paint = new Paint();
    private List<PointF> points = new ArrayList<>();

    private float scaleFactor = 1.0f;
    private float translationX = 0.0f;
    private float translationY = 0.0f;

    private float lastTouchX;
    private float lastTouchY;

    private ScaleGestureDetector scaleGestureDetector;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(translationX, translationY);
        canvas.scale(scaleFactor, scaleFactor);

        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, 10, paint);
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = (event.getX() - translationX) / scaleFactor;
                final float y = (event.getY() - translationY) / scaleFactor;
                points.add(new PointF(x, y));
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!scaleGestureDetector.isInProgress()) {
                    final float dx = event.getX() - lastTouchX;
                    final float dy = event.getY() - lastTouchY;
                    translationX += dx;
                    translationY += dy;
                    invalidate();
                }
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            invalidate();
            return true;
        }
    }
}
