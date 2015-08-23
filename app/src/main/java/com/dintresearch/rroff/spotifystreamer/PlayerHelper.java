/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 8/23/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dintresearch.rroff.spotifystreamer.service.PlayerService;
import com.squareup.picasso.Picasso;

/**
 * Contains common routines for fragment & dialog player UIs.
 */
public class PlayerHelper {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerHelper.class.getName();

    private static final int IMAGE_HEIGHT = 400;
    private static final int IMAGE_WIDTH = 400;

    private Context mContext;

    private PlayerService mBoundService;
    private boolean mServiceBound = false;

    private Track mTrack;

    private int mDuration;

    private Button mPlayPauseButton;
    private TextView mDurationTV;

    /**
     * Constructor.  Used to populate UI fields.
     *
     * @param rootView View corresponding to UI
     * @param context Context corresponding to UI
     * @param track Track to play
     */
    public PlayerHelper(View rootView, Context context, Track track) {
        mContext = context;
        mTrack = track;

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
                Picasso.with(context)
                        .load(imageUrl)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            } else {
                // Image not found
                Picasso.with(context)
                        .load(R.drawable.no_image_available)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            }
        } else {
            artistNameTV.setText("???");
            albumNameTV.setText("???");
            trackNameTV.setText("???");
            Picasso.with(context)
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
    }

    /**
     * Starts player service (if needed) and creates binding.
     */
    public void start() {
        // Bind to service
        Intent intent = new Intent(mContext, PlayerService.class);
        mContext.startService(intent);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds player service.
     */
    public void unbindService() {
        if (mServiceBound) {
            mContext.unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    /**
     * Plays audio track using service call.
     */
    private void playTrack() {
        if (mServiceBound && (mTrack != null)) {
            mDuration = mBoundService.play(mTrack.getPreviewUrl());
            if (mDuration > 0) {
                mPlayPauseButton.setText("PAUSE");
            }
            mDurationTV.setText(Integer.toString(mDuration));
        } else {
            Log.e(LOG_TAG, "Unable to play track - service not bound");
        }
    }

    /**
     * Pauses audio track using service call.
     */
    private void pauseTrack() {
        if (mServiceBound) {
            mBoundService.pause();
            mPlayPauseButton.setText("PLAY");
        } else {
            Log.e(LOG_TAG, "Unable to pause track - service not bound");
        }
    }

    /**
     * Handles the button toggle between play & pause.
     */
    private void togglePlayPause() {
        if (mPlayPauseButton.getText().equals("PLAY")) {
            playTrack();
        } else {
            pauseTrack();
        }
    }

    /**
     * Manages connection to player service.
     */
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
