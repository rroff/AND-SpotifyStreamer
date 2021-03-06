/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/21/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity for Top Tracks.
 */
public class TopTracksActivity extends ActionBarActivity {

    public static final String INSTANCE_BUNDLE = "TopTracksActivityInstanceBundle";

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed from this
     *                           previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        if (savedInstanceState == null) {
            // Create the fragment and pass through the bundle as arguments
            TopTracksFragment ttFragment = new TopTracksFragment();
            ttFragment.setArguments(getIntent().getBundleExtra(TopTracksActivity.INSTANCE_BUNDLE));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, ttFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
