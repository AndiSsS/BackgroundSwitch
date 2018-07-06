package com.example.sass.backgroundswitch;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

class ConfigManager {
    final private int defaultTimeout = 2;
    final private Object object = new Object();

    private String urlConfig = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1BKo57JlpffywKz78VWYVKZdsCHeOHyyx";
    private int timeout = defaultTimeout;

    private Handler mHandler;

    private final int RESULT_OK = 1;
    private final int RESULT_ERROR = 0;


    ConfigManager(){
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Log.d("handleMessage", String.valueOf(inputMessage.what));

                if(inputMessage.what == RESULT_OK){

                }
                else {
                    setTimeout(defaultTimeout); }
            }
        };
    }

    public void updateConfig(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder configStr = new StringBuilder("");
                BufferedReader bufferedReader = null;

                try{
                    URL url = new URL(urlConfig);
                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while((str = bufferedReader.readLine()) != null)
                        configStr.append(str);

                    int newTimeout = new JSONObject(configStr.toString()).getInt("timeout");

                    mHandler.sendMessage()
                }
                catch (IOException e){
                    Log.d("IOException", e.getMessage());
                    mHandler.sendEmptyMessage(RESULT_ERROR);
                }
                catch (JSONException e){
                    Log.e("JSONException", e.getMessage());
                    mHandler.sendEmptyMessage(RESULT_ERROR);
                }
                finally {
                    try {
                        if (bufferedReader != null){
                            bufferedReader.close(); }
                    } catch (IOException e){
                        Log.e("IOException", e.getMessage());
                    }

                }
            }
        }).start();
    }

    private void setTimeout(int value){
        synchronized (object){
            timeout = value;
        }
    }

    public int getTimeout(){
        synchronized (object) {
            return timeout;
        }
    }
}
