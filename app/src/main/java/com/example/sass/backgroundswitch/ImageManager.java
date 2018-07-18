package com.example.sass.backgroundswitch;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private String picsumImageUrl;
    private Handler mHandler;
    private ConstraintLayout constraintLayout;
    private ConfigManager configManager;
    private MutableBoolean isImageUpdated = new MutableBoolean(true);
    private DownloadFrom downloadFrom = DownloadFrom.picsum;

    public enum DownloadFrom{
        picsum, config
    }

    ImageManager(final Context context, ScreenProperty screenProperty, ConfigManager configManager) {
        picsumImageUrl = screenProperty.getPicsumImageUrl();
        this.configManager = configManager;
        constraintLayout = ((AppCompatActivity) context).findViewById(R.id.background_main);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Drawable image = (Drawable) inputMessage.obj;
                isImageUpdated.setValue(true);

                Log.d("ImageMagerImageUpdedHS", String.valueOf(isImageUpdated.hashCode()));

                if(inputMessage.what == RESULT_OK && image != null){
                    constraintLayout.setBackground(image);
                    setDebugInfo(context, "Internet");

                    //Log.d("ImageManagerThreadID", String.valueOf(Thread.currentThread().getId()));
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
                    urlConnection.setConnectTimeout(1000);
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

    @SuppressLint("SetTextI18n")
    private void setDebugInfo(Context context, String imageType){
        ((TextView)((AppCompatActivity) context).findViewById(R.id.timeoutTextView))
                .setText("Timeout: " + String.valueOf(configManager.getTimeout()/1000) + "sec");

        ((TextView)((AppCompatActivity) context).findViewById(R.id.imageTypeTextView))
                .setText(imageType);
    }

    public void updateImage(){
        isImageUpdated.setValue(false);

        if(downloadFrom == DownloadFrom.config){
            if(!configManager.getNextImageUrl().isEmpty())
                downloadImage(configManager.getNextImageUrl());
            else{
                mHandler.sendEmptyMessage(RESULT_ERROR);
            }
        }
        else
            downloadImage(picsumImageUrl);
    }

    public void resetIsImageUpdated() {
        this.isImageUpdated.setValue(true);
    }

    public boolean isImageUpdated() {
        return isImageUpdated.isValue();
    }

    public void setDownloadFrom(DownloadFrom downloadFrom) {
        this.downloadFrom = downloadFrom;
    }
}
