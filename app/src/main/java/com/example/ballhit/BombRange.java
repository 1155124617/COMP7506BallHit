package com.example.ballhit;

public class BombRange {
    private int left, right, top, bottom;
    public BombRange(int left, int right, int top, int bottom){
        this.left=left;
        this.right=right;
        this.top=top;
        this.bottom=bottom;
    }

    public boolean containBrick(Brick brick){
        return brick.right<=this.left && brick.left>=this.left && brick.bottom<=this.bottom && brick.top>=this.top;
    }
}
