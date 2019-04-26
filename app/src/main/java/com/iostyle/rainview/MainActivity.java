package com.iostyle.rainview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iostyle.library.RainView;

public class MainActivity extends AppCompatActivity {

    private RainView rainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rainView = findViewById(R.id.rainView);
//        rainView.setTrans(true, 0.3f, 0.2f);
        rainView.setRandomSize(true,0.8f,1.2f);
        rainView.play();
//        rainView.setDebug(true);
    }
}
