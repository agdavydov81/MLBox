package io.github.agdavydov81.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    public enum ShapeType {
        CIRCLE, SQUARE, TRIANGLE
    }

    private static class DrawingPoint {
        PointF point;
        ShapeType type;

        DrawingPoint(PointF point, ShapeType type) {
            this.point = point;
            this.type = type;
        }
    }

    private final Paint paint = new Paint();
    private final Paint gridPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final List<DrawingPoint> points = new ArrayList<>();
    private ShapeType currentShapeType = ShapeType.CIRCLE;

    private float scaleFactor = 1.0f;
    private float translationX = 0.0f;
    private float translationY = 0.0f;

    private float lastTouchX;
    private float lastTouchY;
    private boolean isDragging = false;
    private boolean wasScaling = false;

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

    public void setShapeType(ShapeType type) {
        this.currentShapeType = type;
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

        float size = 10 / scaleFactor;
        for (DrawingPoint dp : points) {
            drawShape(canvas, dp.point.x, dp.point.y, size, dp.type);
        }

        canvas.restore();
    }

    private void drawShape(Canvas canvas, float x, float y, float size, ShapeType type) {
        switch (type) {
            case CIRCLE:
                canvas.drawCircle(x, y, size, paint);
                break;
            case SQUARE:
                canvas.drawRect(x - size, y - size, x + size, y + size, paint);
                break;
            case TRIANGLE:
                Path path = new Path();
                path.moveTo(x, y + size); // Top
                path.lineTo(x - size, y - size); // Bottom Left
                path.lineTo(x + size, y - size); // Bottom Right
                path.close();
                canvas.drawPath(path, paint);
                break;
        }
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
        gridPaint.setStrokeWidth(1f / scaleFactor);

        // Draw vertical lines and labels (X-axis)
        for (float x = (float) (Math.floor(left / gridSize) * gridSize); x < right; x += gridSize) {
            canvas.drawLine(x, top, x, bottom, gridPaint);
            String text = String.valueOf((int) x);
            canvas.save();
            canvas.translate(x, bottom);
            canvas.scale(1, -1);
            canvas.drawText(text, (5 / scaleFactor), -(5 / scaleFactor), textPaint);
            canvas.restore();
        }

        // Draw horizontal lines and labels (Y-axis)
        for (float y = (float) (Math.floor(bottom / gridSize) * gridSize); y < top; y += gridSize) {
            canvas.drawLine(left, y, right, y, gridPaint);
            String text = String.valueOf((int) y);
            canvas.save();
            canvas.translate(left, y);
            canvas.scale(1, -1);
            canvas.drawText(text, (5 / scaleFactor), -(5 / scaleFactor), textPaint);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isDragging = false;
                wasScaling = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Only allow panning if we are not currently scaling and haven't scaled during this gesture
                if (!scaleGestureDetector.isInProgress() && !wasScaling) {
                    final float dx = event.getX() - lastTouchX;
                    final float dy = event.getY() - lastTouchY;

                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                        isDragging = true;
                    }

                    if (isDragging) {
                        translationX += dx;
                        translationY -= dy;
                        invalidate();
                    }
                }
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isDragging && !wasScaling && !scaleGestureDetector.isInProgress()) {
                    final float worldX = (event.getX() - translationX) / scaleFactor;
                    final float worldY = (getHeight() - event.getY() - translationY) / scaleFactor;
                    points.add(new DrawingPoint(new PointF(worldX, worldY), currentShapeType));
                    invalidate();
                }
                isDragging = false;
                wasScaling = false;
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            wasScaling = true;
            isDragging = false; // Stop any ongoing drag when scaling starts
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float f = detector.getScaleFactor();
            float newScaleFactor = Math.max(0.1f, Math.min(scaleFactor * f, 10.0f));

            // Adjust translation to zoom into the center of the screen
            float cartCenterX = getWidth() / 2.0f;
            float cartCenterY = getHeight() / 2.0f;

            // Re-calculate the actual ratio based on clamped scale to avoid coordinate drift
            float actualF = newScaleFactor / scaleFactor;

            translationX = translationX * actualF + cartCenterX * (1 - actualF);
            translationY = translationY * actualF + cartCenterY * (1 - actualF);

            scaleFactor = newScaleFactor;

            invalidate();
            return true;
        }
    }
}
