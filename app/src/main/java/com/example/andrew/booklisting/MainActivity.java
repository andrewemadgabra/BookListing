package com.example.andrew.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<book> bookArrayList = new ArrayList<>();
    private String Search = "";
    private static final int READ_TIMEOUT = 10000;
    private static final String KEY_TITLE = "title";
    private static final String KEY_language = "language";
    private static final String KEY_authors = "authors";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        boolean flag = connection();
        if (flag) {
            bookAsyncTask task = new bookAsyncTask();
            task.execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    void bindView() {
        listView = (ListView) findViewById(R.id.listview);
        listView.setEmptyView(findViewById(R.id.textView));
    }

    public void edittext(View view) {
        EditText searchtext = (EditText) findViewById(R.id.Searchs);
        Search = searchtext.getText().toString();
    }

    public boolean connection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void submit(View view) {
        boolean flag = connection();
        if (flag) {
            edittext(view);
            bookAsyncTask task = new bookAsyncTask();
            task.execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void update() {
        bookAdapter BookAdapter = new bookAdapter(this, bookArrayList);
        listView.setAdapter(BookAdapter);
    }

    public class bookAsyncTask extends AsyncTask<URL, Void, ArrayList<book>> {
        private final String LOG_TAG = bookAsyncTask.class.getName();

        @Override
        public ArrayList<book> doInBackground(URL... urls) {
            URL url = createUrl("https://www.googleapis.com/books/v1/volumes?q=" + Search + "&maxResults=3");
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
            TextView notfoundsearch =(TextView)findViewById(R.id.searchnotfound);
            if (books == null) {
                notfoundsearch.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
            } else {
                update();
                notfoundsearch.setVisibility(View.INVISIBLE);
            }
        }

        public ArrayList<book> Json(String jsonResponse) {
            try {
                JSONObject JsonResponseurl = new JSONObject(jsonResponse);
                if(JsonResponseurl.has("items")){
                JSONArray jasonArray = JsonResponseurl.getJSONArray("items");
                for (int i = 0; i < jasonArray.length(); i++) {
                    JSONObject bookJsonObject = jasonArray.getJSONObject(i);
                    JSONObject firstobject = bookJsonObject.getJSONObject("volumeInfo");
                    String title = "", language = "", author = "";
                    if (firstobject.has("title")) {
                        title = firstobject.getString(KEY_TITLE);
                    } else {
                        title = "No found Title";
                    }
                    if (firstobject.has("language")) {
                        language = firstobject.getString(KEY_language);
                    } else {
                        language = "Not found language";
                    }
                    if (firstobject.has("authors")) {
                        author = firstobject.getJSONArray(KEY_authors).get(0).toString();
                    } else {
                        author = "Not found author";
                    }
                    book booktest = new book(title, language, author);
                    bookArrayList.add(booktest);
                }
                }
                else {
                    return null;
                }
                return bookArrayList;
            } catch (JSONException e) {
                Log.e("Error", "Problem in the book JSON", e);
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
                urlConnection.setReadTimeout(READ_TIMEOUT/* milliseconds */);
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

        public URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error in URL ", e);
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
