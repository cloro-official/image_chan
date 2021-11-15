package com.example.image_chan.core;

import android.content.SearchRecentSuggestionsProvider;

// DEPRECATED
public class Suggestions extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.image_chan.core.Suggestions";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public Suggestions() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
