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
    MutableBoolean isImageUpdated = new MutableBoolean(false);

    Thread onCreateThread, onResumeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configManager = new ConfigManager();
        imageManager = new ImageManager(this, configManager);

        imageManager.updateImage(isImageUpdated);

        onCreateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                updateImageTimeout();
            }
        });
        onCreateThread.start();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(!threadWork){
            threadWork = true;

            onResumeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateImageTimeout();
                }
            });
            onResumeThread.start();
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

            //if(!isImageUpdated.isValue())
            //    continue;

            isImageUpdated.setValue(false);
            try {
                configManager.updateConfig();
                imageManager.updateImage(isImageUpdated);

                Thread.sleep(configManager.getTimeout());

            } catch (InterruptedException e){
                Log.e("InterruptedException", e.getMessage());
            }
        }
    }
}
