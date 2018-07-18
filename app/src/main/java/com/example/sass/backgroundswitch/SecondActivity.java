package com.example.sass.backgroundswitch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    ImageManager imageManager;
    ConfigManager configManager;
    ScreenProperty screenProperty;

    boolean threadWork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        screenProperty = new ScreenProperty(this);
        configManager = new ConfigManager(screenProperty);
        imageManager = new ImageManager(this, screenProperty, configManager);

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
        while(threadWork){
            if(imageManager.isImageUpdated())
                continue;

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
