package com.example.ballhit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View  {

    Context context;
    float ballX, ballY;
    Velocity velocity = new Velocity(23, 32); // Velocity of the ball
    Handler handler; // Delay UPDATE_MILLIS to run runnable thread
    final long UPDATE_MILLIS = 30; // Control FPS, recommended 30 ms
    Runnable runnable; // Background thread to call invalidate to call onDraw()
    Paint textPaint = new Paint(); // Paint object for text rendering
    Paint healthPaint = new Paint(); // Paint object for health bar rendering
    Paint brickPaint = new Paint(); // Paint object for brick rendering
    float TEXT_SIZE = 120; // Text size for countdown
    float paddleX, paddleY; // X and Y coordinates of the paddle
    float oldX, oldPaddleX; // Old X coordinates of the paddle
    int points = 0; // Player's points
    int goalPionts = 30;
    int turn = 0;
    int life = 3; // Player's remaining life
    Bitmap ball, paddle; // Bitmap images for ball and paddle
    int dWidth, dHeight; // Width and height of the screen
    int ballWidth, ballHeight; // Width and height of the ball
    MediaPlayer mpHit, mpMiss, mpBreak; // Media players for sound effects
    Random random; // Random object for generating random numbers
    Brick[] bricks = new Brick[30]; // Array of bricks
    int numBricks = 0; // Number of bricks
    int brokenBricks = 0; // Number of broken bricks
    boolean gameOver = false; // Flag to indicate if game is over
    private CountDownTimerFactory.CountDownTimerExt countDownTimer; // Countdown timer for game duration
    private boolean stopGame;

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }
    public GameView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); // Call onDraw() to redraw the view
            }
        };
        mpHit = MediaPlayer.create(context, R.raw.hit);
        mpMiss =  MediaPlayer.create(context, R.raw.miss);
        mpBreak = MediaPlayer.create(context, R.raw.breaking);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.GREEN);
        brickPaint.setColor(Color.argb(255, 249, 129, 0));
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        random = new Random();
        ballX = random.nextInt(dWidth - 50);
        ballY = dHeight / 3;
        paddleY = (dHeight * 4) / 5;
        paddleX = dWidth / 2 - paddle.getWidth() / 2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        countDownTimer = CountDownTimerFactory.getInstance(60000);
        countDownTimer.start();
        stopGame = false;
        createBricks();
    }

    private void createBricks() {
        int brickWidth = dWidth / 8;
        int brickHeight = dHeight / 16;
        for (int column = 0 ; column < 8 ; column ++) {
            for (int row = 0 ; row < 3 ; row++) {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        if (countDownTimer.remainingTime == 0) {
            // Time is up
            gameOver = true;
            endTurn();
        }

        // Calculate minutes and seconds from remaining time

        int minutes = (int) (countDownTimer.remainingTime / 1000) / 60;
        int seconds = (int) (countDownTimer.remainingTime / 1000) % 60;

        // Draw countdown text on canvas
        String countdownText = String.format("%02d:%02d", minutes, seconds);
        float countdownTextSize = textPaint.measureText(countdownText);

        ballX += velocity.getX();
        ballY += velocity.getY();
        if ((ballX >= dWidth - ball.getWidth()) || ballX <= 0 ) {
            velocity.setX(velocity.getX() * -1);

        }
        if (ballY <= 0) {
            velocity.setY(velocity.getY() * -1);
        }
        if (ballY > paddleY + paddle.getHeight()) {
            ballX = 1 + random.nextInt(dWidth - ball.getWidth() - 1);
            ballY = dHeight / 3;
            if (mpMiss != null) {
                mpMiss.start();
            }
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life == 0) {
                gameOver = true;
                endTurn();
            }
        }
        if (velocity.getY() >= 0) {
            // Only when the ball falls down to the paddle, the collision will be included
            // Fix the bug that the ball bounces within paddle boundary
            if (((ballX + ball.getWidth()) >= paddleX)
                    && (ballX <= paddleX + paddle.getWidth())
                    && (ballY + ball.getHeight() >= paddleY)
                    && (ballY + ball.getHeight() <= paddleY + paddle.getHeight())) {
                if (mpHit != null) {
                    mpHit.start();
                }
                velocity.setX(velocity.getX() + 1);
                velocity.setY((velocity.getY() + 1) * -1);
            }
        }
        canvas.drawBitmap(ball, ballX, ballY, null);
        canvas.drawBitmap(paddle, paddleX, paddleY, null);
        for (int i = 0 ; i < numBricks ; i++) {
            if (bricks[i].getVisibility()) {
                canvas.drawRect(bricks[i].column * bricks[i].width + 1, bricks[i].row * bricks[i].height + 1, bricks[i].column * bricks[i].width + bricks[i].width - 1, bricks[i].row * bricks[i].height + bricks[i].height - 1, brickPaint);
            }
        }
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        canvas.drawText(countdownText, (getWidth() - countdownTextSize) / 2f, textPaint.getTextSize(), textPaint);
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth-200, 30, dWidth - 200 + 60 * life, 80 ,healthPaint);
        for (int i = 0 ; i < numBricks ; i++) {
            if (bricks[i].getVisibility()) {
                if (ballX + ballWidth >= bricks[i].column * bricks[i].width
                && ballX <= bricks[i].column * bricks[i].width + bricks[i].width
                && ballY <= bricks[i].row * bricks[i].height + bricks[i].height
                && ballY >= bricks[i].row * bricks[i].height) {
                    if (mpBreak != null) {
                        mpBreak.start();
                    }
                    velocity.setY((velocity.getY() + 1) * -1);
                    bricks[i].setInVisible();
                    points += 10;
                    brokenBricks++;
                    if (brokenBricks == 24) {
                        endTurn();
                    }
                    break;
                }
            }
        }

        if (brokenBricks == numBricks) {
            // All blocks are eliminated
            gameOver = true;
        }
        if (!gameOver && !stopGame) {
            handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= paddleY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if (newPaddleX <= 0) {
                    paddleX = 0;
                }
                else if (newPaddleX >= dWidth - paddle.getWidth()) {
                    paddleX = dWidth - paddle.getWidth();
                }
                else {
                    paddleX = newPaddleX;
                }
            }
        }
        return true;
    }

    private void endTurn(){
        if (points >= goalPionts){
            Intent intent = new Intent(context, GamePlay.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity)context).finish();
        }else{
            launchGameOver();
        }
    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private int xVelocity() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(6);
        return values[index];
    }

    public void stopGame() {
        stopGame = true;
        long remaingTime = countDownTimer.remainingTime;
        countDownTimer.cancel();
        countDownTimer = CountDownTimerFactory.getInstance(remaingTime);
    }

    public void resumeGame() {
        stopGame = false;
        countDownTimer.start();
        invalidate();
    }
}
