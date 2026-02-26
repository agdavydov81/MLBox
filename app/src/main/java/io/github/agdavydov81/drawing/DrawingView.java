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
    private final Paint textPaint = new Paint();
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

        textPaint.setColor(Color.DKGRAY);
        textPaint.setAntiAlias(true);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        // --- Cartesian coordinate system setup ---
        canvas.translate(0, getHeight());
        canvas.scale(1, -1);
        // ----------------------------------------

        canvas.translate(translationX, translationY);
        canvas.scale(scaleFactor, scaleFactor);

        drawGrid(canvas);

        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, 10 / scaleFactor, paint);
        }

        canvas.restore();
    }

    private static final int PREFERRED_GRID_LINES = 12;

    private static final float[] gridFactors = new float[] {1, 2, 5};

    private void drawGrid(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();

        final float left = -translationX / scaleFactor;
        final float right = (width - translationX) / scaleFactor;
        final float bottom = -translationY / scaleFactor;
        final float top = (height - translationY) / scaleFactor;

        final var scaledSize = Math.max(width, height) / scaleFactor;
        float gridSize125 = Math.round(Math.log10(scaledSize / PREFERRED_GRID_LINES) * gridFactors.length);
        float gridSize = (float)(Math.pow(10.0, (long)(gridSize125 / gridFactors.length)) *
                gridFactors[(int) gridSize125 % gridFactors.length]);

        textPaint.setTextSize(28 / scaleFactor);

        // Draw vertical lines and labels (X-axis)
        for (float x = (float) (Math.floor(left / gridSize) * gridSize); x < right; x += gridSize) {
            canvas.drawLine(x, top, x, bottom, gridPaint);
            String text = String.valueOf((int) x);
            canvas.save();
            canvas.translate(x, bottom);
            canvas.scale(1, -1); // Flip back to draw text upright
            canvas.drawText(text, (5 / scaleFactor), -(5 / scaleFactor), textPaint);
            canvas.restore();
        }

        // Draw horizontal lines and labels (Y-axis)
        for (float y = (float) (Math.floor(bottom / gridSize) * gridSize); y < top; y += gridSize) {
            canvas.drawLine(left, y, right, y, gridPaint);
            String text = String.valueOf((int) y);
            canvas.save();
            canvas.translate(left, y);
            canvas.scale(1, -1); // Flip back to draw text upright
            canvas.drawText(text, (5 / scaleFactor), -(5 / scaleFactor), textPaint);
            canvas.restore();
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

                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isDragging = true;
                    }

                    if (isDragging) {
                        translationX += dx;
                        translationY -= dy; // Invert dy for Cartesian panning
                        invalidate();
                    }
                }
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isDragging && !scaleGestureDetector.isInProgress()) {
                    final float worldX = (event.getX() - translationX) / scaleFactor;
                    final float worldY = (getHeight() - event.getY() - translationY) / scaleFactor;
                    points.add(new PointF(worldX, worldY));
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