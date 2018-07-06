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

    boolean threadWork = true;

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
                updateImageTimeout();
            }
        }).start();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(!threadWork){
            threadWork = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateImageTimeout();
                }
            }).start();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        threadWork = false;
    }

    private void updateImageTimeout(){
        while(true){
            if(!threadWork)
                break;

            try {
                Thread.sleep(configManager.getTimeout());
                imageManager.updateImage();
                configManager.updateConfig();
            } catch (InterruptedException e){
                Log.e("InterruptedException", e.getMessage());
            }
        }
    }
}
