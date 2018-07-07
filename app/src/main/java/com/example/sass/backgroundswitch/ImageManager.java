package com.example.sass.backgroundswitch;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageManager {
    private final int RESULT_OK = 1;
    private final int RESULT_ERROR = 0;

    private String urlImage = "https://picsum.photos/500/800?random";
    private String urlLandImage = "https://picsum.photos/800/500?random";
    private String urlLargeImage = "https://picsum.photos/1000/1500?random";
    private String urlLargeLandImage = "https://picsum.photos/1500/1000?random";
    private String urlImageChecked;

    private Handler mHandler;
    private ConstraintLayout constraintLayout;
    private ConfigManager configManager;

    ImageManager(final Context context, final ConfigManager configManager) {

        this.configManager = configManager;
        constraintLayout = (ConstraintLayout) ((AppCompatActivity) context).findViewById(R.id.background_main);

        int screenLayout = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int screenOrientation = context.getResources().getConfiguration().orientation;

        if(screenOrientation == Configuration.ORIENTATION_PORTRAIT){

            switch (screenLayout) {
                case Configuration.SCREENLAYOUT_SIZE_NORMAL: urlImageChecked = urlImage; break;
                case Configuration.SCREENLAYOUT_SIZE_LARGE: urlImageChecked = urlLargeImage; break;
            }
        }
        else if(screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            switch (screenLayout) {
                case Configuration.SCREENLAYOUT_SIZE_NORMAL: urlImageChecked = urlLandImage; break;
                case Configuration.SCREENLAYOUT_SIZE_LARGE: urlImageChecked = urlLargeLandImage; break;
            }
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Drawable image = (Drawable) inputMessage.obj;
                if(inputMessage.what == RESULT_OK && image != null){
                    constraintLayout.setBackground(image);
                    setDebugInfo(context, "Internet");

                    Log.d("ImageManager", "OK");
                }
                else{
                    constraintLayout.setBackgroundResource(R.drawable.default_background);
                    setDebugInfo(context, "Default");

                    Log.e("ImageManager", "ERROR");
                }
            }
        };
    }

    private void downloadImage(final String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(imageUrl);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(1200);
                    urlConnection.connect();

                    Drawable image = Drawable.createFromStream(urlConnection.getInputStream(), "tempImage.jpg");

                    Message message = new Message();
                    message.what = RESULT_OK;
                    message.obj = image;

                    mHandler.sendMessage(message);

                } catch (IOException e){
                    Log.d("IOExceptionImageManager", e.getMessage());
                    mHandler.sendEmptyMessage(RESULT_ERROR);
                }
            }
        }).start();
    }

    private void setDebugInfo(Context context, String imageType){
        ((TextView)((AppCompatActivity) context).findViewById(R.id.timeoutTextView))
                .setText("Timeout: " + String.valueOf(configManager.getTimeout()/1000) + "sec");

        ((TextView)((AppCompatActivity) context).findViewById(R.id.imageTypeTextView))
                .setText(imageType);
    }

    public void updateImage(){
        downloadImage(urlImageChecked);
    }
}
