package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class Main3Activity extends AppCompatActivity {
    private TextView tv1;
    private LottieAnimationView pass, fail, succ, loose;
    private RelativeLayout my;
    private ImageButton back;
    private Button check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tv1 = (TextView) findViewById(R.id.result);
        pass = (LottieAnimationView) findViewById(R.id.my_progress);
        fail = (LottieAnimationView) findViewById(R.id.my_progress2);
        my = (RelativeLayout) findViewById(R.id.my);
        succ = (LottieAnimationView) findViewById(R.id.pass);
        loose = (LottieAnimationView) findViewById(R.id.fail);
        back = (ImageButton)findViewById(R.id.back);
        check = (Button)findViewById(R.id.check);

        Intent myIntent = getIntent();
        int intValue = myIntent.getIntExtra("intVariableName", 0);
        int percent = intValue / 40;
        if (intValue < 4) {
            pass.setVisibility(View.INVISIBLE);
            my.setBackgroundResource(R.color.red);
            succ.setVisibility(View.INVISIBLE);

        } else {
            fail.setVisibility(View.INVISIBLE);
            my.setBackgroundResource(R.color.main_page);
            loose.setVisibility(View.INVISIBLE);
        }
        int val = percent * 100;
        tv1.setText(String.valueOf((100 * intValue) / 40) + "%");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main3Activity.this, Main26Activity.class);
                startActivity(intent);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main3Activity.this, Main37Activity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Main3Activity.this, Main26Activity.class);
        startActivity(intent);
    }
}
