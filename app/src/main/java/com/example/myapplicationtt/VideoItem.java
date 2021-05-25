package com.example.myapplicationtt;

import android.net.Uri;

public class VideoItem {
    public Uri videoUrl;
    public String subtitleUrl;

    public VideoItem(Uri videoUrl, String subtitleUrl) {
        this.videoUrl = videoUrl;
        this.subtitleUrl = subtitleUrl;
    }
}
