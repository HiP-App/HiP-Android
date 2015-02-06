package com.example.timo.hip;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.*;

public class HttpAsyncTask extends AsyncTask<String, Void, String> {
    private MainActivity mActivity;
    private DBAdapter database;

    public HttpAsyncTask(MainActivity mActivity) {
        this.mActivity = mActivity;
        Log.i("Http", "Init");
    }

    @Override
    protected String doInBackground(String... urls) {
        Log.i("Http", "doInBackground");
        return GET(urls[0]);
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        Log.i("Http", "onPostExecute");
        this.database = new DBAdapter(this.mActivity);
        this.database.open();
        Log.i("Http", result);

        database.deleteAll();

        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray arr = jsonObj.getJSONArray("data");
            for (int i = 0; i < arr.length(); i++)
            {
                int id = arr.getJSONObject(i).getInt("id");
                String name = arr.getJSONObject(i).getString("name");
                String description = arr.getJSONObject(i).getString("description");
                Double lat = arr.getJSONObject(i).getDouble("lat");
                Double lng = arr.getJSONObject(i).getDouble("lng");
                String categories = arr.getJSONObject(i).getString("categories");
                String tags = arr.getJSONObject(i).getString("tags");
                database.insertRow(id, name, description, lat, lng, categories, tags);
                Log.i("Http", "Inserted ID: " + id);
            }
            this.mActivity.notifyExhibitSetChanged();
            database.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //etResponse.setText(result);
    }

    protected static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}