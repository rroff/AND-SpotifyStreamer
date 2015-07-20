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
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Fragment for the Top Tracks activity.
 */
public class TopTracksFragment extends Fragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = TopTracksFragment.class.getName();

    /**
     * Artist Info
     */
    private Artist mArtist;

    /**
     * Adapter for Top Tracks ListView
     */
    private TopTrackAdapter mTopTracksAdapter;

    /**
     * Flag to indicate if instance data was restored
     */
    private boolean mInstanceDataRestored = false;

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

        mTopTracksAdapter = new TopTrackAdapter(getActivity());

        // Artist info is passed in via Intent
        Intent intent = getActivity().getIntent();
        if (  (intent != null)
           && intent.hasExtra(TopTracksActivity.INSTANCE_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(TopTracksActivity.INSTANCE_BUNDLE);
            mArtist = bundle.getParcelable(Artist.class.getName());
        }

        // Restore saved data
        if (savedInstanceState != null) {
            String countryCode
                    = savedInstanceState.getString(getString(R.string.pref_country_key));
            mTopTracksAdapter.setCountryCode(countryCode);

            ArrayList<Track> tracks
                        = savedInstanceState.getParcelableArrayList(TopTrackAdapter.class.getName());
            mTopTracksAdapter.addAll(tracks);
            mInstanceDataRestored = true;
        }

        // Setup ListView for track results
        final ListView listView = (ListView) rootView.findViewById(R.id.listview_top_tracks);
        listView.setAdapter(mTopTracksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start Player Activity using selected track
                Intent detailIntent = new Intent(getActivity(), PlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Track.class.getName(), mTopTracksAdapter.getItem(position));
                detailIntent.putExtra(PlayerActivity.INSTANCE_BUNDLE, bundle);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    /**
     * Runs when fragment is started.
     */
    @Override
    public void onStart() {
        super.onStart();
        setSubtitleToArtistName();

        String countryCode = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getString(getString(R.string.pref_country_key),
                                    getString(R.string.pref_country_default));

        // Update list if not restored by instance or if country code has changed
        if (  (!mInstanceDataRestored)
           || (  (countryCode != null)
              && (!countryCode.equals(mTopTracksAdapter.getCountryCode())) ) ) {
            updateTopTracks(countryCode);
        }
    }

    /**
     * Saves instance state for later restoration, if needed.
     *
     * @param outState Outbound state bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Preserve top tracks data
        outState.putString(getString(R.string.pref_country_key),
                           mTopTracksAdapter.getCountryCode());
        outState.putParcelableArrayList(TopTrackAdapter.class.getName(),
                mTopTracksAdapter.getArtistArrayList());
    }

    /**
     * Displays artist name as subtitle in activity.
     */
    private void setSubtitleToArtistName() {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        if ((actionBar != null) && (mArtist != null)) {
            actionBar.setSubtitle(mArtist.getName());
        }
    }

    /**
     * Updates Top Tracks data in UI.
     */
    private void updateTopTracks(String countryCode) {
        if (mArtist != null) {
            new TopTracksTask(mTopTracksAdapter).execute(mArtist.getId(), countryCode);
        }
    }
}
