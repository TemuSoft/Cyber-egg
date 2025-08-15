package com.CyberEgg.CE0808;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener{
    private LinearLayout layout_canvas;
    private LayoutInflater inflate;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;
    private AlertDialog.Builder builder;
    private Random random;
    private Handler handler;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("smashp78ucks489", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");

        setContentView(R.layout.activity_game);

        builder = new AlertDialog.Builder(this);
        random = new Random();
        handler = new Handler();


        layout_canvas = findViewById(R.id.layout_canvas);
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout_canvas.removeAllViews();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        int w = point.x;
        int h = point.y;
        gameView = new GameView (this, w, h, getResources(), 0);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        layout_canvas.addView(gameView);

        layout_canvas.setOnTouchListener(this);
        reloading_UI();
    }


    private void reloading_UI(){
        Runnable r = new Runnable() {
            public void run() {
                if (gameView.isPlaying){
                    gameView.update();

                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

	
    private void game_over(){
        gameView.isPlaying = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.isPlaying = false;
        if (!isMute)
            Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.isPlaying = true;
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (!isMute)
            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                processActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                processActionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                processActionUp(x, y);
                break;
        }
        return true;
    }

    private void processActionDown(int x, int y) {

    }

    private void processActionUp(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp ,yp);

    }

    private void processActionMove(int x, int y) {

    }
}