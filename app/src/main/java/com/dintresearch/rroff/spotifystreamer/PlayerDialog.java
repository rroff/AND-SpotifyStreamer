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

import java.util.ArrayList;

public class PlayerDialog extends DialogFragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerDialog.class.getName();

    private ArrayList<Track> mTracks;
    private int mPosition;

    private PlayerHelper mPlayerHelper;

    public PlayerDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track info is passed in via arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTracks = arguments.getParcelableArrayList(PlayerHelper.TRACK_ARRAY);
            mPosition = arguments.getInt(PlayerHelper.TRACK_POSITION);
        } else {
            Log.e(LOG_TAG, "No tracks provided");
            mTracks = null;
            mPosition = 0;
        }

        // Restore track position when rotation occurs
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PlayerHelper.TRACK_POSITION)) {
                mPosition = savedInstanceState.getInt(PlayerHelper.TRACK_POSITION);
            }
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        mPlayerHelper = new PlayerHelper(view, getActivity(), mTracks, mPosition);

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Provide current track position
        if (mPlayerHelper != null) {
            outState.putInt(PlayerHelper.TRACK_POSITION, mPlayerHelper.getPlaylistPosition());
        }
    }
}
