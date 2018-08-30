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
import java.util.concurrent.atomic.AtomicBoolean;

class ImageManager {
    private final int RESULT_OK = 1;
    private final int RESULT_ERROR = 0;
    private String picsumImagesUrl;
    private Handler mHandler;
    private ConstraintLayout constraintLayout;
    private ConfigManager configManager;
    private AtomicBoolean isImageUpdated = new AtomicBoolean(true);
    private DownloadFrom downloadFrom;
    private Context context;

    public enum DownloadFrom{
        picsum, config
    }

    ImageManager(final Context context, ScreenProperty screenProperty, ConfigManager configManager, DownloadFrom downloadFrom) {
        this.context = context;
        picsumImagesUrl = screenProperty.getPicsumImageUrl();
        this.configManager = configManager;
        constraintLayout = ((AppCompatActivity) context).findViewById(R.id.background_main);
        this.downloadFrom = downloadFrom;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Drawable image = (Drawable) inputMessage.obj;
                isImageUpdated.set(true);

                if(inputMessage.what == RESULT_OK && image != null){
                    constraintLayout.setBackground(image);
                    setDebugInfo(context, "Internet");
                }
                else{
                    changeToDefaultImage();
                    Log.e("ImageManager", "ERROR");
                }
            }
        };
    }
    ImageManager(final Context context, ScreenProperty screenProperty, ConfigManager configManager) {
        this(context, screenProperty, configManager, DownloadFrom.picsum);
    }

    @SuppressLint("SetTextI18n")
    private void setDebugInfo(Context context, String imageType){
        ((TextView)((AppCompatActivity) context).findViewById(R.id.timeoutTextView))
                .setText("Timeout: " + String.valueOf(configManager.getTimeout()/1000) + "sec");

        ((TextView)((AppCompatActivity) context).findViewById(R.id.imageTypeTextView))
                .setText(imageType);
    }

    public void updateImage() {
        isImageUpdated.set(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (!configManager.isConfigUpdated()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String imageUrl = picsumImagesUrl;
                    if(downloadFrom == DownloadFrom.config)
                        imageUrl = configManager.getNextImageUrl();

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
                    Log.e("IOExceptionImageManager", e.getMessage());
                    mHandler.sendEmptyMessage(RESULT_ERROR);
                }
            }
        }).start();
    }
    public void resetIsImageUpdated() {
        this.isImageUpdated.set(true);
    }

    public boolean isImageUpdated() {
        return isImageUpdated.get();
    }

    public void setDownloadFrom(DownloadFrom downloadFrom) {
        this.downloadFrom = downloadFrom;
    }

    public DownloadFrom getDownloadFrom() {
        return downloadFrom;
    }

    public void changeToDefaultImage(){
        constraintLayout.setBackgroundResource(R.drawable.default_background);
        setDebugInfo(context, "Default");
    }
}
