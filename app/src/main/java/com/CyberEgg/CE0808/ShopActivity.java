package com.CyberEgg.CE0808;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class ShopActivity extends AppCompatActivity {
    private ImageView back;
    private TextView coin;
    private LinearLayout layout_horizontal;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private Random random;
    private String lang;
    private int available_coin;
    private FileAccess fileAccess;
    private LayoutInflater inflate;
    private int selected_player;
    private int[] players = new int[]{
            R.drawable.player_0,
            R.drawable.player_1,
            R.drawable.player_2,
            R.drawable.player_3,
            R.drawable.custom,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("CyberEg9e3CE0", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");
        available_coin = sharedPreferences.getInt("available_coin", 0);
        selected_player = sharedPreferences.getInt("selected_player", 0);
        available_coin = 500;

        setContentView(R.layout.activity_shop);

        fileAccess = new FileAccess(this);

        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout_horizontal = findViewById(R.id.layout_horizontal);
        back = findViewById(R.id.back);
        coin = findViewById(R.id.coin);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player.button(soundMute);

                finish();
            }
        });

        load_shop();
    }

    private void load_shop() {
        int min_coin = 100;
        ArrayList<Boolean> purchased = new ArrayList<>();
        purchased.add(true);
        for (int i = 1; i < 4; i++)
            purchased.add(sharedPreferences.getBoolean("purchased_" + i, false));

        layout_horizontal.removeAllViews();
        coin.setText(available_coin + "");

        for (int i = 0; i < 5; i++) {
            View card = inflate.inflate(R.layout.card, null);
            LinearLayout player_layout = card.findViewById(R.id.player_layout);
            ImageView player = card.findViewById(R.id.player);
            ImageView coin = card.findViewById(R.id.coin);
            TextView coin_required = card.findViewById(R.id.coin_required);
            Button buy_select = card.findViewById(R.id.buy_select);

            if (i < 4) {
                player.setImageResource(players[i]);
                coin.setVisibility(VISIBLE);
                coin_required.setVisibility(VISIBLE);
                if (selected_player == i) {
                    buy_select.setText(getResources().getString(R.string.selected));
                    buy_select.setBackgroundResource(R.drawable.btn_green);
                    coin.setVisibility(INVISIBLE);
                    coin_required.setVisibility(INVISIBLE);
                } else if (purchased.get(i)) {
                    buy_select.setText(getResources().getString(R.string.select));
                    buy_select.setBackgroundResource(R.drawable.btn_gray);
                    coin.setVisibility(INVISIBLE);
                    coin_required.setVisibility(INVISIBLE);

                    int finalI = i;
                    buy_select.setOnClickListener(view -> {
                        Player.button(soundMute);
                        selected_player = finalI;
                        editor.putInt("selected_player", selected_player);
                        editor.apply();

                        intent = new Intent(ShopActivity.this, ShopActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    });
                } else {
                    int cc = min_coin + min_coin * i;
                    buy_select.setText(getResources().getString(R.string.buy));
                    buy_select.setBackgroundResource(R.drawable.btn_gray);
                    coin_required.setText(cc + "");
                    if (cc <= available_coin) {
                        int finalI = i;
                        buy_select.setOnClickListener(view -> {
                            Player.button(soundMute);
                            available_coin -= cc;

                            selected_player = finalI;
                            editor.putInt("selected_player", selected_player);
                            editor.putInt("available_coin", available_coin);
                            editor.putBoolean("purchased_" + finalI, true);
                            editor.apply();

                            intent = new Intent(ShopActivity.this, ShopActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                        });
                    } else {
                        buy_select.setAlpha(0.5F);
                    }
                }
            } else {
                fileAccess.registerLaunchers(this, player);
                fileAccess.fileName = Player.CUSTOM_BG;

                Bitmap bitmap = fileAccess.loadImageFromInternalStorage(Player.CUSTOM_BG);
                if (bitmap == null)
                    player.setImageResource(players[i]);
                else {
                    player.setImageBitmap(bitmap);
                    player_layout.setBackgroundResource(R.color.trans);
                }

                coin.setVisibility(GONE);
                coin_required.setText(getResources().getString(R.string.custom_background));
                buy_select.setText(getResources().getString(R.string.set));
                buy_select.setBackgroundResource(R.drawable.btn_gray);

                buy_select.setOnClickListener(view -> {
                    Player.button(soundMute);
                    fileAccess.showImagePickerDialog(Player.CUSTOM_BG);
                });
            }

            layout_horizontal.addView(card);
        }
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
        return;
    }
}