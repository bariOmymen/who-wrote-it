package com.example.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetworkUtils {
    private static final String LOG_TAG =
            NetworkUtils.class.getSimpleName();
    private static final String BOOK_BASE_URL =  "https://www.googleapis.com/books/v1/volumes?";
    // Parameter for the search string.
    private static final String QUERY_PARAM = "q";
    // Parameter that limits search results.
    private static final String MAX_RESULTS = "maxResults";
    // Parameter to filter by print type.
    private static final String PRINT_TYPE = "printType";

    static String getBookInfo(String queryString){
        HttpURLConnection urlConnection=null;
        BufferedReader bufferedReader=null;
        String BookJsonString = null;
        try {
            Uri uri=Uri.parse(BOOK_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAM,queryString)
                    .appendQueryParameter(MAX_RESULTS,"5")
                    .appendQueryParameter("download","epub")
                    .appendQueryParameter(PRINT_TYPE,"books").build();
            URL url=new URL(uri.toString());
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder=new StringBuilder();
            String line;
            while ((line=bufferedReader.readLine())!=null){
                builder.append(line);
                builder.append("\n");
                if(builder.length()==0){
                    return null;
                }

                BookJsonString=builder.toString();



            }

        }catch (Exception e){
            e.printStackTrace();

        }finally {
            urlConnection.disconnect();
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        Log.d(LOG_TAG, BookJsonString);
      return BookJsonString;
    }
}
