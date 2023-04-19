package com.example.ballhit;

public class Wall extends Brick {
    public Wall(int row, int column, int width, int height) {
        super(row, column, width, height);
    }

    @Override
    public void setInVisible() {
        this.isVisible = true;
    }
}
