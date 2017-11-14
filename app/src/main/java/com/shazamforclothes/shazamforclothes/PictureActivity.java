package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Pair;
import com.google.api.services.vision.v1.Vision;

public class PictureActivity extends Activity {
    //picture should be saved to /storage/sdcard/Pictures/CameraSample/"imagename"

    Button buttonCapture;
    ImageView DisplayImage;
    static final int CAM_REQUEST = 1;
    JSONArray features = new JSONArray();
    JSONObject feature = new JSONObject();
    JSONArray requests = new JSONArray();
    JSONObject request = new JSONObject();
    JSONObject postData = new JSONObject();
    JSONArray labels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        TextView textView = findViewById(R.id.ResultsText);
        textView.setMovementMethod(new ScrollingMovementMethod());

        buttonCapture = findViewById(R.id.button3);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAM_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            DisplayImage.setImageBitmap(imageBitmap);
            try {
                ProcessImage(imageBitmap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void ProcessImage(Bitmap image) throws JSONException {
        //First, convert image to bitmap and compress to allow vision API to read
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, byteStream);
        String base64Data = Base64.encodeToString(byteStream.toByteArray(), Base64.URL_SAFE);

        //Read API ID and URL
        String requestURL = "https://vision.googleapis.com/v1/images:annotate?key=" + getResources().getString(R.string.mykey);

        //Implement JSON arrays to read image
        //Convert to string to allow system to read tags
        feature.put("type" , "LABEL_DETECTION");
        features.put(feature);
        JSONObject imageContent = new JSONObject();
        imageContent.put("content", base64Data);
        request.put("image", imageContent);
        request.put("features", features);
        requests.put(request);
        postData.put("requests", requests);
        String body = postData.toString();

        //make the actual HTTP Request
        //this will invoke the vision API making the tags and writing them to a string
        Fuel.post(requestURL)
                .header(
                        new Pair<String, Object>("content-length", body.length()),
                        new Pair<String, Object>("content-type", "application/json")
                )
                .body(body.getBytes())
                .responseString(new Handler<String>() {
                    @Override
                    public void success(@NotNull Request request,
                                        @NotNull Response response,
                                        String data) {
                        try {
                            labels = new JSONObject(data)
                                    .getJSONArray("responses")
                                    .getJSONObject(0)
                                    .getJSONArray("labelAnnotations");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String results = "";

                        for(int i=0;i<labels.length();i++) {
                            try {
                                results = results +
                                        labels.getJSONObject(i).getString("description") +
                                        "\n";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ((TextView)findViewById(R.id.ResultsText)).setText(results);
                    }


                    //required failure method
                    @Override
                    public void failure(@NotNull Request request,
                                        @NotNull Response response,
                                        @NotNull FuelError fuelError) {}
                });

    }
}
