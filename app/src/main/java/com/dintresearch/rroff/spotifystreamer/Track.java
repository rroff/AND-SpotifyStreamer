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
public class Track implements Parcelable {

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
     * Track preview URL.
     */
    private String mPreviewUrlStr;

    /**
     * Artist name.
     */
    private String mArtistNameStr;

    /**
     * Constructor.
     *
     * @param trackName Track name
     * @param trackId Track ID
     * @param albumName Album name
     * @param albumImageUrl Album image URL
     */
    public Track(String trackName, String trackId, String albumName, String albumImageUrl,
                 String trackPreviewUrl, String artistName) {
        mTrackNameStr     = trackName;
        mTrackIdStr       = trackId;
        mAlbumNameStr     = albumName;
        mAlbumImageUrlStr = albumImageUrl;
        mPreviewUrlStr    = trackPreviewUrl;
        mArtistNameStr    = artistName;
    }

    /**
     * Constructor.
     *
     * @param in Parcelized object data
     */
    public Track(Parcel in) {
        mTrackNameStr     = in.readString();
        mTrackIdStr       = in.readString();
        mAlbumNameStr     = in.readString();
        mAlbumImageUrlStr = in.readString();
        mPreviewUrlStr    = in.readString();
        mArtistNameStr    = in.readString();
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

    public String getPreviewUrl() {
        return mPreviewUrlStr;
    }

    public String getArtistName() {
        return mArtistNameStr;
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
        dest.writeString(mPreviewUrlStr);
        dest.writeString(mArtistNameStr);
    }

    public static final Parcelable.Creator<Track> CREATOR
            = new Parcelable.Creator<Track>() {

        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
