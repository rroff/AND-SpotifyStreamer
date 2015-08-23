/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 7/19/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerFragment.class.getName();

    private PlayerHelper mPlayerHelper;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        // Artist info is passed in via Intent
        Track track;
        Intent intent = getActivity().getIntent();
        if (  (intent != null)
           && intent.hasExtra(PlayerActivity.INSTANCE_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(PlayerActivity.INSTANCE_BUNDLE);
            track = bundle.getParcelable(Track.class.getName());
        } else {
            Log.e(LOG_TAG, "No track provided");
            track = null;
        }

        mPlayerHelper = new PlayerHelper(rootView, getActivity(), track);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlayerHelper.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerHelper.unbindService();
    }




}
