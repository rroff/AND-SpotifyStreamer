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
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dintresearch.rroff.spotifystreamer.service.PlayerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Contains common routines for fragment & dialog player UIs.
 */
public class PlayerHelper {

    public static final String TRACK_ARRAY = "tracks";
    public static final String TRACK_POSITION = "position";

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerHelper.class.getName();

    private static final int SECONDS_PER_MINUTE = 60;

    private static final int IMAGE_HEIGHT = 400;
    private static final int IMAGE_WIDTH = 400;

    private static final int STATUS_POLL_WAIT_MS = 250;

    /**
     * Inter-Thread Communication (ITC) used to pass data from play status thread to main UI thread.
     */
    private EventBus mEventBus = EventBus.getDefault();

    /**
     * Cognizant context, either PlayerDialog or PlayerActivity.
     */
    private Context mContext;

    /**
     * View for UI.
     */
    private View mView;

    /**
     * PlayerService binding.
     */
    private PlayerService mBoundService;

    /**
     * PlayerService binding flag.
     */
    private boolean mServiceBound = false;

    /**
     * Track playlist.
     */
    private ArrayList<Track> mTrackPlaylist;

    /**
     * Playback position.
     */
    private int mPlaylistPosition;

    /**
     * Duration UI element.
     */
    private TextView mDurationTV;

    /**
     * Playback progress UI element.
     */
    private SeekBar mTrackSB;

    /**
     * State-changing button:  Play/Pause
     */
    private ImageButton mPlayPauseButton;

    /**
     * Constructor.
     *
     * @param view View corresponding to UI
     * @param context Context corresponding to UI
     * @param trackPlaylist Tracks to play
     * @param playlistPosition Position in playlist to start playback
     */
    public PlayerHelper(View view, Context context, ArrayList<Track> trackPlaylist,
                        int playlistPosition) {
        mView = view;
        mContext = context;
        mTrackPlaylist = trackPlaylist;
        mPlaylistPosition = playlistPosition;

        mDurationTV = (TextView)mView.findViewById(R.id.track_duration_textview);
        mTrackSB = (SeekBar)mView.findViewById(R.id.track_seekbar);

        mPlayPauseButton = (ImageButton)mView.findViewById(R.id.play_pause_button);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        ImageButton prevTrackButton = (ImageButton)mView.findViewById(R.id.prev_track_button);
        prevTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTrack(-1);
            }
        });

        ImageButton nextTrackButton = (ImageButton)mView.findViewById(R.id.next_track_button);
        nextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTrack(1);
            }
        });

        mEventBus.register(this);

        updatePlayerUi(mPlaylistPosition);
    }

    /**
     * Populates UI fields.
     *
     * @param position Track number within playlist
     */
    private void updatePlayerUi(int position) {
        TextView artistNameTV = (TextView)mView.findViewById(R.id.artist_name_textview);
        TextView albumNameTV  = (TextView)mView.findViewById(R.id.album_name_textview);
        TextView trackNameTV  = (TextView)mView.findViewById(R.id.track_name_textview);
        ImageView albumIv     = (ImageView)mView.findViewById(R.id.track_image);

        if ((mTrackPlaylist != null) && (mPlaylistPosition < mTrackPlaylist.size())) {
            Track track = mTrackPlaylist.get(mPlaylistPosition);

            artistNameTV.setText(track.getArtistName());
            albumNameTV.setText(track.getAlbumName());
            trackNameTV.setText(track.getTrackName());

            String imageUrl = track.getAlbumImageUrl();
            if ((imageUrl != null) && (imageUrl.length() > 0)) {
                Picasso.with(mContext)
                        .load(imageUrl)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            } else {
                // Image not found
                Picasso.with(mContext)
                        .load(R.drawable.no_image_available)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            }
        } else {
            artistNameTV.setText("???");
            albumNameTV.setText("???");
            trackNameTV.setText("???");

            Picasso.with(mContext)
                    .load(R.drawable.no_image_available)
                    .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                    .centerCrop()
                    .into(albumIv);
        }

        mDurationTV.setText("0:00");
        mTrackSB.setProgress(0);
    }

    /**
     * Changes track to play in playlist.
     *
     * @param requestedOffset Offset of current position to play
     */
    private void changeTrack(int requestedOffset) {
        int position = mPlaylistPosition + requestedOffset;
        if ((position >= 0) && (position < mTrackPlaylist.size())) {
            mPlaylistPosition = position;
            updatePlayerUi(mPlaylistPosition);
            playTrack();
        }
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
        if (mServiceBound) {
            if ((mTrackPlaylist != null) && (mPlaylistPosition < mTrackPlaylist.size())) {
                mBoundService.play(mTrackPlaylist.get(mPlaylistPosition).getPreviewUrl());
                mPlayPauseButton.setImageResource(R.drawable.audio_pause);
            } else {
                Log.e(LOG_TAG, "Unable to play track - no track specified");
            }
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
            mPlayPauseButton.setImageResource(R.drawable.audio_play);
        } else {
            Log.e(LOG_TAG, "Unable to pause track - service not bound");
        }
    }

    /**
     * Handles the button toggle between play & pause.
     */
    private void togglePlayPause() {
        if (mServiceBound) {
            if (!mBoundService.isPlaying()) {
                playTrack();
            } else {
                pauseTrack();
            }
        }
    }

    /**
     * Handler for receiving messages from status thread.
     *
     * @param event Status message
     */
    public void onEventMainThread(StatusEvent event) {

        // Update Duration Text
        int minutes = event.getTrackDurationSeconds()/SECONDS_PER_MINUTE;
        int remainingSeconds = event.getTrackDurationSeconds() - (minutes*SECONDS_PER_MINUTE);
        String durationStr = String.format("%d:%02d", minutes, remainingSeconds);
        mDurationTV.setText(durationStr);

        // Update Seek (Progress) Bar
        mTrackSB.setMax(event.getTrackDurationSeconds());
        mTrackSB.setProgress(event.getPlayPositionSeconds());
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

            // Start statusing thread
            new PlayerStatusTask().execute();
        }
    };

    /**
     * Container class holding data being passed back from status thread.
     */
    private class StatusEvent {
        private int mTrackDurationSeconds;
        private int mPlayPositionSeconds;

        public StatusEvent(int trackDurationSeconds, int playPositionSeconds) {
            mTrackDurationSeconds = trackDurationSeconds;
            mPlayPositionSeconds = playPositionSeconds;
        }

        public int getTrackDurationSeconds() {
            return mTrackDurationSeconds;
        }

        public int getPlayPositionSeconds() {
            return mPlayPositionSeconds;
        }
    }

    /**
     * Thread that manages gathering and providing playback status.
     */
    private class PlayerStatusTask extends AsyncTask<Void, Void, Void> {

        /**
         * Default constructor.
         */
        public PlayerStatusTask() {
        }

        /**
         * Statusing thread.
         *
         * @param params Not used
         * @return Always null
         */
        @Override
        protected Void doInBackground(Void... params) {

            boolean exitFlag = false;

            // Thread runs as long as service is bound to the player
            while (!exitFlag && mServiceBound) {
                StatusEvent event = new StatusEvent(
                        mBoundService.getDurationInSeconds(),
                        mBoundService.getPlayPositonInSeconds());
                EventBus.getDefault().post(event);

                try {
                    Thread.sleep(STATUS_POLL_WAIT_MS);
                } catch (InterruptedException e) {
                    exitFlag = true;
                }
            }

            return null;
        }
    }
}
