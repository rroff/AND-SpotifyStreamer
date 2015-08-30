/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 8/23/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dintresearch.rroff.spotifystreamer.service.PlayerService;
import com.dintresearch.rroff.spotifystreamer.service.PlayerStatus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerDialogFragment extends DialogFragment {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerDialogFragment.class.getName();

    /**
     * Argument key for track array data.
     */
    public static final String TRACK_ARRAY_KEY = "tracks";

    /**
     * Argument key for current track position.
     */
    public static final String TRACK_POSITION_KEY = "position";

    /**
     * Default time string, used in UI until real data is available from service.
     */
    public static final String DEFAULT_TIME_STR = "0:00";

    /**
     * String to use if data is unknown or not available.
     */
    public static final String UNKNOWN_STR = "???";

    /**
     * Number of seconds per minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * Height of album image.
     */
    private static final int IMAGE_HEIGHT = 400;

    /**
     * Width of album image.
     */
    private static final int IMAGE_WIDTH = 400;

    /**
     * Track playlist.
     */
    private ArrayList<Track> mTrackPlaylist;

    /**
     * Playback position.
     */
    private int mPlaylistPosition;

    /**
     * PlayerService binding.
     */
    private PlayerService mBoundService;

    /**
     * PlayerService binding flag.
     */
    private boolean mServiceBound = false;

    private StatusReceiver mStatusReceiver;

    /**
     * Artist Name UI element.
     */
    TextView mArtistNameTV;

    /**
     * Album Name UI element.
     */
    TextView mAlbumNameTV;

    /**
     * Track Name UI element.
     */
    TextView mTrackNameTV;

    /**
     * Album Image UI element.
     */
    ImageView mAlbumIv;

    /**
     * Duration UI element.
     */
    private TextView mDurationTV;

    /**
     * Elapsed Playtime UI element.
     */
    private TextView mElapsedTV;

    /**
     * Playback Progress UI element.
     */
    private SeekBar mTrackSB;

    /**
     * Indicates if the seekbar is being actively used.
     */
    private boolean mSeekBarTouchInProgress;

    /**
     * State-changing button:  Play/Pause
     */
    private ImageButton mPlayPauseButton;

    /**
     * Previous Track button.
     */
    private ImageButton mPrevTrackButton;

    /**
     * Next Track button.
     */
    private ImageButton mNextTrackButton;

    /**
     * Called when the DialogFragment is created.
     *
     * @param savedInstanceState If non-null, this DialogFragment is being re-constructed from this
     *                           previous saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Track info is passed in via arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTrackPlaylist = arguments.getParcelableArrayList(TRACK_ARRAY_KEY);
            mPlaylistPosition = arguments.getInt(TRACK_POSITION_KEY);
        } else {
            Log.e(LOG_TAG, "No tracks provided");
            mTrackPlaylist = null;
            mPlaylistPosition = 0;
        }

        // Restore track position when rotation occurs
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRACK_POSITION_KEY)) {
                mPlaylistPosition = savedInstanceState.getInt(TRACK_POSITION_KEY);
            }
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog);
    }

    /**
     * Creates and returns the view hierarchy associated with this DialogFragment.
     *
     * @param inflater LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view to which the fragment's UI should be
     *                  attached
     * @param savedInstanceState If non-null, this fragment is being re-constructed from this
     *                           previous saved state
     *
     * @return View for the Player DialogFragment UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        // Store UI references
        mArtistNameTV    = (TextView)view.findViewById(R.id.artist_name_textview);
        mAlbumNameTV     = (TextView)view.findViewById(R.id.album_name_textview);
        mTrackNameTV     = (TextView)view.findViewById(R.id.track_name_textview);
        mAlbumIv         = (ImageView)view.findViewById(R.id.track_image);
        mDurationTV      = (TextView)view.findViewById(R.id.track_duration_textview);
        mElapsedTV       = (TextView)view.findViewById(R.id.track_elapsed_textview);
        mPlayPauseButton = (ImageButton)view.findViewById(R.id.play_pause_button);
        mPrevTrackButton = (ImageButton)view.findViewById(R.id.prev_track_button);
        mNextTrackButton = (ImageButton)view.findViewById(R.id.next_track_button);
        mTrackSB         = (SeekBar)view.findViewById(R.id.track_seekbar);

        // Attach button listeners
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        mPrevTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTrack(-1);
            }
        });
        mNextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTrack(1);
            }
        });

        // Attach seekbar listener
        mTrackSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // NOTE: SeekBar is represented in seconds
                mElapsedTV.setText(toMinutesSecondsString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekBarTouchInProgress = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mServiceBound) {
                    // NOTE: SeekBar is represented in seconds
                    mBoundService.seek(seekBar.getProgress());
                }
                mSeekBarTouchInProgress = false;
            }
        });

        mSeekBarTouchInProgress = false;
        updatePlayerUi();

        return view;
    }

    /**
     * Runs when DialogFragment is started.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Runs when DialogFragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();

        // Unregister receiver
        getActivity().unregisterReceiver(mStatusReceiver);
        Log.d(LOG_TAG, "StatusReceiver unregistered");

        // Unbind service
        unbindService();
    }

    /**
     * Runs when DialogFragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Register status receiver
        IntentFilter filter = new IntentFilter(PlayerService.STATUS_INTENT_FILTER_TAG);
        mStatusReceiver = new StatusReceiver();
        getActivity().registerReceiver(mStatusReceiver, filter);
        Log.d(LOG_TAG, "StatusReceiver registered");

        // Bind to service
        Intent intent = new Intent(getActivity(), PlayerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Saves instance state for later restoration, if needed.
     *
     * @param outState Outbound state bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Provide current track position
        outState.putInt(TRACK_POSITION_KEY, mPlaylistPosition);
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
            updatePlayerUi();
            playTrack();
        }
    }

    /**
     * Pauses audio track using service call.
     */
    private void pauseTrack() {
        if (mServiceBound) {
            mBoundService.pause();
        } else {
            Log.e(LOG_TAG, "Unable to pause track - service not bound");
        }
    }

    /**
     * Plays audio track using service call.
     */
    private void playTrack() {
        if (mServiceBound) {
            if ((mTrackPlaylist != null) && (mPlaylistPosition < mTrackPlaylist.size())) {
                mBoundService.play(mTrackPlaylist.get(mPlaylistPosition).getPreviewUrl());
            } else {
                Log.e(LOG_TAG, "Unable to play track - no track specified");
            }
        } else {
            Log.e(LOG_TAG, "Unable to play track - service not bound");
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
     * Converts seconds to a minutes-seconds string.
     *
     * @param seconds Number of seconds
     *
     * @return Minutes-seconds string, represented with a colon delimeter.
     */
    private String toMinutesSecondsString(int seconds) {
        int minutes = seconds/SECONDS_PER_MINUTE;
        int remainingSeconds = seconds - (minutes*SECONDS_PER_MINUTE);

        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    /**
     * Unbinds player service.
     */
    private void unbindService() {
        if (mServiceBound) {
            getActivity().unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    /**
     * Populates UI fields.
     */
    private void updatePlayerUi() {
        Context context = getActivity();

        if ((mTrackPlaylist != null) && (mPlaylistPosition < mTrackPlaylist.size())) {
            Track track = mTrackPlaylist.get(mPlaylistPosition);

            mArtistNameTV.setText(track.getArtistName());
            mAlbumNameTV.setText(track.getAlbumName());
            mTrackNameTV.setText(track.getTrackName());

            String imageUrl = track.getAlbumImageUrl();
            if ((imageUrl != null) && (imageUrl.length() > 0)) {
                Picasso.with(context)
                        .load(imageUrl)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(mAlbumIv);
            } else {
                // Image not found
                Picasso.with(context)
                        .load(R.drawable.no_image_available)
                        .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(mAlbumIv);
            }
        } else {
            mArtistNameTV.setText(UNKNOWN_STR);
            mAlbumNameTV.setText(UNKNOWN_STR);
            mTrackNameTV.setText(UNKNOWN_STR);

            Picasso.with(context)
                    .load(R.drawable.no_image_available)
                    .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                    .centerCrop()
                    .into(mAlbumIv);
        }

        mElapsedTV.setText(DEFAULT_TIME_STR);
        mDurationTV.setText(DEFAULT_TIME_STR);
        mTrackSB.setProgress(0);
    }

    /**
     * Manages connection to player service.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            Log.d(LOG_TAG, "Disconnected (" + name.getClassName() + ")");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Save server binding
            PlayerService.PlayerBinder playerBinder = (PlayerService.PlayerBinder)service;
            mBoundService = playerBinder.getService();
            mServiceBound = true;
            Log.d(LOG_TAG, "Connected (" + name.getClassName() + ")");

            // Start playback after bind has completed if not paused or stopped
            if (!mBoundService.isPaused() && !mBoundService.isStopped()) {
                playTrack();
            }
        }
    };

    /**
     * BroadcastReceiver class for processing status messages from the PlayerService.
     */
    private class StatusReceiver extends BroadcastReceiver {

        public StatusReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(PlayerStatus.class.getName())) {
                PlayerStatus status = intent.getParcelableExtra(PlayerStatus.class.getName());

                // Update Duration & Elapsed Time Text
                mDurationTV.setText(toMinutesSecondsString(status.getTrackDurationSeconds()));

                // Update Seek (Progress) Bar
                mTrackSB.setMax(status.getTrackDurationSeconds());
                mTrackSB.setProgress(status.getPlayPositionSeconds());

                // Update PlayPause Button State
                if (status.isPlaying()) {
                    mPlayPauseButton.setImageResource(R.drawable.audio_pause);
                } else {
                    mPlayPauseButton.setImageResource(R.drawable.audio_play);
                }
            }
        }
    }
}
