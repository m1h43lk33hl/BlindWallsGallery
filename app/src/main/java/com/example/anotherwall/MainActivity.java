package com.example.anotherwall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.anotherwall.Utilities.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private List<WallItem> WallItemList;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;

    // LifeCycle variables
    private String JSONResults = "";
    final static private String JSON_KEY_RESULTS = "";
    final static private String WALL_ITEM_LIST_KEY = "";

    // SharedPrefences variables
    private String APIUrlPreferenceString = "";
    private String langPreferenceString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyRecyclerViewAdapter(MainActivity.this, new ArrayList<WallItem>());
        mRecyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Setup shared preferences
        setupSharedPreferences();

        // Load the recyclerView
        loadRecyclerView(savedInstanceState);
    }

    private void setLanguageSettings(String lang)
    {
        // Create a string for country
        String country = "";


        if(lang.equals("en"))
            country = "EN";
        else if(lang.equals("nl"))
            country = "NL";

        // Use constructor with country
        Locale locale = new Locale(lang, country);

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }

    private void setupSharedPreferences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        APIUrlPreferenceString = sharedPreferences.getString(getString(R.string.pref_api_url_key), getString(R.string.pref_api_url_def_value));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Language settings
        if(sharedPreferences.getBoolean(getString(R.string.pref_lang_check_key), true))
        {
            // Use device settings
            setLanguageSettings(Resources.getSystem().getConfiguration().locale.getLanguage());
            langPreferenceString = Resources.getSystem().getConfiguration().locale.getLanguage();

        }
        else
        {
            // Use preference settings
            setLanguageSettings(sharedPreferences.getString(getString(R.string.pref_lang_list_key), getString(R.string.pref_lang_label_en)));
            langPreferenceString = sharedPreferences.getString(getString(R.string.pref_lang_list_key), getString(R.string.pref_lang_label_en));
        }
    }

    private void loadRecyclerView(Bundle savedInstanceState)
    {
        // Lifecycle event to preserve data to prevent repeating API calls
        if(savedInstanceState != null &&  savedInstanceState.containsKey(WALL_ITEM_LIST_KEY) && savedInstanceState.containsKey(JSON_KEY_RESULTS))
        {
            progressBar.setVisibility(View.GONE);

            // Set again in order to preserve state on future rotations
            JSONResults = savedInstanceState.getString(JSON_KEY_RESULTS);

            // Set wallItemList again in order to preserve state on future rotations
            WallItemList = savedInstanceState.getParcelableArrayList(WALL_ITEM_LIST_KEY);

            populateRecyclerView();
        }
        else
        {
            // First execution
            new DownloadTask().execute();
        }
    }

    public class DownloadTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result;
            String blindWallResults;

            try {
                // Error fix, because NetworkUtils.buildUrl returns null when failing
                if(null == NetworkUtils.buildUrl(APIUrlPreferenceString))
                    return false;

                // Get response from API
                blindWallResults = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(APIUrlPreferenceString));

                // Send to parser
                JSONResults = blindWallResults;
                parseResult(blindWallResults);
                result = true;

            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }

            // When failed
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);

            // If succeeded
            if (result) {

                // Populate the recyclerView with new items
                populateRecyclerView();

                // Show toast when data has been loaded for the first time
                Toast.makeText(MainActivity.this, getString(R.string.json_toast_data_loaded), Toast.LENGTH_SHORT).show();
            } else {
                // If failed make toast
                Toast.makeText(MainActivity.this, getString(R.string.json_toast_data_failed), Toast.LENGTH_SHORT).show();

                // Set empty wallList on fail

                adapter.setWallItems(new ArrayList<WallItem>());
            }
        }
    }

    /**
     * Populates recyclerView and adds OnItemClickListener
     */
    private void populateRecyclerView()
    {
        // Update adapter with new WallItemList
        adapter.setWallItems(WallItemList);

        adapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(WallItem item) {
                // Function to start new activity
                Class detailActivity = DetailActivity.class;

                // Create intent
                Intent startDetailActivityIntent = new Intent(MainActivity.this, detailActivity);

                // Add object to intent
                startDetailActivityIntent.putExtra("detailWallItem", (Parcelable)item);

                // Start activity
                startActivity(startDetailActivityIntent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save instances of existing objects
        outState.putString(JSON_KEY_RESULTS, JSONResults);
        outState.putParcelableArrayList(WALL_ITEM_LIST_KEY, (ArrayList<? extends Parcelable>) this.WallItemList);
    }

    /**
     * Parses JSON result
     *
     * @param result
     */
    private void parseResult(String result) {

        WallItemList = new ArrayList<>();

        try {

            JSONArray mJsonArray = new JSONArray(result);

            // Loop through JSON array
            for (int i = 0; i < mJsonArray.length(); i++) {

                // Get picture URI fragment from JSON
                String pictureURIFragment = mJsonArray.getJSONObject(i)
                        .getJSONArray("images").getJSONObject(0)
                        .getString("url");

                // Load images into String
                JSONArray JSONImageArray = mJsonArray.getJSONObject(i)
                        .getJSONArray("images");

                // Create array for wallItem
                String[] imageArray = new String[JSONImageArray.length()];

                // Loop through JSONArray
                for(int x = 0; x < JSONImageArray.length(); x++)
                {
                    String pictureURLFragment = JSONImageArray.getJSONObject(x).getString("url");

                    // Built picture
                    URL pictureURL = NetworkUtils.builtPictureUrl(pictureURLFragment.toLowerCase());
                    imageArray[x] = java.net.URLDecoder.decode(pictureURL.toString());
                }

                // Built picture
                URL pictureURL = NetworkUtils.builtPictureUrl(pictureURIFragment.toLowerCase());
                String cleanPictureUrl = java.net.URLDecoder.decode(pictureURL.toString());

                // add wall item to the list
                WallItem item = new WallItem();

                // Set fields of wallItem
                item.setThumbnail(cleanPictureUrl);
                item.setTitle(mJsonArray.getJSONObject(i).getString("author"));
                item.setPhotographer(mJsonArray.getJSONObject(i).getString("photographer"));
                item.setAddress(mJsonArray.getJSONObject(i).getString("address"));
                item.setMaterial(mJsonArray.getJSONObject(i).getJSONObject("material").getString(langPreferenceString));
                item.setDescription(mJsonArray.getJSONObject(i).getJSONObject("description").getString(langPreferenceString));
                item.setImgURLArray(imageArray);

                // Add wallItem to list
                WallItemList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.api_url_settings_item)
        {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDeviceLanguage()
    {
        Log.d("HERE", Locale.getDefault().getLanguage());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_api_url_key)))
        {
            // Update String again
            APIUrlPreferenceString = sharedPreferences.getString(getString(R.string.pref_api_url_key), getString(R.string.pref_api_url_def_value));
            new DownloadTask().execute();
        }

        if(key.equals(getString(R.string.pref_lang_list_key)) || key.equals(getString(R.string.pref_lang_check_key)))
        {
            // String to skip download if new settings is the same
            String previousLangString = langPreferenceString;

            // Language settings
            if(sharedPreferences.getBoolean(getString(R.string.pref_lang_check_key), true))
            {
                // Use device settings
                setLanguageSettings(Resources.getSystem().getConfiguration().locale.getLanguage());
                langPreferenceString = Resources.getSystem().getConfiguration().locale.getLanguage();
            }
            else
            {
                // Use preference settings
                setLanguageSettings(sharedPreferences.getString(getString(R.string.pref_lang_list_key), getString(R.string.pref_lang_label_en)));
                langPreferenceString = sharedPreferences.getString(getString(R.string.pref_lang_list_key), getString(R.string.pref_lang_label_en));
            }

            if(!previousLangString.equals(langPreferenceString))
                // Reload data after executing new Download task
                new DownloadTask().execute();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}