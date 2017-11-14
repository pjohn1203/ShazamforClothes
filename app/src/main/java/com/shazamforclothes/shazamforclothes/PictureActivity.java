package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;



public class PictureActivity extends Activity {

    private File imageFile;
    Button buttonCapture;
    ImageView DisplayImage;
    static final int CAM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Button buttonCapture = (Button) findViewById(R.id.button3);
        DisplayImage = findViewById(R.id.PictureActivityView);

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAM_REQUEST);
                }

            }
        });

    }

    private File getFile(){

        File folder = new File("sdcard/camera_app");

        if(!folder.exists()){
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image");
        return image_file;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAM_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            DisplayImage.setImageBitmap(imageBitmap);
        }
    }

    /*

    public void takepicture(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        Uri temp = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT , temp);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY , 1);
        startActivityForResult(intent, 0);
    }

    */


}
