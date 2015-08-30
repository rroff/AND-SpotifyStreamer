/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 8/30/2015
 */
package com.dintresearch.rroff.spotifystreamer.service;

import android.content.Intent;
import android.os.AsyncTask;

/**
 * Thread that manages gathering and providing playback status.
 */
public class PlayerStatusTask extends AsyncTask<Void, Void, Void> {

    /**
     * Wait time between polls during playback service statusing.
     */
    private static final int STATUS_POLL_WAIT_MS = 250;

    /**
     * Service owner of task.
     */
    private PlayerService mService;

    /**
     * Flag to be set when thread can be terminated.
     */
    private boolean mExitFlag;

    /**
     * Default constructor.
     */
    public PlayerStatusTask(PlayerService service) {
        mService = service;
        mExitFlag = false;
    }

    /**
     * Indicates that thread can terminate.
     */
    public void stopStatusing() {
        mExitFlag = true;
    }

    /**
     * Statusing thread.
     *
     * @param params Not used
     * @return Always null
     */
    @Override
    protected Void doInBackground(Void... params) {

        // Thread runs as long as service is bound to the player
        while (!mExitFlag) {
            if (mService != null) {
                // Send player status as broadcast message
                PlayerStatus status = new PlayerStatus(
                        mService.getDurationInSeconds(),
                        mService.getPlayPositonInSeconds(),
                        mService.isPlaying());

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setAction(PlayerService.STATUS_INTENT_FILTER_TAG);
                intent.putExtra(PlayerStatus.class.getName(), status);
                mService.sendBroadcast(intent);
            }

            try {
                Thread.sleep(STATUS_POLL_WAIT_MS);
            } catch (InterruptedException e) {
                mExitFlag = true;
            }
        }

        return null;
    }
}