package com.fat_a_fot.fat_shopkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

public class SplashScreen extends Activity {

    private int SPLASH_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (Common.getSavedUserData(SplashScreen.this, "email").equalsIgnoreCase("")) {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
        timer.start();
    }

}
