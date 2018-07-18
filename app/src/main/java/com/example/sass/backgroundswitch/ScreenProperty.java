package com.example.sass.backgroundswitch;

import android.content.Context;
import android.content.res.Configuration;

public class ScreenProperty {
    private String picsumImageUrl;
    private String configScreenProperty;

    ScreenProperty(Context context){
        String urlNormalImage = "https://picsum.photos/500/800?random";
        String urlNormalLandImage = "https://picsum.photos/800/500?random";
        String urlLargeImage = "https://picsum.photos/1000/1500?random";
        String urlLargeLandImage = "https://picsum.photos/1500/1000?random";

        int screenLayout = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int screenOrientation = context.getResources().getConfiguration().orientation;

        if(screenOrientation == Configuration.ORIENTATION_PORTRAIT){
            switch (screenLayout) {
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    picsumImageUrl = urlNormalImage;
                    configScreenProperty = "normal";
                    break;
                default:
                    picsumImageUrl = urlLargeImage;
                    configScreenProperty = "large";
                    break;
            }
        }
        else if(screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            switch (screenLayout) {
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    picsumImageUrl = urlNormalLandImage;
                    configScreenProperty = "normalLand";
                    break;
                default:
                    picsumImageUrl = urlLargeLandImage;
                    configScreenProperty = "largeLand";
                    break;
            }
        }
    }

    public String getPicsumImageUrl() {
        return picsumImageUrl;
    }

    public String getConfigScreenProperty() {
        return configScreenProperty;
    }
}
