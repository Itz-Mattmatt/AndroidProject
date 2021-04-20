package com.example.monthstrial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginScreen extends AppCompatActivity {

    TextView txtUsername;
    TextView txtPassword;
    TextView txtResult;

    Executor executor;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this::Login_OnClick);
        txtUsername = (TextView)findViewById(R.id.txtUsername);
        txtPassword = (TextView)findViewById(R.id.txtPassword);
        txtResult = (TextView)findViewById(R.id.txtResult);

        executor = Executors.newSingleThreadExecutor();
    }

    public void Login_OnClick(View v){
        try{
            String username = txtUsername.getText().toString();
            String password = txtPassword.getText().toString();

            UserInfo newUser = null;
            if((username != null || username != "") && (password != null || password != "")){
                newUser = new UserInfo(username, password);
                ConnectToElvis(newUser);
            }
        }
        catch (Exception ex){
            String error = ex.getMessage();
        }
    }

    public void ConnectToElvis(UserInfo user){
        try {
            //Checks for network connection before carrying on.
            if(RestUtils.IsNetworkAvailable(this)) {
                //Calls the elvis login procedure
                RestUtils.LoginToElvis(executor, new ElvisResultCallback<String>() {
                    @Override
                    public void onComplete(ElvisResult<String> result) {
                        if(result instanceof ElvisResult.Success){
                            resultText = ((ElvisResult.Success<String>) result).elvisData.toString();
                            try {
                                //Extracts relevant info and moves on to next page.
                                JSONObject jsonObj = new JSONObject(resultText);
                                boolean isSuccess = jsonObj.getBoolean("loginSuccess");
                                if(isSuccess) {
                                    String authCode = jsonObj.getString("csrfToken");
                                    user.AuthToken = authCode;

                                    Intent intent = new Intent(LoginScreen.this, ImageGallery.class);
                                    intent.putExtra("User Info", new String[]{user.Username, user.Password, user.AuthToken});
                                    intent.putExtra("Auth Token", ((ElvisResult.Success<String>) result).AuthToken.toString());
                                    LoginScreen.this.startActivity(intent);
                                }
                                else{
                                    resultText = jsonObj.getString("loginFaultMessage");
                                }

                            }
                            catch (Exception ex){
                                String errorMessage = ex.getMessage();
                            }

                        }
                        else{
                            resultText = ((ElvisResult.Error<String>) result).exception.getMessage();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtResult.setText(resultText);
                            }
                        });

                    }


                }, user);
            }
        }
        catch (Exception ex){
            String message = ex.getMessage();
            if(message != null){
            }
        }
    }


}