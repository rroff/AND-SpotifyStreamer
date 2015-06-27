/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/17/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchArtistsTask extends AsyncTask<String, Void, Artist[]> {

    /**
     * Class name for logging.
     */
    private static final String LOG_TAG = SearchArtistsTask.class.getSimpleName();

    /**
     * Adapter for ingesting artist data.
     */
    private ArtistAdapter mArtistAdapter;

    /**
     * Parameterized constructor.
     *
     * @param artistAdapter Adapter for loading search results into the main thread
     */
    public SearchArtistsTask(ArtistAdapter artistAdapter) {
        super();
        mArtistAdapter = artistAdapter;
    }

    /**
     * Task which runs when the execute() method is performed.
     *
     * @param params Array of Strings.  params[0] is the artist name.
     *
     * @return Array of Artist data, after retrieval from Spotify
     */
    protected Artist[] doInBackground(String... params) {

        Artist artists[] = null;

        if ((params.length > 0) && (params[0].length() > 0)) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String artistJsonStr = null;

            try {
                // Construct the URL for the Spotify Search query
                // Ref: https://developer.spotify.com/web-api/search-item/
                final String FORECAST_BASE_URL = "https://api.spotify.com/v1/search";
                final String QUERY_PARAM = "q";
                final String SEARCH_TYPE_PARAM = "type";
                final String MAX_RESULTS_PARAM = "limit";
                final String RESULTS_OFFSET_PARAM = "offset";

                String searchType = "artist";
                int maxResults    = 50;
                int resultsOffset = 0;

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(SEARCH_TYPE_PARAM, searchType)
                        .appendQueryParameter(MAX_RESULTS_PARAM, Integer.toString(maxResults))
                        .appendQueryParameter(RESULTS_OFFSET_PARAM, Integer.toString(resultsOffset))
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, builtUri.toString());

                // Send request to Spotify
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Store results from search request
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Format in the event the string is needed for debugging
                    buffer.append(line);
                    buffer.append("\n");
                }

                if (buffer.length() > 0) {
                    artistJsonStr = buffer.toString();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // Skip processing
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // Extract JSON data into return array
            try {
                artists = getArtistDataFromJson(artistJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error ", e);
                return null;
            }
        }

        return artists;
    }

    /**
     * Runs on completion of async task.
     *
     * @param artists Array of Artist data produced by async task
     */
    @Override
    protected void onPostExecute(Artist[] artists) {
        mArtistAdapter.clear();

        if ((artists == null) || (artists.length == 0))  {
            mArtistAdapter.showToast(R.string.msg_no_artists_found);
        } else {
            mArtistAdapter.addAll(artists);
        }
    }

    /**
     * Extracts required data from artist search results.
     *
     * @param artistJsonStr JSON-formatted results from artist search
     *
     * @return Array of strings containing artist names
     *
     * @throws JSONException
     */
    private Artist[] getArtistDataFromJson(String artistJsonStr)
            throws JSONException {

        // JSON objects that need to be extracted
        final String SEARCH_ARTISTS   = "artists";
        final String SEARCH_ITEMS     = "items";
        final String SEARCH_NAME      = "name";
        final String SEARCH_ID        = "id";
        final String SEARCH_IMAGES    = "images";
        final String SEARCH_IMAGE_URL = "url";

        JSONObject searchJson = new JSONObject(artistJsonStr);
        JSONObject artistsJson = searchJson.getJSONObject(SEARCH_ARTISTS);
        JSONArray itemsArray = artistsJson.getJSONArray(SEARCH_ITEMS);

        Artist[] artists = new Artist[itemsArray.length()];

        for (int ii=0; ii < itemsArray.length(); ++ii) {
            JSONObject artistJSON = itemsArray.getJSONObject(ii);
            String name = artistJSON.getString(SEARCH_NAME);
            String id   = artistJSON.getString(SEARCH_ID);

            // Use first image, if any exist
            JSONArray images = artistJSON.getJSONArray(SEARCH_IMAGES);
            String imageUrl = "";
            if (images.length() > 0) {
                imageUrl =  images.getJSONObject(0).getString(SEARCH_IMAGE_URL);
            }

            artists[ii] = new Artist(name, id, imageUrl);
        }

        return artists;
    }
}
