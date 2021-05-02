package com.example.myapplicationtt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final ViewPager2 videosViewPager= findViewById(R.id.viewPager2);
        List<VideoItem> videoItems = new ArrayList<>();
        //subtitle.subtitleUrl="http://195.19.44.146:90/Friends.S07E01.srt";

        VideoItem videoItemCeleb= new VideoItem();
        videoItemCeleb.videoUrl=Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4");
        //videoItemCeleb.videoUrl=Uri.parse("http://195.19.44.146:90/Friends.S07E01.mkv");
        videoItemCeleb.subtitleUrl=Uri.parse("http://195.19.44.146:90/Friends.S07E01.srt");
        videoItems.add(videoItemCeleb);
        VideoItem videoItemCartoon= new VideoItem();
        videoItemCartoon.videoUrl=Uri.parse("http://195.19.44.146:90/Friends.S07E01.mkv");
        videoItemCartoon.subtitleUrl=Uri.parse("http://195.19.44.146:90/Friends.S07E01.srt");
        videoItems.add(videoItemCartoon);
        videosViewPager.setAdapter(new VideosAdapter(videoItems));
    }

}