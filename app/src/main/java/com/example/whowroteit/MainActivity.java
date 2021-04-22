package com.example.whowroteit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    TextView resultView;
    EditText searchedText;
    TextView resultView2;
    NetworkInfo networkInfo;
// check the link for more info
    //https://developer.android.com/codelabs/android-training-asynctask-asynctaskloader?index=..%2F..%2Fandroid-training#4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView=findViewById(R.id.resultView);
        searchedText=findViewById(R.id.searchedName);
        resultView2=findViewById(R.id.resultView2);
    }
    public void searchForBooks(View view){
        String searched = searchedText.getText().toString();
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = null;
        if(connectivityManager!=null){

            networkInfo=connectivityManager.getActiveNetworkInfo();
        }if (networkInfo != null && networkInfo.isConnected()
                && searched.length() != 0){
        new SearchTask(resultView,resultView2).execute(searched);
        resultView.setText("");
        resultView2.setText("loading...");} else {
            if (searched.length() == 0) {
                resultView2.setText("");
               resultView.setText("no result");
            } else {
                resultView2.setText("");
                resultView.setText("no network");
            }
        }
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
    public class SearchTask extends AsyncTask<String,Void,String>{

        WeakReference<TextView> mTextView;
        WeakReference<TextView> mTextView2;
        public SearchTask(TextView tv,TextView tv2){
            mTextView=new WeakReference<TextView>(tv);
            mTextView2=new WeakReference<TextView>(tv2);
        }


        @Override
        protected String doInBackground(String... strings) {


            return NetworkUtils.getBookInfo(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject= new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int i = 0;
                String title = null;
                String authors = null;
                while (i < jsonArray.length() &&
                        (authors == null && title == null)) {
                    // Get the current item information.
                    JSONObject book = jsonArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject  acc = book.getJSONObject("accessInfo");

                    // Try to get the author and title from the current item,
                    // catch if either field is empty and move on.
                    try {
                        title = acc.getString("country");
                        authors = volumeInfo.getString("authors");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Move to the next item.
                    i++;
                }
                if(title!=null && authors!=null){
                    mTextView.get().setText(title);
                    mTextView2.get().setText(authors);
                }else {
                    mTextView.get().setText("no results");
                }

            } catch (JSONException e) {
                mTextView.get().setText("no results");
                e.printStackTrace();
            }
        }
    }
}