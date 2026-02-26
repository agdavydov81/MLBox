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

    private final Paint paint = new Paint();
    private final Paint gridPaint = new Paint();
    private final List<PointF> points = new ArrayList<>();

    private float scaleFactor = 1.0f;
    private float translationX = 0.0f;
    private float translationY = 0.0f;

    private float lastTouchX;
    private float lastTouchY;
    private boolean isDragging = false;

    private final ScaleGestureDetector scaleGestureDetector;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(translationX, translationY);
        canvas.scale(scaleFactor, scaleFactor);

        drawGrid(canvas);

        // Adjust point size based on zoom
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, 10 / scaleFactor, paint);
        }

        canvas.restore();
    }

    private void drawGrid(Canvas canvas) {
        final float gridSize = 100f;
        final int width = getWidth();
        final int height = getHeight();

        // Calculate the visible area in world coordinates
        final float left = -translationX / scaleFactor;
        final float top = -translationY / scaleFactor;
        final float right = (width - translationX) / scaleFactor;
        final float bottom = (height - translationY) / scaleFactor;

        // Draw vertical lines
        for (float x = (float) (Math.floor(left / gridSize) * gridSize); x < right; x += gridSize) {
            canvas.drawLine(x, top, x, bottom, gridPaint);
        }

        // Draw horizontal lines
        for (float y = (float) (Math.floor(top / gridSize) * gridSize); y < bottom; y += gridSize) {
            canvas.drawLine(left, y, right, y, gridPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isDragging = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!scaleGestureDetector.isInProgress()) {
                    final float dx = event.getX() - lastTouchX;
                    final float dy = event.getY() - lastTouchY;
                    
                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                        isDragging = true;
                    }

                    if (isDragging) {
                        translationX += dx;
                        translationY += dy;
                        invalidate();
                    }
                }
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isDragging && !scaleGestureDetector.isInProgress()) {
                    final float x = (event.getX() - translationX) / scaleFactor;
                    final float y = (event.getY() - translationY) / scaleFactor;
                    points.add(new PointF(x, y));
                    invalidate();
                }
                isDragging = false;
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
