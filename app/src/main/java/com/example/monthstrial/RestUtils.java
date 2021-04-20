package com.example.monthstrial;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

interface ElvisResultCallback<T>{
    void onComplete(ElvisResult<T> result);
}
public class RestUtils {

    public static boolean IsNetworkAvailable(Context context){
        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if(netInfo == null)
            return false;
        return netInfo.isConnected();
    }
    
    public static void LoginToElvis(Executor executor, ElvisResultCallback<String> callback, UserInfo user) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ElvisResult<String> result = ElvisLogin.LoginToElvis(user);
                        callback.onComplete(result);
                    } catch (Exception ex) {
                        ElvisResult<String> result = new ElvisResult.Error<>(ex);
                        callback.onComplete(result);
                    }
                }
            });
        }
        catch (Exception ex){
            String message = ex.getMessage();
        }
    }

    public static void SearchElvis(Executor executor, ElvisResultCallback<String> callback, UserInfo user, String query) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ElvisResult<String> result = ElvisSearch.SearchElvis(user, query);
                        callback.onComplete(result);
                    } catch (Exception ex) {
                        ElvisResult<String> result = new ElvisResult.Error<>(ex);
                        callback.onComplete(result);
                    }
                }
            });
        }
        catch (Exception ex){
            String message = ex.getMessage();
        }
    }
}

class ElvisLogin {

    public static ElvisResult<String> LoginToElvis(UserInfo user) {
        try {
            URL url = new URL("https://dam.evolvedmedia.co.uk/services/login?username=" + user.Username + "&password=" + user.Password);
            HttpURLConnection elvisCon = (HttpURLConnection) url.openConnection();
            elvisCon.setRequestMethod("POST");
            elvisCon.setConnectTimeout(5000);
            elvisCon.setReadTimeout(5000);

            InputStream inputStream;
            CookieManager cookieManager = new java.net.CookieManager();
            String authToken = "";
            int status = elvisCon.getResponseCode();
            if (status != elvisCon.HTTP_OK) {
                inputStream = elvisCon.getErrorStream();
            } else {
                inputStream = elvisCon.getInputStream();

                String cookiesHeader = "Set-Cookie";

                Map<String, List<String>> headerFields = elvisCon.getHeaderFields();
                List<String> cookiesHeaderList = headerFields.get(cookiesHeader);

                if(cookiesHeader != null)
                {
                    for (String cookie : cookiesHeaderList)
                    {
                        if (cookie.startsWith("authToken")) {
                            //cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                            authToken = HttpCookie.parse(cookie).get(0).toString();
                        }
                    }
                }
            }
            InputStreamReader inputSR = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputSR);
            String inputLine;
            StringBuilder elvisContent = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                elvisContent.append(inputLine);
            }
            reader.close();
            ElvisResult.Success<String> result = new ElvisResult.Success<String>(elvisContent.toString());
            result.AuthToken = authToken;
            return result;
        } catch (Exception ex) {
            return new ElvisResult.Error<String>(ex);
        }
    }
}

    class ElvisSearch {

        public static ElvisResult<String> SearchElvis(UserInfo user, String query) {
            try {
                URL url = new URL("https://dam.evolvedmedia.co.uk/services/search?q=" + query);
                HttpURLConnection elvisCon = (HttpURLConnection) url.openConnection();
                elvisCon.setRequestProperty("Cookie", user.LongAuthToken);
                elvisCon.setRequestMethod("POST");
                elvisCon.setConnectTimeout(5000);
                elvisCon.setReadTimeout(5000);

                InputStream inputStream;

                int status = elvisCon.getResponseCode();
                if (status != elvisCon.HTTP_OK) {
                    inputStream = elvisCon.getErrorStream();
                } else {
                    inputStream = elvisCon.getInputStream();
                }
                InputStreamReader inputSR = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputSR);
                String inputLine;
                StringBuilder elvisContent = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    elvisContent.append(inputLine);
                }
                reader.close();
                return new ElvisResult.Success<String>(elvisContent.toString());
            } catch (Exception ex) {
                return new ElvisResult.Error<String>(ex);
            }
        }
    }
