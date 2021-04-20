package com.example.monthstrial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageGallery extends AppCompatActivity {

    LinearLayout galleryLayout;
    UserInfo currentUser;
    Executor executor;

    String authToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_gallery);
        galleryLayout = (LinearLayout)findViewById(R.id.linLayGallery);

        Intent intent = getIntent();
        String[] user = intent.getStringArrayExtra("User Info");
        currentUser = new UserInfo(user[0], user[1]);
        currentUser.AuthToken = user[2];
        currentUser.LongAuthToken = intent.getStringExtra("Auth Token");

        executor = Executors.newSingleThreadExecutor();

        GetImages();
    }

    public void GetImages(){

        String query = "ancestorPaths:\"/Android\"";

        try {
            if(RestUtils.IsNetworkAvailable(this)) {
                //Calls Elvis Search procedure.
                RestUtils.SearchElvis(executor, new ElvisResultCallback<String>() {
                    @Override
                    public void onComplete(ElvisResult<String> result) {
                        if(result instanceof ElvisResult.Success){
                           String resultText = ((ElvisResult.Success<String>) result).elvisData.toString();
                            try {
                                //Gets results json and sends them to get drawn.
                                SearchResult searchResults = SearchResult.GetSearchResults(resultText);
                                DrawImages(searchResults, currentUser);
                            }
                            catch (Exception ex){
                                String errorMessage = ex.getMessage();
                            }

                        }
                        else{
                           String resultText = ((ElvisResult.Error<String>) result).exception.getMessage();
                        }
                    }
                }, currentUser, query);
            }
        }
        catch (Exception ex){
            String message = ex.getMessage();
            if(message != null){
            }
        }
    }

    public void DrawImages(SearchResult results, UserInfo user){
        try {
            //Parses url for each image and gets it from elvis and then adds it to the view.
            for (int i = 0; i < results.totalHits; i++) {
                ImageView image = new ImageView(this);
                URL imageUrl = new URL(results.hits[i].thumbnailUrl);
                HttpURLConnection elvisCon = (HttpURLConnection) imageUrl.openConnection();
                elvisCon.setRequestProperty("Cookie", user.LongAuthToken);
                elvisCon.setRequestProperty("X-CSRF-TOKEN", user.AuthToken);
                elvisCon.setRequestMethod("POST");
                elvisCon.setConnectTimeout(5000);
                elvisCon.setReadTimeout(5000);

                Bitmap bmp = BitmapFactory.decodeStream(elvisCon.getInputStream());
                image.setImageBitmap(bmp);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      galleryLayout.addView(image);
                    }
                });
            }
        }
        catch(Exception ex){
            String message = ex.getMessage();
        }
    }
}