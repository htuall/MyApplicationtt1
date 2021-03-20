package com.example.myapplicationtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ViewPager2 videosViewPager= findViewById(R.id.viewPager2);
        List<VideoItem> videoItems = new ArrayList<>();
        VideoItem videoItemCeleb= new VideoItem();
        videoItemCeleb.videoUrl="https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
        videoItemCeleb.videoTitle="Celebration";
        videoItemCeleb.videoDescription="Description";
        videoItems.add(videoItemCeleb);
        VideoItem videoItemCartoon= new VideoItem();
        videoItemCartoon.videoUrl="https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
        videoItemCartoon.videoTitle="Cartoon";
        videoItemCartoon.videoDescription="Description";
        videoItems.add(videoItemCartoon);
        videosViewPager.setAdapter(new VideosAdapter(videoItems));
    }
}