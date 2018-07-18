package com.example.sass.backgroundswitch;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ConfigManager configManager;
    ImageManager imageManager;
    ScreenProperty screenProperty;

    Thread imageUpdateThread;
    long lastThreadId;
    Resources resources;

    private void updateImageTimeout(){
        while(Thread.currentThread().getId() == lastThreadId){
            Log.d("lastThreadIdThreadID", String.valueOf(lastThreadId));

            if(imageManager.isImageUpdated())
                continue;

            try {
                configManager.updateConfig();
                Thread.sleep(configManager.getTimeout());

                if(Thread.currentThread().getId() != lastThreadId){
                    imageManager.resetIsImageUpdated();
                    break;
                }

                imageManager.updateImage();
            } catch (InterruptedException e){
                Log.e("InterruptedException", e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources = getResources();
        screenProperty = new ScreenProperty(this);
        configManager = new ConfigManager(screenProperty);
        imageManager = new ImageManager(this, screenProperty, configManager);

        imageManager.updateImage();

        Spinner spinner = findViewById(R.id.spinnerGetImagesFrom);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.getImagesFrom, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(parent.getItemAtPosition(pos) == resources.getString(R.string.images_from_config))
            imageManager.setDownloadFrom(ImageManager.DownloadFrom.config);
        else if(parent.getItemAtPosition(pos) == resources.getString(R.string.images_from_picsum))
            imageManager.setDownloadFrom(ImageManager.DownloadFrom.picsum);
    }

    public void onNothingSelected(AdapterView<?> parent) { }
}
