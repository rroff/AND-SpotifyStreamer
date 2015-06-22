package com.dintresearch.rroff.spotifystreamer;

/**
 * Created by rroff on 6/22/2015.
 */
public class TopTrack {

    private String mTrackNameStr;

    private String mAlbumNameStr;

    private String mTrackIdStr;

    private String mImageUrlStr;

    public TopTrack() { }

    public TopTrack(String trackName, String trackId, String albumName, String imageUrl) {
        mTrackNameStr = trackName;
        mTrackIdStr   = trackId;
        mAlbumNameStr = albumName;
        mImageUrlStr  = imageUrl;
    }

    public String getTrackId() {
        return mTrackIdStr;
    }

    public String getImageUrl() {
        return mImageUrlStr;
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

    public void setImageUrl(String imageUrl) {
        mImageUrlStr = imageUrl;
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
