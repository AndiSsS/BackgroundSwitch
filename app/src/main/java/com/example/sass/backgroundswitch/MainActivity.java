package com.example.sass.backgroundswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ConfigManager configManager;
    ImageManager imageManager;

    boolean threadWork = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void onClick(View view){
        startActivity(new Intent(this, SecondActivity.class));
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
