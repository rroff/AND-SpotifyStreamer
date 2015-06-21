package com.dintresearch.rroff.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private String mArtistStr;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        // Artist info is passed in via Intent
        Intent intent = getActivity().getIntent();
        if ((intent != null) && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mArtistStr != null) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setSubtitle(mArtistStr);
        }
    }
}
