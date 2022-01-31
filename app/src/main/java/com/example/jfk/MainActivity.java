package com.example.jfk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    int gameX,gameY;
    ConstraintLayout panel;
    ddd img[][] = new ddd[5][5];
    int timer;
    int score;
    Random random = new Random();
    AssetManager assetManager;
    MediaPlayer player;

    public Drawable retImage(Drawable image){
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (gameX/5)/3, (gameY/6)/3, false);
        Drawable d =  new BitmapDrawable(getResources(), bitmapResized);
        return d;
    }
    public void saveTo(Context activity,int score){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences("save", Context.MODE_PRIVATE);
        int maxscore = sharedPreferences.getInt("score",-1);
        if(maxscore == -1 || score > maxscore){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("score",score);
            editor.commit();
        }
    }
    public void UpdateLine(){
        for(int j = 4;j >= 1;j--) {
            for (int i = 4; i >= 0; i--) {
                img[j][i].setBackground(retImage(getResources()
                        .getDrawable(img[j-1][i].getTypeID() == 0 ?
                                R.drawable.v : img[j-1][i].getTypeID() == 1 ? R.drawable.w : R.drawable.d)));
                img[j][i].setTypeID(img[j-1][i].getTypeID());
            }
        }
        for(int j = 0;j < 1;j++) {
            boolean one = false,two = false;
            for (int i = 0; i < 5; i++) {
                if(!one && !two)
                    one = random.nextBoolean();
                if(i == 4 && !one && !two)
                    one = true;
                img[j][i].setTypeID(one && !two ? 0 : 1);
                img[j][i].setBackground(retImage(getResources().getDrawable(one && !two ? R.drawable.v : R.drawable.w)));
                if(one)
                    two = true;
            }
        }
    }
    public void init(){
        timer = 20;
        score = 0;
        for(int i = 0;i < 5;i++){
            boolean one = false,two = false;
            for(int j = 0;j < 5;j++){
                img[i][j] = new ddd(getApplicationContext());
                img[i][j].setX(j * (gameX/5));
                img[i][j].setY(i * (gameY/6));
                img[i][j].setWidth((gameX/5)/2);
                img[i][j].setHeight((gameY/6)/2);
                if(i == 4){
                    img[i][j].setTypeID(1);
                    Bitmap b = ((BitmapDrawable)getResources().getDrawable(R.drawable.w)).getBitmap();
                    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (gameX/5)/3, (gameY/6)/3, false);
                    Drawable d =  new BitmapDrawable(getResources(), bitmapResized);
                    img[i][j].setBackground(d);
                    panel.addView(img[i][j]);
                    continue;
                }
                if(!one && !two)
                    one = random.nextBoolean();
                if(!one && !two && j == 4)
                    one = true;
                img[i][j].setTypeID(one && !two ? 0 : 1);//要不活着代表0，死了代表-1？
                Drawable image = getResources().getDrawable(one && !two ? R.drawable.v : R.drawable.w);
                if(one)
                    two = true;
                Bitmap b = ((BitmapDrawable)image).getBitmap();
                Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (gameX/5)/3, (gameY/6)/3, false);
                Drawable d =  new BitmapDrawable(getResources(), bitmapResized);
                img[i][j].setBackground(d);
                panel.addView(img[i][j]);
                setTitle("乐死了\t"+"时间:"+20+"\t分数:"+0);
                if(player != null){
                    player.reset();
                    player.release();
                    player = null;
                }
                player = new MediaPlayer();
                assetManager = getResources().getAssets();
                try {
                    AssetFileDescriptor fileDescriptor = assetManager.openFd("tap.mp3");
                    player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.prepareAsync();
            }
        }
        handler.sendEmptyMessageDelayed(0,1000);
    }
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panel = findViewById(R.id.panel);
        panel.setOnTouchListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        gameX = dm.widthPixels;
        gameY = dm.heightPixels;
        System.out.println("屏幕大小:"+gameX+":"+gameY);
        init();
    }
    public void GameDead(){
        handler.removeMessages(0);
        Intent intent = new Intent(MainActivity.this,ResultActivity.class);
        intent.putExtra("score",score);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && motionEvent.getX() < (gameX/5)*5 && motionEvent.getY() < (gameY/6)*5){
            int x = Integer.valueOf((int) motionEvent.getX()),y = Integer.valueOf((int) motionEvent.getY());
            System.out.println("TypeID:"+img[y/(gameY/6)][x/(gameX/5)].getTypeID());
            if(y/(gameY/6) == 4 && img[y/(gameY/6)-1][x/(gameX/5)].getTypeID() == 0){
                img[y/(gameY/6)-1][x/(gameX/5)].setBackground(retImage(getResources().getDrawable(R.drawable.d)));
                img[y/(gameY/6)-1][x/(gameX/5)].setTypeID(-1);
                UpdateLine();score++;
                if(player.isPlaying())
                    player.seekTo(0);
                else
                    player.start();
            }else{
                img[y/(gameY/6)][x/(gameX/5)].setBackground(retImage(getResources().getDrawable(R.drawable.r)));
                saveTo(getApplicationContext(),score);
                GameDead();
            }
            System.out.println(x+" "+y+" 格子下标:"+"[x:"+x/(gameX/5)+"y:"+y/(gameY/6)+"]");return true;
        }
        return false;
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            handler.sendEmptyMessageDelayed(0,1000);timer--;
            setTitle("乐死了\t"+"时间:"+timer+"\t分数:"+score);
            if(timer <= 0){
                GameDead();
                saveTo(getApplicationContext(),score);
            }
            System.out.println("线程存活中.");return true;
        }
    });
}