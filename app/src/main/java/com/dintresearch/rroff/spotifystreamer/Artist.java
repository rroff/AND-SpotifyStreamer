package com.dintresearch.rroff.spotifystreamer;

/**
 * Created by rroff on 6/22/2015.
 */
public class Artist {

    private String mName;

    private String mId;

    private String mImageUrl;

    public Artist() { }

    public Artist(String name, String id, String imageUrl) {
        mName = name;
        mId = id;
        mImageUrl = imageUrl;
    }

    public String getId() {
        return mId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
