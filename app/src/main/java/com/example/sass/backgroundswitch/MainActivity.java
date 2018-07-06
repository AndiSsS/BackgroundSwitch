package com.example.sass.backgroundswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageManager = new ImageManager(this);
        imageManager.updateImage();

    }

//    @Override
//    protected void onResume(){
//        super.onResume();
//
//    }

    public void onClick(View view){
        startActivity(new Intent(this, SecondActivity.class));
    }
}
