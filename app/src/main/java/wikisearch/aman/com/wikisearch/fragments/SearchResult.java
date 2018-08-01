package wikisearch.aman.com.wikisearch.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import wikisearch.aman.com.wikisearch.activity.CamerActivity;
import wikisearch.aman.com.wikisearch.activity.WebViewActivity;
import wikisearch.aman.com.wikisearch.adapter.ContactsAdapter;
import wikisearch.aman.com.wikisearch.interfaces.WikiInterfaces;
import wikisearch.aman.com.wikisearch.model.ContactDetails;
import wikisearch.aman.com.wikisearch.model.WikiPedia;

/**
 * Created by Instinct on 7/31/2018.
 */

public class SearchResult extends Fragment {
    RecyclerView rvContacts;

    List<ContactDetails> contactDetails;
    ContactsAdapter contactsAdapter;
    Interceptor ONLINE_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response response = chain.proceed(chain.request());
            int maxAge = 60; // read from cache
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        }
    };
    Interceptor OFFLINE_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isConnected()) {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }

            return chain.proceed(request);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_result_layout, container, false);
        rvContacts = (RecyclerView) rootView.findViewById(R.id.rv_cards);
        String strtext = getArguments().getString("edttext");

        if (strtext != null) {
            contactDetails = groupList(strtext);
            contactsAdapter = new ContactsAdapter(getContext(), contactDetails);
            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvContacts.setAdapter(contactsAdapter);

            contactsAdapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, ContactDetails contact, int position) {
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra("pageid", contact.getPageId());
                    getActivity().startActivity(intent);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_contacts_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setQueryHint("Search here...");

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        TextView searchBox = (TextView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView closeBtn = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeBtn.setImageResource(R.drawable.ic_baseline_close_24px);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setHintTextColor(Color.LTGRAY);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactDetails = groupList(newText);
                contactsAdapter = new ContactsAdapter(getContext(), contactDetails);
                rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvContacts.setAdapter(contactsAdapter);

                contactsAdapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, ContactDetails contact, int position) {
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra("pageid", contact.getPageId());
                        getActivity().startActivity(intent);
                    }
                });
                return false;
            }

        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public List<ContactDetails> groupList(String query) {
        final List<ContactDetails> items = new ArrayList<>();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(OFFLINE_INTERCEPTOR)
                .addNetworkInterceptor(ONLINE_INTERCEPTOR)
                .cache(provideCache())
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .validateEagerly(true)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        WikiInterfaces apiInterface = retrofit.create(WikiInterfaces.class);
        apiInterface.savePost("/w/api.php?action=query&format=json&prop=pageimages%7Cpageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpssearch=" + query).enqueue(new Callback<WikiPedia>() {
            @Override
            public void onResponse(Call<WikiPedia> call, Response<WikiPedia> response) {
                WikiPedia userListApi = response.body();
                if (userListApi != null) {
                    WikiPedia.Datum datum = userListApi.data;
                    if (datum != null) {
                        List<WikiPedia.Datum.UserList> userLists = datum.pages;
                        for (int i = 0; i < userLists.size(); i++) {
                            ContactDetails obj = new ContactDetails();
                            obj.title = userLists.get(i).title;
                            obj.pageId = userLists.get(i).pageid;
                            if (userLists.get(i).thumbnail != null) {
                                obj.imageurl = userLists.get(i).thumbnail.source;
                            }
                            items.add(obj);
                            contactsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WikiPedia> call, Throwable t) {

                t.printStackTrace();
                Log.e("faild", t.getMessage());
            }
        });
        return items;
    }

    private Cache provideCache() {
        Cache cache = new Cache(new File(getContext().getCacheDir(), "http-cache"),
                10 * 1024 * 1024); // 10 MB
        return cache;
    }

    public boolean isConnected() {
        try {
            android.net.ConnectivityManager e = (android.net.ConnectivityManager) getContext().getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = e.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.w("online", e.toString());
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ocr:
                Intent intent = new Intent(getContext(), CamerActivity.class);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

}
