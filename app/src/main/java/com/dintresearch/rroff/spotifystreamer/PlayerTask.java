package com.dintresearch.rroff.spotifystreamer;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class PlayerTask extends AsyncTask<Void, Void, Void> {

    /**
     * Name of class, used for logging.
     */
    private static final String LOG_TAG = PlayerTask.class.getName();

    private MediaPlayer mPlayer;

    public PlayerTask(MediaPlayer player) {
        super();
        mPlayer = player;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if ((mPlayer != null) && (!mPlayer.isPlaying())) {
            try {
                // TODO: prepare() generates "Should have subtitle controller already set"
                // within MediaPlayer.  No info on how to eliminate this error.
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error starting playback", e);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
