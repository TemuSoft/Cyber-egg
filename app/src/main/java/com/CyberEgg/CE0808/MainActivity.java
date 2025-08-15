package com.CyberEgg.CE0808;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView sound, music, shop;
    private Button start;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private Random random;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("CyberEg9e3CE0", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");

        setContentView(R.layout.activity_main);

        sound = findViewById(R.id.sound);
        music = findViewById(R.id.music);
        shop = findViewById(R.id.shop);
        start = findViewById(R.id.start);

        Player.all_screens(this, R.raw.all_screens);
        Player.button(this, R.raw.button);


        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);

            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
                intent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
                intent = new Intent(MainActivity.this, ModeActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!isMute)
            Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (!isMute)
            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}