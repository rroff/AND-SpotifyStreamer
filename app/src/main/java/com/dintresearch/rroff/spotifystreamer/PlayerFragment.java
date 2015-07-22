/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 7/19/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;


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

    private MediaPlayer mPlayer;

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

        mPlayer = new MediaPlayer();

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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer != null) {
            Uri builtUri = Uri.parse(mTrack.getPreviewUrl());
            try {
                mPlayer.setDataSource(getActivity(), builtUri);
                new PlayerTask(mPlayer).execute();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error setting data source", e);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((mPlayer != null) && (mPlayer.isPlaying())) {
            mPlayer.stop();
        }
    }
}
