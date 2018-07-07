package com.example.sass.backgroundswitch;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    Handler handler;
    ImageManager imageManager;
    ConfigManager configManager;

    boolean threadWork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        configManager = new ConfigManager();
        imageManager = new ImageManager(this, configManager);

        imageManager.updateImage();
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
                configManager.updateConfig();
                Thread.sleep(configManager.getTimeout());
                imageManager.updateImage();
            } catch (InterruptedException e){
                Log.e("InterruptedException", e.getMessage());
            }
        }
    }
}
