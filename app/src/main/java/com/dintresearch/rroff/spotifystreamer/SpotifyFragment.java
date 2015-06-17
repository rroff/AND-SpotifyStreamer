package com.dintresearch.rroff.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyFragment extends Fragment {

    private ArrayAdapter<String> mArtistAdapter;

    public SpotifyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_spotify, container, false);

        ArrayList<String> artistStrings = new ArrayList<>();
        artistStrings.add("No data - Click Refresh to Load");

        mArtistAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_artists,
                R.id.list_item_artists_textview,
                artistStrings);

        final ListView listView = (ListView)rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistAdapter);

        return rootView;
    }
}
