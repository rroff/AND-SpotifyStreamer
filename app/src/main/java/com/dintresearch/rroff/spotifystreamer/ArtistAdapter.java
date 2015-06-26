/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/24/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Adapter class for managing Artist data within the UI.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    Context mContext;

    /**
     * Constructor for adapter.
     *
     * @param context Current context
     */
    public ArtistAdapter(Context context) {
        super(context, R.layout.list_item_artists);
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
            v = vi.inflate(R.layout.list_item_artists, parent, false);
        }

        // Load data from array into view fields
        if (position < getCount()) {
            TextView trackNameTv = (TextView)v.findViewById(R.id.list_item_artists_textview);
            trackNameTv.setText(getItem(position).getName());

            String imageUrl = getItem(position).getImageUrl();
            ImageView artistIv = (ImageView)v.findViewById(R.id.list_item_artists_image);
            if ((imageUrl != null) && (imageUrl.length() > 0)) {
                Picasso.with(mContext)
                        .load(imageUrl)
                        .resize(200,200)
                        .centerCrop()
                        .into(artistIv);
            } else {
                // Image not found
                Picasso.with(mContext)
                        .load(R.drawable.no_image_available)
                        .resize(200,200)
                        .centerCrop()
                        .into(artistIv);
            }
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
