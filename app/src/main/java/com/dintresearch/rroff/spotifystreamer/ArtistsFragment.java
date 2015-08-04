/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/12/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsFragment extends Fragment {

    private static final String LOG_TAG = ArtistsFragment.class.getName();

    /**
     * Artist search string used for Spotify lookup.
     */
    private EditText mArtistSearchTxt;

    /**
     * Adapter for managing artist data.
     */
    private ArtistAdapter mArtistAdapter;

    /**
     * Position of selected artist.
     */
    private int mSelectedPosition;

    /**
     * Constructor
     */
    public ArtistsFragment() {
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view to which the fragment's UI should be
     *                  attached
     * @param savedInstanceState If non-null, this fragment is being re-constructed from this
     *                           previous saved state
     *
     * @return View for the fragment UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        mArtistAdapter = new ArtistAdapter(getActivity());

        // Restore saved data
        if (savedInstanceState != null) {
            ArrayList<Artist> artists = savedInstanceState
                                        .getParcelableArrayList(ArtistAdapter.class.getName());
            mArtistAdapter.addAll(artists);
        }

        // Establish listener for artist search
        mArtistSearchTxt = (EditText)rootView.findViewById(R.id.artist_search);
        mArtistSearchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    closeSoftKeyboard(v);

                    // Execute the search
                    searchForArtists();

                    handled = true;
                }
                return handled;
            }
        });

        // Setup ListView for artist search results
        final ListView listView = (ListView)rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;

                // Use activity callback for selected artist
                Activity activity = getActivity();
                if (activity instanceof Callback) {
                    ((Callback)activity).onArtistItemSelected(mArtistAdapter.getItem(position));
                }
            }
        });

        return rootView;
    }

    /**
     * Runs when fragment is started.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Saves instance state for later restoration, if needed.
     *
     * @param outState Outbound state bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Preserve artist search data
        outState.putParcelableArrayList(ArtistAdapter.class.getName(),
                mArtistAdapter.getArtistArrayList());
    }

    /**
     * Close soft keyboard
     *
     * @param v Current TextView
     */
    private void closeSoftKeyboard(TextView v) {
        InputMethodManager imm = (InputMethodManager)v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Execute artist search task.
     */
    private void searchForArtists() {
        String artistSearchStr = mArtistSearchTxt.getText().toString();
        new SearchArtistsTask(mArtistAdapter).execute(artistSearchStr);
    }

    /**
     * Callback interface for activities to implement.  Allows activity to
     * be notified of artist selection.
     */
    public interface Callback {
        /**
         * TopTracksFragment Callback for when an item has been selected.
         */
        public void onArtistItemSelected(Artist selectedArtist);
    }
}
