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

import java.io.IOException;
import java.net.URL;

class ImageManager {
    private final int RESULT_OK = 1;
    private final int RESULT_ERROR = 0;

    private Drawable image;

    private String urlImage = "https://picsum.photos/500/800?random";
    private String urlLandImage = "https://picsum.photos/800/500?random";
    private String urlLargeImage = "https://picsum.photos/1000/1500?random";
    private String urlLargeLandImage = "https://picsum.photos/1500/1000?random";
    private String urlImageChecked;

    private Handler mHandler;

    private ConstraintLayout constraintLayout;

    ImageManager(final Context context) {
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
                image = (Drawable) inputMessage.obj;
                if(image != null)
                    constraintLayout.setBackground(image);
                else
                    constraintLayout.setBackgroundResource(R.drawable.default_background);
            }
        };
    }

    private void downloadImage(final String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(imageUrl);

                    image = Drawable.createFromStream(url.openStream(), "tempImage.jpg");

                    Message message = new Message();
                    message.obj = image;

                    mHandler.sendMessage(message);

                    Log.d("downloadImage", "DOWNLOADED");
                } catch (IOException e){
                    Log.d("IOException", e.getMessage());
                    mHandler.sendEmptyMessage(RESULT_ERROR);
                }
            }
        }).start();
    }

    public void updateImage(){
        downloadImage(urlImageChecked);
    }
}
