package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private Button buttonCapture;
    private ImageView DisplayImage;
    static final int CAM_REQUEST = 1;
    private JSONArray features = new JSONArray();
    private JSONObject feature = new JSONObject();
    private JSONArray requests = new JSONArray();
    private JSONObject request = new JSONObject();
    private JSONObject postData = new JSONObject();
    private JSONArray labels;
    private List<String> tags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        TextView textView = findViewById(R.id.ResultsText);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);
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

                        //String Array used for tags, String used to print
                        //TODO: This is the string array used to search web
                        tags = new ArrayList<String>();
                        String LabelString = "";

                        for(int i=0;i<labels.length();i++) {
                            try {
                                tags.add(labels.getJSONObject(i).getString("description") + "\n");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //Get info from array to put into a string for textview
                        for(int i = 0; i < tags.size(); i++){
                            LabelString += tags.get(i);
                        }

                        ((TextView)findViewById(R.id.ResultsText)).setText(LabelString);
                        SearchWebForImages(tags);
                    }


                    //required failure method
                    @Override
                    public void failure(@NotNull Request request,
                                        @NotNull Response response,
                                        @NotNull FuelError fuelError) {}
                });


    }

    //TODO: Make Method to search the web using ArrayList "tags"
    //TODO: Refine search more, maybe call a better method
    //TODO: Save images from google shops, put in the UI
    public void SearchWebForImages(List<String> tags){
        //CODE HERE
        String SearchString = "http://www.google.com/search?biw=1536&bih=710&tbm=shop&ei=96ckWqvMF4LWjwOSh5GYBA&q="
                + tags.get(0) + ", " + tags.get(1) + ", " + tags.get(3) + ", " + tags.get(4) + ", " + tags.get(5);
        Uri uri = Uri.parse(SearchString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);


    }
}
