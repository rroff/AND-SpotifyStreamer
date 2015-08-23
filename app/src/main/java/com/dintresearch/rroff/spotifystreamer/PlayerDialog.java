/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 8/23/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayerDialog extends DialogFragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerDialog.class.getName();

    private Track mTrack;

    private PlayerHelper mPlayerHelper;

    public PlayerDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track info is passed in via arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTrack = arguments.getParcelable(Track.class.getName());
        } else {
            Log.e(LOG_TAG, "No track provided");
            mTrack = null;
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container);
        mPlayerHelper = new PlayerHelper(view, getActivity(), mTrack);

        return view;
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
