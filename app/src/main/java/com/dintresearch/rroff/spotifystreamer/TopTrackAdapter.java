/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/23/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Adapter class for managing TopTrack data within the UI.
 */
public class TopTrackAdapter extends ArrayAdapter<TopTrack> {

    Context mContext;

    /**
     * Constructor for adapter.
     *
     * @param context Current context
     */
    public TopTrackAdapter(Context context) {
        super(context, R.layout.list_item_tracks);
        mContext = context;
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
            v = vi.inflate(R.layout.list_item_tracks, null);
        }

        // Load data from array into view fields
        if (position < getCount()) {
            TextView trackNameTV = (TextView)v.findViewById(R.id.list_item_track_name_textview);
            trackNameTV.setText(getItem(position).getTrackName());

            TextView albumNameTV = (TextView)v.findViewById(R.id.list_item_track_album_textview);
            albumNameTV.setText(getItem(position).getAlbumName());
        }

        return v;
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
