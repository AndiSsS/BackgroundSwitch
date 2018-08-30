package com.example.sass.backgroundswitch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

class ConfigManager {
    final private String urlConfig = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1BKo57JlpffywKz78VWYVKZdsCHeOHyyx";
    private static int defaultTimeout = 5;
    private int timeout = defaultTimeout;
    private int nextImageUrlIndex = 0;
    private String nextImageUrl = "";
    private String screenProperty;
    private Handler mHandler;
    private AtomicBoolean isConfigUpdated = new AtomicBoolean(true);

    ConfigManager(ScreenProperty screenProperty){
        this.screenProperty = screenProperty.getConfigScreenProperty();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                isConfigUpdated.set(true);

                Bundle data = inputMessage.getData();
                timeout = data.getInt("timeout");
                nextImageUrl = data.getString("nextImageUrl");
                defaultTimeout = timeout;

                Log.e("CONFIG new timeout", String.valueOf(timeout));
            }
        };
    }

    public void updateConfig(){
        isConfigUpdated.set(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder configStr = new StringBuilder("");
                BufferedReader bufferedReader = null;
                Bundle data = new Bundle();
                Message message = new Message();
                int newTimeout;
                String imageUrl;

                try{
                    URL url = new URL(urlConfig);

                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while((str = bufferedReader.readLine()) != null)
                        configStr.append(str);

                    JSONObject mainJsonObject = new JSONObject(configStr.toString());
                    newTimeout = mainJsonObject.getInt("timeout");
                    JSONArray imagesJsonArray = mainJsonObject.getJSONObject("images").getJSONArray(screenProperty);
                    nextImageUrlIndex = nextImageUrlIndex + 1 <= imagesJsonArray.length() - 1 ? nextImageUrlIndex + 1 : 0;
                    imageUrl = imagesJsonArray.getJSONObject(nextImageUrlIndex).getString("url");

                    data.putInt("timeout", newTimeout);
                    data.putString("nextImageUrl", imageUrl);
                    message.setData(data);
                    mHandler.sendMessage(message);
                }
                catch (IOException e){
                    Log.d("IOException", e.getMessage());
                }
                catch (JSONException e){
                    Log.e("JSONException", e.getMessage());
                }
                catch (Exception e){
                    Log.e("EXCEPTION", e.getMessage());
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

    public String getNextImageUrl() {
        String temp = nextImageUrl;
        nextImageUrl = "";
        return temp;
    }

    public boolean isConfigUpdated() {
        return isConfigUpdated.get();
    }
}
