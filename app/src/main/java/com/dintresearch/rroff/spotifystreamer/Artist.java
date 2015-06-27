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
 * Container class for Artist data.
 */
public class Artist implements Parcelable {

    /**
     * Artist name.
     */
    private String mNameStr;

    /**
     * Artist ID
     */
    private String mIdStr;

    /**
     * Artist image URL
     */
    private String mImageUrlStr;

    /**
     * Constructor.
     *
     * @param name Artist name
     * @param id Artist ID
     * @param imageUrl Artist image URL
     */
    public Artist(String name, String id, String imageUrl) {
        mNameStr     = name;
        mIdStr       = id;
        mImageUrlStr = imageUrl;
    }

    /**
     * Constructor.
     *
     * @param in Parcelized object data
     */
    public Artist(Parcel in) {
        mNameStr     = in.readString();
        mIdStr       = in.readString();
        mImageUrlStr = in.readString();
    }

    public String getId() {
        return mIdStr;
    }

    public String getImageUrl() {
        return mImageUrlStr;
    }

    public String getName() {
        return mNameStr;
    }

    @Override
    public String toString() {
        return mNameStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNameStr);
        dest.writeString(mIdStr);
        dest.writeString(mImageUrlStr);
    }

    public static final Parcelable.Creator<Artist> CREATOR
            = new Parcelable.Creator<Artist>() {

        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
