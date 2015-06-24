package com.dintresearch.rroff.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private final String LOG_NAME = TopTracksFragment.class.getName();

    private String mArtistIdStr;

    private String mArtistNameStr;

    private TopTrackAdapter mTopTracksAdapter;

    public TopTracksFragment() {
    }

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

        // Setup ListView for track results
        mTopTracksAdapter = new TopTrackAdapter(getActivity());
        final ListView listView = (ListView)rootView.findViewById(R.id.listview_top_tracks);
        listView.setAdapter(mTopTracksAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setSubtitle();
        updateTopTracks();
    }

    private void setSubtitle() {
        if (mArtistNameStr != null) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setSubtitle(mArtistNameStr);
        }
    }

    private void updateTopTracks() {
        new TopTracksTask(mTopTracksAdapter).execute(mArtistIdStr);
    }
}
