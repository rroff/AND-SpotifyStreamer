/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/22/2015
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

/**
 * Task which executes Top Tracks query from Spotify.
 */
public class TopTracksTask extends AsyncTask<String, Void, Track[]> {

    /**
     * Class name for logging
     */
    private final static String LOG_TAG = TopTracksTask.class.getSimpleName();

    /**
     * Adapter for ingesting artist data
     */
    private TopTrackAdapter mTopTracksAdapter;

    /**
     * Country code used for top track retrieval
     */
    private String mCountryCode;

    /**
     * Parameterized constructor.
     *
     * @param topTracksAdapter Adapter for loading track results into the main thread
     */
    public TopTracksTask(TopTrackAdapter topTracksAdapter) {
        super();
        mTopTracksAdapter = topTracksAdapter;
    }

    /**
     * Task which runs when the execute() method is performed.
     *
     * @param params Array of Strings.
     *               params[0] is the artist ID.
     *               params[1] is the country code.
     *
     * @return Array of TopTrack data, after retrieval from Spotify
     */
    @Override
    protected Track[] doInBackground(String... params) {

        Track tracks[]      = null;

        if (  (params.length == 2)
           && (params[0].length() > 0)
           && (params[1].length() > 0)) {

            final String artistId = params[0];
            mCountryCode = params[1];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection    = null;
            BufferedReader    reader           = null;

            String topTracksJsonStr = null;

            try {
                final String BASE_ARTISTS_URL    = "https://api.spotify.com/v1/artists";
                final String TOP_TRACKS_ENDPOINT = "top-tracks";
                final String COUNTRY_PARAM       = "country";

                // Construct the URL for the Spotify Artist Top Tracks query
                // Ref: https://developer.spotify.com/web-api/get-artists-top-tracks/
                Uri builtUri = Uri.parse(BASE_ARTISTS_URL).buildUpon()
                        .appendPath(artistId)
                        .appendPath(TOP_TRACKS_ENDPOINT)
                        .appendQueryParameter(COUNTRY_PARAM, mCountryCode)
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
                    topTracksJsonStr = buffer.toString();
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
                tracks = getTopTracksDataFromJson(topTracksJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error ", e);
                return null;
            }
        }

        return tracks;
    }

    /**
     * Runs on completion of async task.
     *
     * @param tracks Array of Top Track data produced by async task
     */
    @Override
    protected void onPostExecute(Track[] tracks) {
        mTopTracksAdapter.setCountryCode(mCountryCode);
        mTopTracksAdapter.clear();

        if ((tracks == null) || (tracks.length == 0)) {
            mTopTracksAdapter.showToast(R.string.msg_no_tracks_found);
        } else {
            mTopTracksAdapter.addAll(tracks);
            if (tracks.length < 10) {
                mTopTracksAdapter.showToast(R.string.msg_less_than_ten_tracks_found);
            }
        }
    }

    /**
     * Constructs Track array from JSON data.
     *
     * @param topTracksJsonStr JSON track data from Spotify
     *
     * @return Array of Track data
     *
     * @throws JSONException
     */
    public static Track[] getTopTracksDataFromJson(String topTracksJsonStr)
            throws JSONException {

        // JSON objects that need to be extracted
        final String JSON_LABEL_TRACKS       = "tracks";
        final String TRACK_LABEL_NAME        = "name";
        final String TRACK_LABEL_ID          = "id";
        final String TRACK_LABEL_PREVIEW_URL = "preview_url";
        final String TRACK_LABEL_ALBUM       = "album";
        final String ALBUM_LABEL_NAME        = "name";
        final String ALBUM_LABEL_IMAGES      = "images";
        final String IMAGE_LABEL_URL         = "url";
        final String TRACK_LABEL_ARTISTS     = "artists";
        final String ARTIST_LABEL_NAME       = "name";

        JSONObject topTracksJson = new JSONObject(topTracksJsonStr);
        JSONArray tracksArray = topTracksJson.getJSONArray(JSON_LABEL_TRACKS);

        Track[] tracks = new Track[tracksArray.length()];

        for (int ii=0; ii < tracksArray.length(); ++ii) {
            JSONObject trackJson = tracksArray.getJSONObject(ii);
            String name          = trackJson.getString(TRACK_LABEL_NAME);
            String id            = trackJson.getString(TRACK_LABEL_ID);
            String previewUrl    = trackJson.getString(TRACK_LABEL_PREVIEW_URL);

            JSONObject albumJson = trackJson.getJSONObject(TRACK_LABEL_ALBUM);
            String albumName     = albumJson.getString(ALBUM_LABEL_NAME);

            // Use first image, if any exist
            JSONArray images = albumJson.getJSONArray(ALBUM_LABEL_IMAGES);
            String imageUrl = "";
            if (images.length() > 0) {
                imageUrl =  images.getJSONObject(0).getString(IMAGE_LABEL_URL);
            }

            JSONArray artistsArray = trackJson.getJSONArray(TRACK_LABEL_ARTISTS);
            if (artistsArray.length() > 0) {
                JSONObject artistJson = artistsArray.getJSONObject(0);
                String artistName = artistJson.getString(ARTIST_LABEL_NAME);
                tracks[ii] = new Track(name, id, albumName, imageUrl, previewUrl, artistName);
            } else {
                tracks[ii] = new Track(name, id, albumName, imageUrl, previewUrl, "");
            }
        }

        return tracks;

    }
}
