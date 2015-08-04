/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/12/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements ArtistsFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Top Tracks fragment tag.
     */
    private final static String TRACKSFRAGMENT_TAG = "TRACKSFTAG";

    /**
     * Indicates if layout has two fragments.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top_tracks_container) != null) {
            // Screen > sw600dp - show top tracks
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.artists_container, new ArtistsFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onArtistItemSelected(Artist selectedArtist) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(TopTracksFragment.TOPTRACKS_ARTIST, selectedArtist);

            TopTracksFragment ttFragment = new TopTracksFragment();
            ttFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, ttFragment, TRACKSFRAGMENT_TAG)
                    .commit();
        } else {
            // Start Top Tracks Activity using selected artist
            Intent detailIntent = new Intent(this, TopTracksActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Artist.class.getName(), selectedArtist);
            detailIntent.putExtra(TopTracksActivity.INSTANCE_BUNDLE, bundle);
            startActivity(detailIntent);

        }
    }
}
