package com.example.sass.backgroundswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ConfigManager configManager;
    ImageManager imageManager;

    MutableBoolean isImageUpdated = new MutableBoolean(false);
    Thread imageUpdateThread;
    long lastThreadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configManager = new ConfigManager();
        imageManager = new ImageManager(this, configManager);

        imageManager.updateImage(isImageUpdated);

//        imageUpdateThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                updateImageTimeout();
//            }
//        });
//        lastThreadId = imageUpdateThread.getId();
//        imageUpdateThread.start();
    }

    @Override
    protected void onResume(){
        super.onResume();

        imageUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                updateImageTimeout();
            }
        });
        lastThreadId = imageUpdateThread.getId();
        Log.d("NEWLASTRTHREADID", String.valueOf(lastThreadId));
        imageUpdateThread.start();
    }

    @Override
    protected void onStop(){
        super.onStop();

        lastThreadId = 0;
    }

    public void onClick(View view){
        startActivity(new Intent(this, SecondActivity.class));
    }

    private void updateImageTimeout(){
        while(Thread.currentThread().getId() == lastThreadId){
            Log.d("lastThreadIdThreadID", String.valueOf(lastThreadId));

            isImageUpdated.setValue(false);
            try {
                configManager.updateConfig();
                Thread.sleep(configManager.getTimeout());

                if(imageUpdateThread.getId() != lastThreadId){
                    isImageUpdated.setValue(true);
                    break;
                }

                imageManager.updateImage(isImageUpdated);
            } catch (InterruptedException e){
                Log.e("InterruptedException", e.getMessage());
            }
        }
    }
}
