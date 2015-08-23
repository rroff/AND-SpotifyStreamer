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

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private static final String LOG_TAG = PlayerService.class.getName();

    private static final int MS_PER_SECOND = 1000;

    private IBinder mBinder = new PlayerBinder();

    private MediaPlayer mPlayer;

    private String mTrackPlaying;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
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

    public void fastForward() {

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
     * @return Duration of track, in seconds
     */
    public int play(String trackUrl) {
        int duration = 0;
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

                // TODO: Duration returning 0, maybe add callback for setting duration?
                // duration = mPlayer.getDuration()/MS_PER_SECOND;
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
                duration = mPlayer.getDuration()/MS_PER_SECOND;
            }
        }

        return duration;
    }

    public void rewind() {

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
    }

    public class PlayerBinder extends Binder {

        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
