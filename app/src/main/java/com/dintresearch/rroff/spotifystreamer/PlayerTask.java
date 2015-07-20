package com.dintresearch.rroff.spotifystreamer;

import android.media.MediaPlayer;
import android.os.AsyncTask;

public class PlayerTask extends AsyncTask<String, Void, Void> {

    private MediaPlayer mPlayer;

    public PlayerTask(MediaPlayer player) {
        super();
        mPlayer = player;
    }

    @Override
    protected Void doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
