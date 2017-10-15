package com.example.andrew.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {
    private ArrayList<book> bookArrayList = new ArrayList<>();
    private String Search="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookAsyncTask task = new  bookAsyncTask();
        task.execute();
    }

    public void edittext(View view){
        EditText searchtext = (EditText) findViewById(R.id.Searchs);
        Search = searchtext.getText().toString();
    }
    public void submit(View view) {
        edittext(view);
        bookAsyncTask task = new  bookAsyncTask();
        task.execute();
    }

    public void update() {
        bookAdapter BookAdapter = new bookAdapter(this, bookArrayList);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(BookAdapter);
    }

    public class bookAsyncTask extends AsyncTask<URL, Void, ArrayList<book>> {

        private final String LOG_TAG = bookAsyncTask.class.getName();

        @Override
        public ArrayList<book> doInBackground(URL... urls) {
            URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q="+Search+"&maxResults=3");
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }
            ArrayList<book> test = Json(jsonResponse);
            return test;
        }

        protected void onPostExecute(ArrayList<book> books) {
            if (books == null) {
                return;
            }
            else {
                update();
            }
        }

        public  ArrayList<book> Json(String jsonResponse) {
            try {
                JSONObject JsonResponseurl = new JSONObject(jsonResponse);
                JSONArray jasonArray = JsonResponseurl.getJSONArray("items");
                for (int i = 0; i < jasonArray.length(); i++) {
                    JSONObject bookJsonObject = jasonArray.getJSONObject(i);
                    JSONObject firstobject = bookJsonObject.getJSONObject("volumeInfo");
                    JSONObject bookJsonArray = new JSONObject(jsonResponse);
                        String title = "", language = "", author = "";
                        try {
                            title = firstobject.getString("title");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            language = firstobject.getString("language");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            author = firstobject.getString("authors");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        book booktest = new book(title, language, author);
                        bookArrayList.add(booktest);
                    }
                return bookArrayList;
            } catch (JSONException e) {
                Log.e("Query Error", "Problem in the book JSON", e);
            }
            return null;
        }

        public String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP Request.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        public  URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error building the URL ", e);
            }
            return url;
        }

        public String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }
}
