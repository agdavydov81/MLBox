package io.github.agdavydov81.data;

public class StatInfo {
    public double min = Double.NaN;
    public double max = Double.NaN;

    public double sum = 0;

    public double sumSq = 0;

    public long count = 0;

    public double mean() {
        return sum / count;
    }

    public double variance() {
        return Math.max(0.0, sumSq - sum * sum / count) / (count - 1);
    }

    public double std() {
        return Math.sqrt(variance());
    }

    public void add(double value) {
        if (count == 0L) {
            min = value;
            max = value;
        }
        else {
            min = Math.min(value, min);
            max = Math.max(value, max);
        }
        sum += value;
        sumSq += value * value;
        count++;
    }
}
