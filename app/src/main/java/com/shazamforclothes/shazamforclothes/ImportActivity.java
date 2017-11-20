package com.shazamforclothes.shazamforclothes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class ImportActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    TextView textView;
    private static final int PICK_IMAGE = 100;
    static final int CAM_REQUEST = 1;
    Uri imageURI;
    private JSONArray features = new JSONArray();
    private JSONObject feature = new JSONObject();
    private JSONArray requests = new JSONArray();
    private JSONObject request = new JSONObject();
    private JSONObject postData = new JSONObject();
    private JSONArray labels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        imageView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.button4);
        textView = (TextView)findViewById(R.id.Results);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode , resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageURI = data.getData();
            InputStream ImageStream = null;
            try {
                ImageStream = getContentResolver().openInputStream(imageURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap imageBitmap = BitmapFactory.decodeStream(ImageStream);
            imageView.setImageBitmap(imageBitmap);
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
                        List<String> tags = new ArrayList<String>();
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

                        ((TextView)findViewById(R.id.Results)).setText(LabelString);
                    }


                    //required failure method
                    @Override
                    public void failure(@NotNull Request request,
                                        @NotNull Response response,
                                        @NotNull FuelError fuelError) {}
                });

    }
}
