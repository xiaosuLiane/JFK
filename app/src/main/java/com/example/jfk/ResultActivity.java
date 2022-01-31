package com.example.jfk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    TextView textView;
    TextView getTextView;
    Button button;
    int score;
    int maxscore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        textView = findViewById(R.id.saveResult);
        getTextView = findViewById(R.id.resultTitle);
        button = findViewById(R.id.resetGame);
        score = getIntent().getIntExtra("score",-1);
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("save", Context.MODE_PRIVATE);
        maxscore = sharedPreferences.getInt("score",-1);
        textView.setText("得分 "+score+"\n最佳 "+(maxscore == -1 ? "?" : maxscore));
        getTextView.setText("你刺杀了"+score+"位肯尼迪！\n难道你就是枪手转世吗？");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this,MainActivity.class));
                finish();
            }
        });
    }
}