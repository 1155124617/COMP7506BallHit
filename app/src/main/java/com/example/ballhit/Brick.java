package com.example.ballhit;

public class Brick {

    protected boolean isVisible;
    public int left, right, top, bottom; // Border of the brick

    public Brick(int row, int column, int width, int height) {
        isVisible = true;

        this.left = column * width;
        this.right = (column + 1) * width;
        this.top = row * height;
        this.bottom = (row + 1) * height;
    }

    public void setInVisible() {
        this.isVisible = false;
    }

    public boolean getVisibility() {
        return this.isVisible;
    }

    public boolean collision(float left, float right, float top, float bottom) {
        return isVisible
                && right >= this.left
                && left <= this.right
                && top <= this.bottom
                && bottom >= this.top;
    }

    public Velocity velocityAfterCollision(Velocity velocity, float centerX, float centerY) {
        int Vx = velocity.getX();
        int Vy = velocity.getY();
        if (Vx >= 0 && Vy >= 0) {
            // Determine which quadrants the ball drops in
            if (centerX >= left && centerY < top) {
                return new Velocity(Vx, -Vy);
            }
            else if (centerX < left && centerY >= top) {
                return new Velocity(-Vx, Vy);
            }
            else if (centerX < left && centerY < top) {
                if (Math.abs(centerY - top) / Math.abs(centerX - left) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(Vx, -Vy);
                }
                else {
                    return new Velocity(-Vx, Vy);
                }
            }
            else {
                if (Math.abs(centerY - top) / Math.abs(centerX - left) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(-Vx, Vy);
                }
                else {
                    return new Velocity(Vx, -Vy);
                }
            }
        }

        else if (Vx >= 0) {
            // Determine which quadrants the ball drops in
            if (centerX >= left && centerY >= bottom) {
                return new Velocity(Vx, -Vy);
            }
            else if (centerX < left && centerY < bottom) {
                return new Velocity(-Vx, Vy);
            }
            else if (centerX < left && centerY >= bottom) {
                if (Math.abs(centerY - bottom) / Math.abs(centerX - left) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(Vx, -Vy);
                }
                else {
                    return new Velocity(-Vx, Vy);
                }
            }
            else {
                if (Math.abs(centerY - bottom) / Math.abs(centerX - left) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(-Vx, Vy);
                }
                else {
                    return new Velocity(Vx, -Vy);
                }
            }
        }

        else if (Vy >= 0) {
            // Determine which quadrants the ball drops in
            if (centerX < right && centerY < top) {
                return new Velocity(Vx, -Vy);
            }
            else if (centerX >= right && centerY >= top) {
                return new Velocity(-Vx, Vy);
            }
            else if (centerX >= right && centerY < top) {
                if (Math.abs(centerY - top) / Math.abs(centerX - right) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(Vx, -Vy);
                }
                else {
                    return new Velocity(-Vx, Vy);
                }
            }
            else {
                if (Math.abs(centerY - top) / Math.abs(centerX - right) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(-Vx, Vy);
                }
                else {
                    return new Velocity(Vx, -Vy);
                }
            }
        }

        else {
            // Determine which quadrants the ball drops in
            if (centerX < right && centerY >= bottom) {
                return new Velocity(Vx, -Vy);
            }
            else if (centerX >= right && centerY < bottom) {
                return new Velocity(-Vx, Vy);
            }
            else if (centerX >=right && centerY >= bottom) {
                if (Math.abs(centerY - bottom) / Math.abs(centerX - right) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(Vx, -Vy);
                }
                else {
                    return new Velocity(-Vx, Vy);
                }
            }
            else {
                if (Math.abs(centerY - bottom) / Math.abs(centerX - right) >= Math.abs((float) Vy / Vx)) {
                    return new Velocity(-Vx, Vy);
                }
                else {
                    return new Velocity(Vx, -Vy);
                }
            }
        }
    }

}
