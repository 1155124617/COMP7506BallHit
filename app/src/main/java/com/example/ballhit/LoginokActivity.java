package com.example.ballhit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginokActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }
    public void startGame(View view) {
        Intent intent = new Intent(this, GamePlay.class);
        startActivity(intent);
        finish();
    }
}

