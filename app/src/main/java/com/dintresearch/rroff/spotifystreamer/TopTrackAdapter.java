/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/23/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Adapter class for managing TopTrack data within the UI.
 */
public class TopTrackAdapter extends ArrayAdapter<Track> {

    private static final int IMAGE_HEIGHT = 200;
    private static final int IMAGE_WIDTH = 200;

    private Context mContext;

    private String mCountryCode;

    /**
     * Constructor for adapter.
     *
     * @param context Current context
     */
    public TopTrackAdapter(Context context) {
        super(context, R.layout.list_item_tracks);
        mContext = context;
        mCountryCode = PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(context.getString(R.string.pref_country_key),
                                context.getString(R.string.pref_country_default));
    }

    /**
     * Retrieves new/updated data into view.
     *
     * @param position Specified item within array to load into view
     * @param convertView View to be modified
     * @param parent Group of view to be modified
     *
     * @return Modified view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // Create view if it does not exist
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_tracks, parent, false);
        }

        // Load data from array into view fields
        if (position < getCount()) {
            TextView trackNameTV = (TextView)v.findViewById(R.id.list_item_track_name_textview);
            trackNameTV.setText(getItem(position).getTrackName());

            TextView albumNameTV = (TextView)v.findViewById(R.id.list_item_track_album_textview);
            albumNameTV.setText(getItem(position).getAlbumName());

            String imageUrl = getItem(position).getAlbumImageUrl();
            ImageView albumIv = (ImageView)v.findViewById(R.id.list_item_track_image);
            if ((imageUrl != null) && (imageUrl.length() > 0)) {
                Glide.with(mContext)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            } else {
                // Image not found
                Glide.with(mContext)
                        .load(R.drawable.no_image_available)
                        .override(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .centerCrop()
                        .into(albumIv);
            }
        }

        return v;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        this.mCountryCode = countryCode;
    }

    /**
     * Returns the top track data within the adapter.
     *
     * @return ArrayList of top track data
     */
    public ArrayList<Track> getTrackArrayList() {
        ArrayList<Track> tracks = new ArrayList<>();
        for (int ii=0; ii<getCount(); ++ii) {
            tracks.add(getItem(ii));
        }
        return tracks;
    }

    /**
     * Displays a toast message in the app.
     *
     * @param resId Resource id of string resource
     */
    public void showToast(int resId) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(mContext, resId, duration);
        toast.show();
    }
}
