/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/22/2015
 */
package com.dintresearch.rroff.spotifystreamer;

/**
 * Container class for Top 10 Track data.
 */
public class TopTrack {

    /**
     * Track name.
     */
    private String mTrackNameStr;

    /**
     * Album name.
     */
    private String mAlbumNameStr;

    /**
     * Track ID.
     */
    private String mTrackIdStr;

    /**
     * Album image URL.
     */
    private String mAlbumImageUrlStr;

    /**
     * Constructor.
     *
     * @param trackName Track name
     * @param trackId Track ID
     * @param albumName Album name
     * @param albumImageUrl Album image URL
     */
    public TopTrack(String trackName, String trackId, String albumName, String albumImageUrl) {
        mTrackNameStr     = trackName;
        mTrackIdStr       = trackId;
        mAlbumNameStr     = albumName;
        mAlbumImageUrlStr = albumImageUrl;
    }

    public String getTrackId() {
        return mTrackIdStr;
    }

    public String getAlbumImageUrl() {
        return mAlbumImageUrlStr;
    }

    public String getTrackName() {
        return mTrackNameStr;
    }

    public String getAlbumName() {
        return mAlbumNameStr;
    }

    public void setTrackId(String id) {
        mTrackIdStr = id;
    }

    public void setAlbumImageUrl(String imageUrl) {
        mAlbumImageUrlStr = imageUrl;
    }

    public void setTrackName(String name) {
        mTrackNameStr = name;
    }

    public void setAlbumNameStr(String name) {
        mAlbumNameStr = name;
    }

    @Override
    public String toString() {
        return mTrackNameStr;
    }
}
