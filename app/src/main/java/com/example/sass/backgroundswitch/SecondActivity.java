package com.example.sass.backgroundswitch;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    Handler handler;
    ImageManager imageManager;
    ConfigManager configManager;
    MutableBoolean isImageUpdated = new MutableBoolean(false);

    boolean threadWork = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        configManager = new ConfigManager();
        imageManager = new ImageManager(this, configManager);

        imageManager.updateImage(isImageUpdated);

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

            //if(!isImageUpdated.isValue())
            //    continue;

            isImageUpdated.setValue(false);
            try {
                configManager.updateConfig();
                Thread.sleep(configManager.getTimeout());
                imageManager.updateImage(isImageUpdated);

            } catch (InterruptedException e){
                Log.e("InterruptedException", e.getMessage());
            }
        }
    }
}
