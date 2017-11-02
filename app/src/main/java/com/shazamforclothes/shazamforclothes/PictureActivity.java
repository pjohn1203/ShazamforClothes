package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class PictureActivity extends AppCompatActivity {

    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
    }

    public void takepicture(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        Uri temp = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT , temp);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY , 1);
        startActivityForResult(intent, 0);
    }
}
