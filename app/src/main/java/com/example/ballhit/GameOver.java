package com.example.ballhit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class GameOver extends AppCompatActivity {

    TextView tvPoints, tvUsername, tvHighestScore;
    ImageView ivNewHighest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        tvPoints = findViewById(R.id.tvPoints);
        tvUsername = findViewById(R.id.tvUsername);
        tvHighestScore = findViewById(R.id.tvHighestScore);
        ivNewHighest = findViewById(R.id.ivNewHighest);

        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
        tvUsername.setText(username);

        // Read the highest score for this user from a local file
        int highestScore = preferences.getInt(username, 0);

        int points = getIntent().getExtras().getInt("points");
        if (points > highestScore) {
            // Update the highest score and save it to the local file
            highestScore = points;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(username, highestScore);
            editor.apply();
            ivNewHighest.setVisibility(View.VISIBLE);
        }
        tvPoints.setText("" + points);
        tvHighestScore.setText("" + highestScore);
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }
}

