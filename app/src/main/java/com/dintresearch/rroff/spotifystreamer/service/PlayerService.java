/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 8/20/2015
 */
package com.dintresearch.rroff.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class PlayerService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String LOG_TAG = PlayerService.class.getName();

    private static final int MS_PER_SECOND = 1000;

    private IBinder mBinder = new PlayerBinder();

    private MediaPlayer mPlayer;

    private String mTrackPlaying;

    private int mDurationInSeconds;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mDurationInSeconds = 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    /**
     * Pauses track playback.
     */
    public void pause() {
        if ((mPlayer != null) && (mPlayer.isPlaying())) {
            mPlayer.pause();
        }
    }

    /***
     * Plays a specified audio track, or resumes playback if paused.
     *
     * @param trackUrl URL of track to play
     */
    public void play(String trackUrl) {
        if (mPlayer != null) {

            // If track is different, play
            if (!trackUrl.equals(mTrackPlaying)) {
                Uri trackUri = Uri.parse(trackUrl);
                mPlayer.reset();
                try {
                    mPlayer.setDataSource(this, trackUri);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error starting playback", e);
                }
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(this);

                mTrackPlaying = trackUrl;
            } else {

                // Track has not changed
                if (!mPlayer.isPlaying()) {
                    // Track not playing: Restart if at end, Resume otherwise
                    if (mPlayer.getCurrentPosition() >= mPlayer.getDuration()) {
                        mPlayer.seekTo(0);
                        mPlayer.start();
                    } else {
                        mPlayer.start();
                    }
                }
            }
        }
    }

    public int getDurationInSeconds() {
        return mDurationInSeconds;
    }

    public int getPlayPositonInSeconds() {
        int position = 0;
        if (mPlayer != null) {
            position = mPlayer.getCurrentPosition()/MS_PER_SECOND;
        }

        return position;
    }

    /**
     * Stops audio playback.
     */
    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public boolean isPlaying() {
        boolean playFlag = false;

        if (mPlayer != null) {
            playFlag = mPlayer.isPlaying();
        }

        return playFlag;
    }

    /**
     * Starts playback once audio has been buffered.
     *
     * @param mp Prepared MediaPlayer
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mDurationInSeconds = mp.getDuration()/MS_PER_SECOND;
        mp.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "Playback complete");
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
