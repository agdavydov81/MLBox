package io.github.agdavydov81.data;

import java.util.Arrays;

public class DataClass {
    public static final int PLANE_DIMENSION = 2;

    public DataClass(String name, LabelInfo label, int color) {
        this(name, label, color, PLANE_DIMENSION);
    }

    public DataClass(String name, LabelInfo label, int color, int dimension) {
        this.name = name;
        this.label = label;
        this.color = color;
        this.dimension = dimension;
        points = new float[1024 * dimension];
    }

    public final String name;
    public final LabelInfo label;
    public final int color;
    public final int dimension;

    float[] points;
    int numPoints = 0;

    public void add(float x, float y) {
        if ((numPoints + 1) * dimension > points.length)
            points = Arrays.copyOf(points, (numPoints * 3 / 2 + 1) * dimension);
        int offset = numPoints * dimension;
        points[offset] = x;
        points[offset + 1] = y;
        numPoints++;
    }
}
