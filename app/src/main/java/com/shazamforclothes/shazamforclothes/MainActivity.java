package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;

public class MainActivity extends AppCompatActivity {

    Vision.Builder visionBuilder = new Vision.Builder(
            new NetHttpTransport(),
            new AndroidJsonFactory(),
            null);

    Vision vision = visionBuilder.build();

    public void setVisionBuilder(Vision.Builder visionBuilder) {
        this.visionBuilder = visionBuilder;
    }

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
