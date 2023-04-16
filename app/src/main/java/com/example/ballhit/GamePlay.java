package com.example.ballhit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GamePlay extends AppCompatActivity {
    private PopupWindow popupWindow;
    private GameView gameView;
    TextView tvGoalPoints, tvTurn, tvLifePlusNum, tvTimePlusNum;
    ImageButton ibLifePlus, ibTimePlus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_play);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        gameView = findViewById(R.id.gameview);
        if (getIntent().getExtras() != null) {
            gameView.points = getIntent().getExtras().getInt("points");
            gameView.turn = getIntent() .getExtras().getInt("turn")+ 1;
            gameView.goalPoints = getIntent().getExtras().getInt("goalPoints") + (int)(30*(gameView.turn*0.5+1));
            gameView.timePlus=getIntent().getExtras().getInt("timePlus");
            gameView.lifePlus=getIntent().getExtras().getInt("lifePlus");
        }
        tvGoalPoints = findViewById(R.id.goalPoints);
        tvTurn = findViewById(R.id.turn);
        tvGoalPoints.append(""+gameView.goalPoints);
        tvTurn.append(""+gameView.turn);

        ibLifePlus=findViewById(R.id.lifePlus);
        ibTimePlus=findViewById(R.id.timePlus);
        tvLifePlusNum=findViewById(R.id.lifePlusNum);
        tvTimePlusNum=findViewById(R.id.timePlusNum);
        if(gameView.timePlus==0){
            ibTimePlus.setClickable(false);
            ibTimePlus.setImageResource(R.drawable.time_plus_disable);
        }else{
            tvTimePlusNum.setText("×"+gameView.timePlus);
            ibTimePlus.setClickable(true);
            ibTimePlus.setImageResource(R.drawable.time_plus);
        }
        if(gameView.lifePlus==0){
            ibLifePlus.setClickable(false);
            ibLifePlus.setImageResource(R.drawable.life_plus_disable);
        }else{
            tvLifePlusNum.setText("×"+gameView.lifePlus);
            ibLifePlus.setClickable(true);
            ibLifePlus.setImageResource(R.drawable.life_plus);
        }
    }

    public void showOverlay(View view) {
        gameView.stopGame();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View overlayView = inflater.inflate(R.layout.overlay_layout, null);
        // Create a PopupWindow or a Dialog to show the overlay page
        popupWindow = new PopupWindow(overlayView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void onOptionButtonClick(View view) {
        // Get the ID of the clicked view
        int viewId = view.getId();

        if (viewId == R.id.resume) {
            // Handle option 1 click event
            // Toast.makeText(this, "Option 1 clicked", Toast.LENGTH_SHORT).show();
            // Perform the action to go back to the main page, e.g. finish the current activity
            popupWindow.dismiss();
            gameView.resumeGame();
        } else if (viewId == R.id.exit) {
            // Handle option 2 click event
            // Toast.makeText(this, "Option 2 clicked", Toast.LENGTH_SHORT).show();
            // Add your logic for option 2 here
            finish();
        }
    }

    public void lifePlus(View view){
        if (gameView.life==3){
            return;
        }
        gameView.life+=1;
        gameView.lifePlus-=1;
        if (gameView.lifePlus==0){
            ibLifePlus.setClickable(false);
            ibLifePlus.setImageResource(R.drawable.life_plus_disable);
            tvLifePlusNum.setText("");
        }else{
            tvLifePlusNum.setText("×"+gameView.lifePlus);
        }
    }

    public void timePlus(View view){
        gameView.timePlus-=1;
        //todo: time +10s
        if (gameView.timePlus==0){
            ibTimePlus.setClickable(false);
            ibTimePlus.setImageResource(R.drawable.time_plus_disable);
            tvTimePlusNum.setText("");
        }else{
            tvTimePlusNum.setText("×"+gameView.timePlus);
        }
    }


}
