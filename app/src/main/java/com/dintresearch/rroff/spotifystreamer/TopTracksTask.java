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
 * Created by rroff on 6/22/2015.
 */
public class TopTracksTask extends AsyncTask<String, Void, TopTrack[]> {

    /**
     * Class name for logging.
     */
    private final static String LOG_TAG = TopTracksTask.class.getSimpleName();

    /**
     * Adapter for ingesting artist data.
     */
    private TopTrackAdapter mTopTracksAdapter;

    private final static String BASE_ARTISTS_URL = "https://api.spotify.com/v1/artists";

    private final static String TOP_TRACKS_ENDPOINT = "top-tracks";

    /**
     * Query parameter name for country code.
     */
    private final static String COUNTRY_PARAM = "country";

    /**
     * Default constructor.  Marked private to prevent its use.
     * Parameterized constructor required.
     */
    @SuppressWarnings("unused")
    private TopTracksTask() { }

    /**
     * Parameterized constructor.
     *
     * @param topTracksAdapter Adapter for loading track results into the main thread
     */
    public TopTracksTask(TopTrackAdapter topTracksAdapter) {
        super();
        mTopTracksAdapter = topTracksAdapter;
    }

    @Override
    protected TopTrack[] doInBackground(String... params) {

        TopTrack topTracks[]      = null;

        if ((params.length > 0) && (params[0].length() > 0)) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection    = null;
            BufferedReader    reader           = null;
            String            topTracksJsonStr = null;

            String countryCode = "US";

            try {
                // Construct the URL for the Spotify Artist Top Tracks query
                // Ref: https://developer.spotify.com/web-api/get-artists-top-tracks/
                Uri builtUri = Uri.parse(BASE_ARTISTS_URL).buildUpon()
                        .appendPath(params[0])
                        .appendPath(TOP_TRACKS_ENDPOINT)
                        .appendQueryParameter(COUNTRY_PARAM, countryCode)
                        .build();
                URL url = new URL(builtUri.toString());

                // Send request to Spotify
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Store results from search request
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Format in the event the string is needed for debugging
                    buffer.append(line + "\n");
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
                topTracks = getTopTracksDataFromJson(topTracksJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error ", e);
                return null;
            }
        }

        return topTracks;
    }

    @Override
    protected void onPostExecute(TopTrack[] topTracks) {
        mTopTracksAdapter.clear();

        if ((topTracks == null) || (topTracks.length == 0)) {
            mTopTracksAdapter.showToast(R.string.msg_no_tracks_found);
        } else {
            mTopTracksAdapter.addAll(topTracks);
            if (topTracks.length < 10) {
                mTopTracksAdapter.showToast(R.string.msg_less_than_ten_tracks_found);
            }
        }
    }

    private TopTrack[] getTopTracksDataFromJson(String topTracksJsonStr)
            throws JSONException {

        // JSON objects that need to be extracted
        final String JSON_LABEL_TRACKS = "tracks";
        final String TRACK_LABEL_NAME  = "name";
        final String TRACK_LABEL_ID    = "id";
        final String TRACK_LABEL_ALBUM = "album";
        final String ALBUM_LABEL_NAME  = "name";

        JSONObject topTracksJson = new JSONObject(topTracksJsonStr);
        JSONArray tracksArray = topTracksJson.getJSONArray(JSON_LABEL_TRACKS);

        TopTrack[] topTracks = new TopTrack[tracksArray.length()];

        for (int ii=0; ii < tracksArray.length(); ++ii) {
            JSONObject trackJson = tracksArray.getJSONObject(ii);
            String name = trackJson.getString(TRACK_LABEL_NAME);
            String id   = trackJson.getString(TRACK_LABEL_ID);

            JSONObject albumJson = trackJson.getJSONObject(TRACK_LABEL_ALBUM);
            String albumName = albumJson.getString(ALBUM_LABEL_NAME);

            // TODO: Extract image url
            String imageUrl = "";

            topTracks[ii] = new TopTrack(name, id, albumName, imageUrl);
        }

        return topTracks;

    }
}
