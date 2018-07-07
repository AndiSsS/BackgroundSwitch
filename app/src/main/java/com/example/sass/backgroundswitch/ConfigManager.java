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
    final private String urlConfig = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1BKo57JlpffywKz78VWYVKZdsCHeOHyyx";

    private static int defaultTimeout = 5;
    private int timeout = defaultTimeout;
    private Handler mHandler;

    ConfigManager(){
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                //Log.d("ConfigManagerNewTimeout", String.valueOf(inputMessage.what));

                timeout = inputMessage.what;
                defaultTimeout = timeout;
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

                    mHandler.sendEmptyMessage(newTimeout);
                }
                catch (IOException e){
                    Log.d("IOException", e.getMessage());
                }
                catch (JSONException e){
                    Log.e("JSONException", e.getMessage());
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

    public int getTimeout(){
        return timeout * 1000;
    }
}
