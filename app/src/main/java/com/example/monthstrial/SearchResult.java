package com.example.monthstrial;

import android.util.JsonReader;

import org.json.JSONObject;
import com.google.gson.Gson;

public class SearchResult {

    public int firstResult;
    public int maxResultHits;
    public int totalHits;
    public Hits[] hits;

    public static SearchResult GetSearchResults(String jsonString){
        SearchResult result = new SearchResult();

        try{
            Gson gson = new Gson();
            result = gson.fromJson(jsonString, SearchResult.class);
        }
        catch (Exception ex){
            String message = ex.getMessage();
        }
        return result;
    }


}

class Hits{

    public String id;
    public String thumbnailUrl;
    public MetaData metadata;
}

class MetaData{
    public String filename;
    public String baseName;
}

