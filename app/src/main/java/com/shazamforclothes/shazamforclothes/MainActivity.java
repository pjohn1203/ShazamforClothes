package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Take_Picture(View view){
        Intent TakePictureActivity = new Intent(this, PictureActivity.class);
        startActivity(TakePictureActivity);
    }

    public void Import_Picture(View view){
        Intent ImportPictureActivity = new Intent(this, ImportActivity.class);
        startActivity(ImportPictureActivity);
    }
}
