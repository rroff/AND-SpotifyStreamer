/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 7/19/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dintresearch.rroff.spotifystreamer.service.PlayerService;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerFragment.class.getName();

    private static final int IMAGE_HEIGHT = 400;
    private static final int IMAGE_WIDTH = 400;

    private Track mTrack;

    private PlayerService mBoundService;
    private boolean mServiceBound = false;

    private int mDuration;

    private Button mPlayPauseButton;
    private TextView mDurationTV;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        // Artist info is passed in via Intent
        Intent intent = getActivity().getIntent();
        if (  (intent != null)
           && intent.hasExtra(PlayerActivity.INSTANCE_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(PlayerActivity.INSTANCE_BUNDLE);
            mTrack = bundle.getParcelable(Track.class.getName());
        }

        TextView artistNameTV = (TextView)rootView.findViewById(R.id.artist_name_textview);
        TextView albumNameTV  = (TextView)rootView.findViewById(R.id.album_name_textview);
        TextView trackNameTV  = (TextView)rootView.findViewById(R.id.track_name_textview);
        ImageView albumIv     = (ImageView)rootView.findViewById(R.id.track_image);

        if (mTrack != null) {
            artistNameTV.setText(mTrack.getArtistName());
            albumNameTV.setText(mTrack.getAlbumName());
            trackNameTV.setText(mTrack.getTrackName());

            String imageUrl = mTrack.getAlbumImageUrl();
            if ((imageUrl != null) && (imageUrl.length() > 0)) {
                Picasso.with(getActivity())
                        .load(imageUrl)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            } else {
                // Image not found
                Picasso.with(getActivity())
                        .load(R.drawable.no_image_available)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            }
        } else {
            artistNameTV.setText("???");
            albumNameTV.setText("???");
            trackNameTV.setText("???");
            Picasso.with(getActivity())
                    .load(R.drawable.no_image_available)
                    .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                    .centerCrop()
                    .into(albumIv);
        }

        mDurationTV = (TextView)rootView.findViewById(R.id.track_duration_textview);
        mPlayPauseButton = (Button)rootView.findViewById(R.id.play_pause_button);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bind to service
        Intent intent = new Intent(getActivity(), PlayerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mServiceBound) {
            getActivity().unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    private void playTrack() {
        if (mServiceBound) {
            mDuration = mBoundService.play(mTrack.getPreviewUrl());
            if (mDuration > 0) {
                mPlayPauseButton.setText("PAUSE");
            }
            mDurationTV.setText(Integer.toString(mDuration));
        } else {
            Log.e(LOG_TAG, "Unable to play track - service not bound");
        }
    }

    private void pauseTrack() {
        if (mServiceBound) {
            mBoundService.pause();
            mPlayPauseButton.setText("PLAY");
        } else {
            Log.e(LOG_TAG, "Unable to pause track - service not bound");
        }
    }

    private void togglePlayPause() {
        if (mPlayPauseButton.getText().equals("PLAY")) {
            playTrack();
        } else {
            pauseTrack();
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            Log.d(LOG_TAG, "PlayerService disconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayerBinder playerBinder = (PlayerService.PlayerBinder)service;
            mBoundService = playerBinder.getService();
            mServiceBound = true;
            Log.d(LOG_TAG, "PlayerService connected");

            // Start playback after bind has completed
            playTrack();
        }
    };
}
