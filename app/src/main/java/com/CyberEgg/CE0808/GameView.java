package com.CyberEgg.CE0808;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View{
    private SharedPreferences sharedPreferences;
    private int screenX, screenY;
    private Resources resources;
    private Random random;
    boolean isPlaying = true;

    int score;
    private int xSpeed, ySpeed;
    private Context context;

    int LEFT_TOP = 0, LEFT_BOTTOM = 1, RIGHT_TOP = 2, RIGHT_BOTTOM = 3;
    Bitmap chicken_left, chicken_right, ground_left, ground_right;
    int g_w, g_h, g_lx, g_rx, g_ty, g_by;
    int c_w, c_h, c_padding;
    ArrayList<Bitmap> grounds = new ArrayList<>();
    ArrayList<Bitmap> chickens = new ArrayList<>();
    ArrayList<ArrayList<Integer>> egg_data = new ArrayList<>();
    ArrayList<ArrayList<Integer>> c_data = new ArrayList<>();

    public GameView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();

        ground_left = BitmapFactory.decodeResource(res, R.drawable.ground_left);
        ground_right = BitmapFactory.decodeResource(res, R.drawable.ground_right);
        chicken_left = BitmapFactory.decodeResource(res, R.drawable.chicken_left);
        chicken_right = BitmapFactory.decodeResource(res, R.drawable.chicken_right);
        int w = ground_left.getWidth();
        int h = ground_right.getHeight();
        g_w = screenX / 3;
        g_h = g_w * h / w;
        g_lx = 0;
        g_rx = screenX - g_w;
        g_ty = screenY / 2 - g_h;


        w = chicken_left.getWidth();
        h = chicken_left.getHeight();
        c_w = g_h;
        c_h = c_w * h / w;

        g_by = g_ty + c_h * 3 / 2;
        c_padding = c_w / 10;

        ArrayList<Integer> data = new ArrayList<>();
        data.add(g_lx + c_padding);
        data.add(g_ty - c_h);
        c_data.add(data);

        data = new ArrayList<>();
        data.add(g_lx + c_padding);
        data.add(g_by - c_h);
        c_data.add(data);

        data = new ArrayList<>();
        data.add(screenX - c_padding - c_w);
        data.add(g_ty - c_h);
        c_data.add(data);

        data = new ArrayList<>();
        data.add(screenX - c_padding - c_w);
        data.add(g_by - c_h);
        c_data.add(data);

        grounds.add(Bitmap.createScaledBitmap(ground_left, g_w, g_h, false));
        grounds.add(Bitmap.createScaledBitmap(ground_right, g_w, g_h, false));
        chickens.add(Bitmap.createScaledBitmap(chicken_left, c_w, c_h, false));
        chickens.add(Bitmap.createScaledBitmap(chicken_right, c_w, c_h, false));

        setSpeed();
        add_egg(LEFT_TOP);
        add_egg(LEFT_BOTTOM);
        add_egg(RIGHT_TOP);
        add_egg(LEFT_BOTTOM);
    }

    private void add_egg(int position) {

    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        canvas.drawBitmap(grounds.get(0), g_lx, g_ty, paint);
        canvas.drawBitmap(grounds.get(0), g_lx, g_by, paint);
        canvas.drawBitmap(grounds.get(1), g_rx, g_ty, paint);
        canvas.drawBitmap(grounds.get(1), g_rx, g_by, paint);

        canvas.drawBitmap(chickens.get(0), c_data.get(LEFT_TOP).get(0), c_data.get(LEFT_TOP).get(1), paint);
        canvas.drawBitmap(chickens.get(0), c_data.get(LEFT_BOTTOM).get(0), c_data.get(LEFT_BOTTOM).get(1), paint);
        canvas.drawBitmap(chickens.get(1), c_data.get(RIGHT_TOP).get(0), c_data.get(RIGHT_TOP).get(1), paint);
        canvas.drawBitmap(chickens.get(1), c_data.get(RIGHT_BOTTOM).get(0), c_data.get(RIGHT_BOTTOM).get(1), paint);
    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        invalidate();
    }
}