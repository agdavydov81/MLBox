package io.github.agdavydov81;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.github.agdavydov81.drawing.DrawingView;

public class ClassificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        DrawingView drawingView = findViewById(R.id.drawing_view);

        findViewById(R.id.btn_circle).setOnClickListener(v -> drawingView.setShapeType(DrawingView.ShapeType.CIRCLE));
        findViewById(R.id.btn_square).setOnClickListener(v -> drawingView.setShapeType(DrawingView.ShapeType.SQUARE));
        findViewById(R.id.btn_triangle).setOnClickListener(v -> drawingView.setShapeType(DrawingView.ShapeType.TRIANGLE));
    }
}