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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

enum Border {
    LEFT,
    RIGHT,
    TOP
}

public class GameView extends View  {

    Context context;
    float ballX, ballY;
    Velocity velocity = new Velocity(23, 32); // Velocity of the ball
    Handler delayedThreadExec; // Delay UPDATE_MILLIS to run runnable thread
    final long UPDATE_MILLIS = 30; // Control FPS, recommended 30 ms
    Runnable refreshThread; // Background thread to call invalidate to call onDraw()
    Paint textPaint = new Paint(); // Paint object for text rendering
    Paint healthPaint = new Paint(); // Paint object for health bar rendering
    Paint brickPaint = new Paint(); // Paint object for brick rendering
    Paint wallPaint = new Paint(); // Paint object for wall rendering
    float TEXT_SIZE = 120; // Text size for countdown
    float paddleX, paddleY; // X and Y coordinates of the paddle
    float pressedX, pressedPaddleX, pressedY, pressedPaddleY; // Record press event position
    Range paddleMoveRangeX, paddleMoveRangeY; // Define movement range of paddle
    int points = 0; // Player's points
    int goalPoints = 30;
    int turn = 0;
    int lifePlus=0;
    int bomb;
    boolean setBomb =false;
    BombRange bombRange;
    int timePlus=0;
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
    CountDownTimerFactory.CountDownTimerExt countDownTimer; // Countdown timer for game duration
    private boolean stopGame;
    Brick lastVisitedBrick;
    Border touchedBorder;

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
        delayedThreadExec = new Handler();
        refreshThread = new Runnable() {
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
        wallPaint.setColor(Color.BLUE);
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
        paddleMoveRangeX = new Range(0, dWidth - paddle.getWidth());
        paddleMoveRangeY = new Range((dHeight * 3) / 5, (dHeight * 4) / 5);
        countDownTimer = CountDownTimerFactory.getInstance(60000);
        countDownTimer.start();
        stopGame = false;
        createBricks();
    }

