/*
 * Copyright(c) 2015 Ron Roff
 * All Rights Reserved.
 *
 * Author: Ron Roff (rroff@roff.us)
 * Creation Date: 8/30/2015
 */
package com.dintresearch.rroff.spotifystreamer.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Container class holding status data being passed back from service.
 */
public class PlayerStatus implements Parcelable {

    private int mTrackDurationSeconds;
    private int mPlayPositionSeconds;
    private boolean mIsPlaying;

    public PlayerStatus(int trackDurationSeconds,
                       int playPositionSeconds,
                       boolean isPlaying) {
        mTrackDurationSeconds = trackDurationSeconds;
        mPlayPositionSeconds = playPositionSeconds;
        mIsPlaying = isPlaying;
    }

    public PlayerStatus(Parcel in) {
        mTrackDurationSeconds = in.readInt();
        mPlayPositionSeconds  = in.readInt();
        mIsPlaying            = (in.readInt() != 0);
    }

    public int getTrackDurationSeconds() {
        return mTrackDurationSeconds;
    }

    public int getPlayPositionSeconds() {
        return mPlayPositionSeconds;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTrackDurationSeconds);
        dest.writeInt(mPlayPositionSeconds);
        dest.writeInt((mIsPlaying ? 1 : 0));
    }

    public static final Parcelable.Creator<PlayerStatus> CREATOR
            = new Parcelable.Creator<PlayerStatus>() {

        @Override
        public PlayerStatus createFromParcel(Parcel source) {
            return new PlayerStatus(source);
        }

        @Override
        public PlayerStatus[] newArray(int size) {
            return new PlayerStatus[size];
        }
    };
}
