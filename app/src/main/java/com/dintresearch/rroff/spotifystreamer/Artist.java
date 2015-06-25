/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 6/22/2015
 */
package com.dintresearch.rroff.spotifystreamer;

/**
 * Container class for Artist data.
 */
public class Artist {

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

    public String getId() {
        return mIdStr;
    }

    public String getImageUrl() {
        return mImageUrlStr;
    }

    public String getName() {
        return mNameStr;
    }

    public void setId(String id) {
        mIdStr = id;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrlStr = imageUrl;
    }

    public void setName(String name) {
        mNameStr = name;
    }

    @Override
    public String toString() {
        return mNameStr;
    }
}
