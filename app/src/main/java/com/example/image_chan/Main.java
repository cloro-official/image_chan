package com.example.image_chan;
// CLORO - 2020
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;

import android.view.Menu;
import android.view.MenuInflater;

import com.example.image_chan.core.DatabaseHelper;
import com.example.image_chan.core.SearchFormatter;
import com.example.image_chan.ui.gallery.GalleryFragment;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.entities.impl.SafebooruImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends AppCompatActivity implements GalleryFragment.GalleryListener {
    private boolean firstStarted = true;
    private GalleryFragment gallery;
    private DatabaseHelper databaseHelper;
    private final SearchFormatter formatter = new SearchFormatter();
    private FragmentTransaction m;
    private TextView searched;
    private String mode;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing())
            firstStarted = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        m = getSupportFragmentManager().beginTransaction();
        gallery = new GalleryFragment();

        m.add(R.id.nav_view, gallery);
        m.commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        searched = findViewById(R.id.searched);

        handleIntent(getIntent());

        Button home = findViewById(R.id.clear);
        Button favorites = findViewById(R.id.favorites);
        Button about = findViewById(R.id.about);

        View inclusion = findViewById(R.id.inclusion);

        LinearLayout about_page = findViewById(R.id.about_page);

        TextView fav_text = findViewById(R.id.fav_text);
        TextView home_text = findViewById(R.id.home_text);
        TextView abt_text = findViewById(R.id.abt_text);

        home.setOnClickListener(view -> {
            about_page.setVisibility(View.GONE);
            fav_text.setVisibility(View.GONE);
            home_text.setVisibility(View.VISIBLE);
            inclusion.setVisibility(View.VISIBLE);
            searched.setVisibility(View.GONE);
            abt_text.setVisibility(View.GONE);

            DefaultImageBoards.SAFEBOORU.get().async(this::populate);
        });

        favorites.setOnClickListener(view -> {
            about_page.setVisibility(View.GONE);
            home_text.setVisibility(View.GONE);
            fav_text.setVisibility(View.VISIBLE);
            inclusion.setVisibility(View.VISIBLE);
            searched.setVisibility(View.GONE);
            abt_text.setVisibility(View.GONE);

            constructFavorites();
        });

        about.setOnClickListener(view ->  {
            home_text.setVisibility(View.GONE);
            fav_text.setVisibility(View.GONE);
            abt_text.setVisibility(View.VISIBLE);

            about_page.setVisibility(View.VISIBLE);
            inclusion.setVisibility(View.GONE);
        });

        if (firstStarted)
            DefaultImageBoards.SAFEBOORU.get().async(this::populate);
    }

    @Override
    public void onInputSent(int ID) {
        gallery.updateText(ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        startSearch(null, false, appData, false);

        handleIntent(getIntent());
        return super.onSearchRequested();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(getIntent());
    }

    private void refresh(Bundle b2) {
        gallery.setArguments(b2);
        getSupportFragmentManager().beginTransaction()
                .detach(gallery)
                .attach(gallery)
                .commit();
    }

    private void populate(List<SafebooruImage> images) {
        Bundle b2 = new Bundle();

        // bruh
        m.detach(gallery);
        m.attach(gallery);

        if (images.size() == 0)
            b2.putString("state", "none");
        else {
            b2.putString("state", "issearch");

            refresh(b2);
            ArrayList<String> urls = new ArrayList<>();

            ArrayList<String> ratingLong = new ArrayList<>();
            ArrayList<String> ratingShort = new ArrayList<>();
            ArrayList<String> tags = new ArrayList<>();

            boolean[] Existing = new boolean[images.size()];

            for (int i = 0; i <= images.size(); i++) {
                SafebooruImage item = images.get(i);

                Existing[i] = databaseHelper.ifExists(item.getURL());
                urls.add(item.getFileUrl());
                ratingLong.add(item.getRating().getLongName());
                ratingShort.add(item.getRating().getShortName());

                tags.add(item.getTags().toString());

                // there are better ways to do this, but this works for now.
                b2.putStringArrayList("urls", urls);
                b2.putBooleanArray("exists", Existing);

                b2.putStringArrayList("ratingLong", ratingLong);
                b2.putStringArrayList("ratingShort", ratingShort);
                b2.putStringArrayList("tags", tags);

                System.out.println("ADDED " + urls.size());
                System.out.println("FILE_URL: " + item.getFileUrl());
                System.out.println("URL: " + item.getURL());
            }

            System.out.println("IS: " + urls);
            gallery.setArguments(b2);
        }

        refresh(b2);
    }

    private void search(String query) {
        String toSearch = String.valueOf(Arrays.asList(query.split(", ")));

        DefaultImageBoards.SAFEBOORU.search(toSearch.substring(1, toSearch.length() - 1).replaceAll(", ",  " ")).async(this::populate);
    }

    public void constructFavorites() {
        Bundle b2 = new Bundle();

        b2.putString("state", "issearch");

        refresh(b2);

        ArrayList<String> urls = new ArrayList<>();
        ArrayList<Integer> names = new ArrayList<>();

        ArrayList<String> ratingLong = new ArrayList<>();
        ArrayList<String> ratingShort = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();

        Cursor data = databaseHelper.getData();
        while (data.moveToNext()) {
            urls.add(data.getString(0));
            names.add(0);

            tags.add(data.getString(1));
            ratingLong.add(data.getString(2));
            ratingShort.add(data.getString(3));

            b2.putStringArrayList("urls", urls);

            b2.putStringArrayList("ratingLong", ratingLong);
            b2.putStringArrayList("ratingShort", ratingShort);
            b2.putStringArrayList("tags", tags);

            System.out.println("ADDED " + urls.size());
        }

        refresh(b2);
    }

    private void handleIntent(Intent intent) {
        if (intent.getExtras() != null && intent.getExtras().containsKey("updateFavorites")) {
            Button favorites = findViewById(R.id.favorites);
            TextView fav_text = findViewById(R.id.fav_text);
            TextView home_text = findViewById(R.id.home_text);

            home_text.setVisibility(View.GONE);
            favorites.setVisibility(View.GONE);
            fav_text.setVisibility(View.VISIBLE);

            constructFavorites();

            return;
        }

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, Suggestions.AUTHORITY, Suggestions.MODE);

            TextView home_text = findViewById(R.id.home_text);
            home_text.setVisibility(View.GONE);

            Bundle b1 = new Bundle();

            b1.putString("state", "querying");

            String convTags = formatter.FormatToTagsStr(query);
            System.out.println("GOT: " + convTags);

            refresh(b1);

            String format = convTags.substring(1, convTags.length() - 1);
            String searchedText = "SEARCHING FOR: " + format;
            search(format);

            searched = findViewById(R.id.searched);
            searched.setVisibility(View.VISIBLE);
            searched.setText(searchedText);

            firstStarted = false;
            // suggestions.saveRecentQuery(query, null);
        }
    }

}
