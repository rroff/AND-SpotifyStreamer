package com.dintresearch.rroff.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by rroff on 6/23/2015.
 */
public class TopTrackAdapter extends ArrayAdapter<TopTrack> {

    public TopTrackAdapter(Context context) {
        super(context, R.layout.list_item_tracks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_tracks, null);
        }

        if (position < getCount()) {
            TextView trackNameTV = (TextView)v.findViewById(R.id.list_item_track_name_textview);
            trackNameTV.setText(getItem(position).getTrackName());

            TextView albumNameTV = (TextView)v.findViewById(R.id.list_item_track_album_textview);
            albumNameTV.setText(getItem(position).getAlbumName());
        }

        return v;
    }
}
