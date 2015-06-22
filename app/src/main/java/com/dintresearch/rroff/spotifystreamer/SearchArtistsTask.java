package com.dintresearch.rroff.spotifystreamer;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
 * Created by rroff on 6/17/2015.
 */
public class SearchArtistsTask extends AsyncTask<String, Void, String[]> {

    /**
     * Class name for logging.
     */
    private final String LOG_TAG = SearchArtistsTask.class.getSimpleName();

    /**
     * Adapter for ingesting artist data.
     */
    private ArrayAdapter<String> mArtistAdapter;

    /**
     * Default constructor.  Marked private to prevent its use.
     * Parameterized constructor required.
     */
    private SearchArtistsTask() {
    }

    /**
     * Parameterized constructor.
     *
     * @param artistAdapter Adapter for loading search results into the main thread
     */
    public SearchArtistsTask(ArrayAdapter<String> artistAdapter) {
        super();
        mArtistAdapter = artistAdapter;
    }

    /**
     *
     * @param params
     * @return
     */
    protected String[] doInBackground(String... params) {

        if ((params.length == 0) || (params[0].length() == 0)) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String artistJsonStr = null;
        String artistStrings[];

        String searchType = "artist";
        int maxResults = 50;
        int resultsOffset = 0;

        try {
            // Construct the URL for the Spotify Search query
            // Ref: https://developer.spotify.com/web-api/search-item/
            final String FORECAST_BASE_URL = "https://api.spotify.com/v1/search";
            final String QUERY_PARAM = "q";
            final String SEARCH_TYPE_PARAM = "type";
            final String MAX_RESULTS_PARAM = "limit";
            final String RESULTS_OFFSET_PARAM = "offset";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(SEARCH_TYPE_PARAM, searchType)
                    .appendQueryParameter(MAX_RESULTS_PARAM, Integer.toString(maxResults))
                    .appendQueryParameter(RESULTS_OFFSET_PARAM, Integer.toString(resultsOffset))
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
            artistStrings = getArtistDataFromJson(artistJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error ", e);
            return null;
        }

        return artistStrings;
    }

    /**
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            mArtistAdapter.clear();
            mArtistAdapter.addAll(result);
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
    private String[] getArtistDataFromJson(String artistJsonStr)
            throws JSONException {

        // JSON objects that need to be extracted
        final String SEARCH_ARTISTS = "artists";
        final String SEARCH_ITEMS   = "items";
        final String SEARCH_NAME    = "name";

        JSONObject searchJson = new JSONObject(artistJsonStr);
        JSONObject artistsJson = searchJson.getJSONObject(SEARCH_ARTISTS);
        JSONArray itemsArray = artistsJson.getJSONArray(SEARCH_ITEMS);

        String[] artistStrings = new String[itemsArray.length()];

        for (int ii=0; ii < itemsArray.length(); ++ii) {
            JSONObject artistJSON = itemsArray.getJSONObject(ii);
            artistStrings[ii] = artistJSON.getString(SEARCH_NAME);
        }

        return artistStrings;
    }
}
