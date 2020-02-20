package com.johnmelodyme.starlabsproductswarranty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class splash extends AppCompatActivity {
    private static final String TAG = "Starlabs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, splash.class.getName());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toMain;
                toMain = new Intent(splash.this, MainActivity.class);
                startActivity(toMain);
                finish();
            }
        }, 3000);
    }
}
