package com.CyberEgg.CE0808;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ModeActivity extends AppCompatActivity {
    private ImageView back, single_mode, two_player_mode, call_mode;
    private Button single_mode_btn, two_player_mode_btn, call_mode_btn;
    private Button play;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private Random random;
    private String lang;
    private String game_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("CyberEg9e3CE0", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");
        game_mode = sharedPreferences.getString("game_mode", "single_mode");

        setContentView(R.layout.activity_mode);

        back = findViewById(R.id.back);
        single_mode = findViewById(R.id.single_mode);
        two_player_mode = findViewById(R.id.two_player_mode);
        call_mode = findViewById(R.id.call_mode);
        play = findViewById(R.id.play);

        single_mode_btn = findViewById(R.id.single_mode_btn);
        two_player_mode_btn = findViewById(R.id.two_player_mode_btn);
        call_mode_btn = findViewById(R.id.call_mode_btn);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
                finish();
            }
        });
        single_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
                game_mode = "single_mode";
                arrange_UI();
            }
        });
        two_player_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
                game_mode = "two_player_mode";
                arrange_UI();
            }
        });
        call_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);
                game_mode = "call_mode";
                arrange_UI();
            }
        });
        play.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(ModeActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }

    private void arrange_UI() {
        single_mode_btn.setBackgroundResource(R.drawable.btn_gray);
        two_player_mode_btn.setBackgroundResource(R.drawable.btn_gray);
        call_mode_btn.setBackgroundResource(R.drawable.btn_gray);
        single_mode_btn.setText(getResources().getString(R.string.select));
        two_player_mode_btn.setText(getResources().getString(R.string.select));
        call_mode_btn.setText(getResources().getString(R.string.select));

        if (game_mode.equals("single_mode")) {
            single_mode_btn.setBackgroundResource(R.drawable.btn_green);
            single_mode_btn.setText(getResources().getString(R.string.selected));
        }else if (game_mode.equals("two_player_mode")) {
            two_player_mode_btn.setBackgroundResource(R.drawable.btn_green);
            two_player_mode_btn.setText(getResources().getString(R.string.selected));
        }else if (game_mode.equals("call_mode")) {
            call_mode_btn.setBackgroundResource(R.drawable.btn_green);
            call_mode_btn.setText(getResources().getString(R.string.selected));
        }

        editor.putString("game_mode", game_mode);
        editor.apply();
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