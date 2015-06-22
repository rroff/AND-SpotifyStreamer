package com.dintresearch.rroff.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsFragment extends Fragment {

    private EditText mArtistSearchTxt;

    private ArrayAdapter<String> mArtistAdapter;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.spotifyfragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        // Establish listener for artist search
        mArtistSearchTxt = (EditText)rootView.findViewById(R.id.artist_search);
        mArtistSearchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // Close the soft keyboard
                    InputMethodManager imm = (InputMethodManager)v.getContext()
                                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    searchForArtists();
                    handled = true;
                }
                return handled;
            }
        });

        // Setup ListView for artist search results
        ArrayList<String> artistStrings = new ArrayList<>();
        mArtistAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_artists,
                R.id.list_item_artists_textview,
                artistStrings);

        final ListView listView = (ListView)rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), TopTracksActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, mArtistAdapter.getItem(position));
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            searchForArtists();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchForArtists() {
        String artistSearchStr = mArtistSearchTxt.getText().toString();
        new SearchArtistsTask(mArtistAdapter).execute(artistSearchStr);
    }
}