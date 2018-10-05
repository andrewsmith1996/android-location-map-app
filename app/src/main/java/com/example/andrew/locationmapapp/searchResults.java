package com.example.andrew.locationmapapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class searchResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Get the intent
        Intent searchIntent = getIntent();

        // Get the search term
        String searchTerm = searchIntent.getStringExtra("searchTerm");

        // Create the URI for the intent
        String uri = "geo:0,0?q=" + searchTerm;

        // Begin the map intent
        Intent intent_map = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent_map);
    }
}
