package net.ctwatch.utils;

public class SlidingWindow {
    private final int minimum = 1;
    private final int maximum = 2000;
    private int windowSize = maximum;

    public void onSuccess() {
        windowSize = Math.min(windowSize * 2, maximum);
    }

    public void onFailure() {
        windowSize = minimum;
    }

    public int windowSize() {
        return this.windowSize;
    }
}
