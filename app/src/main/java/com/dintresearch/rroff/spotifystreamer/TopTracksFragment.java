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
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;

/**
 * Fragment for the Top Tracks activity.
 */
public class TopTracksFragment extends Fragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = TopTracksFragment.class.getName();

    /**
     * Saved instance label for JSON string
     */
    private static final String JSON_STRING_LABEL = "mTopTracksJsonStr";

    /**
     * Artist ID
     */
    private String mArtistIdStr;

    /**
     * Artist name
     */
    private String mArtistNameStr;

    /**
     * Adapter for Top Tracks ListView
     */
    private TopTrackAdapter mTopTracksAdapter;

    /**
     * Top Tracks JSON string, restored from saved instance
     */
    private String mTopTracksJsonStr;

    /**
     * Executed task for retrieving Top Tracks data from Spotify
     */
    private TopTracksTask mTopTracksTask;

    /**
     * Constructor
     */
    public TopTracksFragment() {
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view to which the fragment's UI should be
     *                  attached
     * @param savedInstanceState If non-null, this fragment is being re-constructed from this
     *                           previous saved state
     *
     * @return View for the fragment UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        // Artist info is passed in via Intent
        Intent intent = getActivity().getIntent();
        if (  (intent != null)
           && intent.hasExtra(Intent.EXTRA_TEXT)
           && intent.hasExtra(Intent.EXTRA_TITLE)) {
            mArtistNameStr = intent.getStringExtra(Intent.EXTRA_TITLE);
            mArtistIdStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Restore saved data
        if (savedInstanceState != null) {
            mTopTracksJsonStr = savedInstanceState.getString(JSON_STRING_LABEL);
        }

        // Setup ListView for track results
        mTopTracksAdapter = new TopTrackAdapter(getActivity());
        final ListView listView = (ListView) rootView.findViewById(R.id.listview_top_tracks);
        listView.setAdapter(mTopTracksAdapter);

        return rootView;
    }

    /**
     * Runs when fragment is started.
     */
    @Override
    public void onStart() {
        super.onStart();
        setSubtitleToArtistName();
        updateTopTracks();
    }

    /**
     * Saves instance state for later restoration, if needed.
     *
     * @param outState Outbound state bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // JSON string can source from the string member variable (if restored from a previous
        // state), or from the executed task
        if (mTopTracksJsonStr != null) {
            outState.putString(JSON_STRING_LABEL, mTopTracksJsonStr);
        } else if (mTopTracksTask != null) {
            outState.putString(JSON_STRING_LABEL, mTopTracksTask.getTopTracksJsonStr());
        }
    }

    /**
     * Displays artist name as subtitle in activity.
     */
    private void setSubtitleToArtistName() {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        if ((actionBar != null) && (mArtistNameStr != null)) {
            actionBar.setSubtitle(mArtistNameStr);
        }
    }

    /**
     * Updates Top Tracks data in UI.
     */
    private void updateTopTracks() {

        if (mTopTracksJsonStr != null) {
            // If JSON string exists, no need to reexecute query
            try {
                TopTrack[] topTracks = TopTracksTask.getTopTracksDataFromJson(mTopTracksJsonStr);
                mTopTracksAdapter.clear();
                mTopTracksAdapter.addAll(topTracks);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error ", e);
            }
        } else {
            mTopTracksTask = new TopTracksTask(mTopTracksAdapter);
            mTopTracksTask.execute(mArtistIdStr);
        }
    }
}
