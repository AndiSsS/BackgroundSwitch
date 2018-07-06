package com.example.sass.backgroundswitch;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    Handler handler;
    ImageManager imageManager;
    ConfigManager configManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        configManager = new ConfigManager();

        imageManager = new ImageManager(this, configManager);
        imageManager.updateImage();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(configManager.getTimeout());
                        imageManager.updateImage();
                        configManager.updateConfig();
                    } catch (InterruptedException e){
                        Log.e("InterruptedException", e.getMessage());
                    }
                }
            }
        }).start();
    }
}
