/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/22/2015
 */
package com.dintresearch.rroff.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Container class for Top 10 Track data.
 */
public class TopTrack implements Parcelable {

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

    /**
     * Constructor.
     *
     * @param in Parcelized object data
     */
    public TopTrack(Parcel in) {
        mTrackNameStr     = in.readString();
        mTrackIdStr       = in.readString();
        mAlbumNameStr     = in.readString();
        mAlbumImageUrlStr = in.readString();
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

    @Override
    public String toString() {
        return mTrackNameStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTrackNameStr);
        dest.writeString(mTrackIdStr);
        dest.writeString(mAlbumNameStr);
        dest.writeString(mAlbumImageUrlStr);
    }

    public static final Parcelable.Creator<TopTrack> CREATOR
            = new Parcelable.Creator<TopTrack>() {

        @Override
        public TopTrack createFromParcel(Parcel source) {
            return new TopTrack(source);
        }

        @Override
        public TopTrack[] newArray(int size) {
            return new TopTrack[size];
        }
    };
}