    private void createBricks() {
        int brickWidth = dWidth / 8;
        int brickHeight = dHeight / 16;
        Set<Integer> distinctNumbers = new HashSet<>();
        Random random = new Random();

        // First row is full arranged with bricks
        for (int column = 0 ; column < 8 ; column++) {
            bricks[numBricks] = new Brick(0, column, brickWidth, brickHeight);
            numBricks++;
        }

        // Other 14 is randomly allocated
        for (; numBricks < 24 ; numBricks++) {
            int randomPos = random.nextInt(32) + 8;
            while (distinctNumbers.contains(randomPos)) {
                randomPos = random.nextInt(32) + 8;
            }
            distinctNumbers.add(randomPos);
            bricks[numBricks] = new Brick(randomPos / 8, randomPos % 8, brickWidth, brickHeight);
        }

        // 5 bricks of wall
        for (int i = 1 ; i <= 3 ; i++) {
            int randomPos = random.nextInt(32) + 8;
            while (distinctNumbers.contains(randomPos)) {
                randomPos = random.nextInt(32) + 8;
            }
            distinctNumbers.add(randomPos);
            bricks[numBricks + i] = new Wall(randomPos / 8, randomPos % 8, brickWidth, brickHeight);
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
        interactBorder();
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
                lastVisitedBrick = null;
                touchedBorder = null;

                velocity.setY((velocity.getY()) * -1);
            }
        }
        canvas.drawBitmap(ball, ballX, ballY, null);
        canvas.drawBitmap(paddle, paddleX, paddleY, null);
        for (Brick brick : bricks) {
            if (brick != null && brick.getVisibility()) {
                if (brick instanceof Wall) {
                    canvas.drawRect(brick.left + 1, brick.top + 1, brick.right - 1, brick.bottom - 1, wallPaint);
                }
                else {
                    canvas.drawRect(brick.left + 1, brick.top + 1, brick.right - 1, brick.bottom - 1, brickPaint);
                }
            }
        }
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        canvas.drawText(countdownText, (getWidth() - countdownTextSize) / 2f, textPaint.getTextSize(), textPaint);
        if (life == 3) {
            healthPaint.setColor(Color.GREEN);
        }
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth-200, 30, dWidth - 200 + 60 * life, 80 ,healthPaint);
        for (Brick brick : bricks) {
            if (brick != null && (lastVisitedBrick != brick) && brick.collision(ballX, ballX + ballWidth, ballY, ballY + ballHeight)) {
                if (mpBreak != null) {
                    lastVisitedBrick = brick;
                    touchedBorder = null;
                    mpBreak.start();
                    velocity = brick.velocityAfterCollision(velocity, ballX + ballWidth/2, ballY + ballHeight/2);
                    if (setBomb){
                        setBomb=false;
                        bombRange = new BombRange(brick.left-dWidth/8, brick.right+dWidth/8, brick.top-dHeight/16, brick.bottom+dHeight/16);
                    }
                    if (!(brick instanceof Wall)) {
                        brick.setInVisible();
                        points += 10;
                        brokenBricks++;
                    }
                    if (brokenBricks == 24) {
                        endTurn();
                    }
                    break;
                }
            }
        }
        for (Brick brick : bricks){
            if (brick!=null && bombRange!=null && brick.getVisibility() && bombRange.containBrick(brick)){
                brick.setInVisible();
                points += 10;
                if (!(brick instanceof Wall)) {
                    brokenBricks++;
                }
            }
        }
        bombRange=null;

        if (brokenBricks == numBricks) {
            // All blocks are eliminated
            gameOver = true;
            Toast toast=Toast.makeText(context, "Congratulations! \nYou have cleaned all bricks.\npoints +100", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        if (!gameOver && !stopGame) {
            delayedThreadExec.postDelayed(refreshThread, UPDATE_MILLIS);
        }
    }

    private void interactBorder() {
        if (ballX <= 0 && touchedBorder != Border.LEFT) {
            lastVisitedBrick = null;
            touchedBorder = Border.LEFT;
            velocity.setX(velocity.getX() * -1);

        }
        if (ballX >= dWidth - ballWidth && touchedBorder != Border.RIGHT) {
            lastVisitedBrick = null;
            touchedBorder = Border.RIGHT;
            velocity.setX(velocity.getX() * -1);
        }
        if (ballY <= 0 && touchedBorder != Border.TOP) {
            lastVisitedBrick = null;
            touchedBorder = Border.TOP;
            velocity.setY(velocity.getY() * -1);
        }
        if (ballY > (dHeight * 4) / 5 + paddle.getHeight()) {
            lastVisitedBrick = null;
            touchedBorder = null;

            ballX = 1 + random.nextInt(dWidth - ballWidth - 1);
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() >= paddleY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                pressedX = event.getX();
                pressedY = event.getY();
                pressedPaddleX = paddleX;
                pressedPaddleY = paddleY;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shiftX = pressedX - event.getX();
                float shiftY = pressedY - event.getY();
                paddleX = paddleMoveRangeX.fitIn(pressedPaddleX - shiftX);
                paddleY = paddleMoveRangeY.fitIn(pressedPaddleY - shiftY);
            }
        }
        return true;
    }

    private void endTurn(){
        if (points >= goalPoints){
            Intent intent = new Intent(context, Store.class);
            intent.putExtra("points", points);
            intent.putExtra("goalPoints", goalPoints);
            intent.putExtra("turn", turn);
            intent.putExtra("lifePlus", lifePlus);
            intent.putExtra("timePlus", timePlus);
            context.startActivity(intent);
            ((Activity)context).finish();
        }else{
            launchGameOver();
        }
    }

    private void launchGameOver() {
        delayedThreadExec.removeCallbacksAndMessages(null);
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
        long remainingTime = countDownTimer.remainingTime;
        countDownTimer.cancel();
        countDownTimer = CountDownTimerFactory.getInstance(remainingTime);
    }

    public void resumeGame() {
        stopGame = false;
        countDownTimer.start();
        invalidate();
    }

    public void addTimeCountDown(long addTime) {
        long remainingTime = countDownTimer.remainingTime;
        countDownTimer.cancel();
        countDownTimer = CountDownTimerFactory.getInstance(remainingTime + addTime);
        countDownTimer.start();
    }
}
