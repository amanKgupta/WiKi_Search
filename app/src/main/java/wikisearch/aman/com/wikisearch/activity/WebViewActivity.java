package wikisearch.aman.com.wikisearch.activity;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wikisearch.aman.com.wikisearch.R;
import wikisearch.aman.com.wikisearch.interfaces.WikiInterfaces;
import wikisearch.aman.com.wikisearch.model.ContactDetails;
import wikisearch.aman.com.wikisearch.model.WikiPedia;

/**
 * Created by Instinct on 7/31/2018.
 */

public class WebViewActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_web_dialog);

        toolbar = (Toolbar) findViewById(R.id.tolbar);
        webView = (WebView) findViewById(R.id.web);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ProgressBar progressBar = new ProgressBar(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        userNameCheck(progressBar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);


    }

    public void userNameCheck(ProgressBar progressBar) {

        int pageid = getIntent().getIntExtra("pageid", 0);
        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=info&pageids=" + pageid + "&inprop=url";
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String querystring = jsonResponse.getString("query");
                    JSONObject query = new JSONObject(querystring);

                    String pagestring = query.getString("pages");
                    JSONObject pages = new JSONObject(pagestring);

                    String uniqueString = pages.getString(pageid + "");
                    JSONObject unique = new JSONObject(uniqueString);

                    String urlstring = unique.getString("fullurl");
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(urlstring);
                    progressBar.setVisibility(View.GONE);
                    Log.e("url", urlstring);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("json error", e.getMessage());
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.e("Volley error", error.getMessage());
            }
        });
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(postRequest);
    }
}
