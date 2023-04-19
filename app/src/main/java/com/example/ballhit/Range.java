package com.example.ballhit;

public class Range {
    private float lower;
    private float upper;

    public Range(float lower, float upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean isIn(float n) {
        return n >= lower && n <= upper;
    }

    public float fitIn(float n) {
        if (n < lower) {
            return lower;
        }
        else if (n > upper) {
            return upper;
        }
        else {
            return n;
        }
    }
}
