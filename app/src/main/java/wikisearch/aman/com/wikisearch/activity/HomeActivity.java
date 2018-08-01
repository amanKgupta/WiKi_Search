package wikisearch.aman.com.wikisearch.activity;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import wikisearch.aman.com.wikisearch.R;
import wikisearch.aman.com.wikisearch.fragments.SearchResult;

public class HomeActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    Activity homeactivity;
    FragmentTransaction fragmentTransaction;
    SearchResult searchResultfragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.tolbar);
        setSupportActionBar(toolbar);

        homeactivity = this;

        String searchResult = getIntent().getStringExtra("edt");

        Bundle bundle = new Bundle();
        bundle.putString("edttext", searchResult);

        searchResultfragment = new SearchResult();
        searchResultfragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .add(R.id.fragment_holder, searchResultfragment)
                .commit();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lyt_no_connection);
        if (isConnected()) {
            linearLayout.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public boolean isConnected() {
        try {
            android.net.ConnectivityManager e = (android.net.ConnectivityManager) getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = e.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.w("online", e.toString());
        }

        return false;
    }
}
