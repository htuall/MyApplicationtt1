package com.example.myapplicationtt;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {
    private List<VideoItem> videoItems;
    public static Context context;
    public VideosAdapter(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new VideoViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(videoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder{
        PlayerView playerView;
        ProgressBar progressBar;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView=itemView.findViewById(R.id.player_view);
            progressBar=itemView.findViewById(R.id.progress_bar);
        }

        void setVideoData(VideoItem videoItem){
            SimpleExoPlayer simpleExoPlayer;
            CacheDataSourceFactory cacheDataSourceFactory;
            cacheDataSourceFactory = new CacheDataSourceFactory(
                    context,
                    100 * 1024 * 1024,
                    5 * 1024 * 1024);

            LoadControl loadControl = new DefaultLoadControl();

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelector trackSelector = new DefaultTrackSelector(
                    new AdaptiveTrackSelection.Factory(bandwidthMeter)
            );

            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(//deprecated:Use SimpleExoPlayer.Builder or ExoPlayer.Builder instead.
                    context,trackSelector, loadControl
            );

            DefaultHttpDataSourceFactory factory= new DefaultHttpDataSourceFactory(//Deprecated.Use DefaultHttpDataSource.Factory instead.
                    "exoplayer_video"
            );

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();//Factory for arrays of Extractor instances.

            MediaSource mediaSource= new ExtractorMediaSource(videoItem.videoUrl,
                    factory, extractorsFactory, null, null);

            playerView.setPlayer(simpleExoPlayer);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null,
                    MimeTypes.APPLICATION_SUBRIP,
                    Format.NO_VALUE,
                    null);

            MediaSource subtitleSource = new SingleSampleMediaSource
                    .Factory(cacheDataSourceFactory)
                    .createMediaSource(videoItem.subtitleUrl, subtitleFormat, C.TIME_UNSET);
            CaptionStyleCompat captionStyleCompat = new CaptionStyleCompat(Color.YELLOW, Color.TRANSPARENT, Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW, Color.LTGRAY, null);
            playerView.getSubtitleView().setStyle(captionStyleCompat);

            simpleExoPlayer.prepare(new MergingMediaSource(mediaSource, subtitleSource),false, false);
            showSubtitle(true, simpleExoPlayer);
            simpleExoPlayer.setPlayWhenReady(true);

            playerView.setKeepScreenOn(true);

            simpleExoPlayer.prepare(mediaSource);

            simpleExoPlayer.setPlayWhenReady(true);

            simpleExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState==Player.STATE_BUFFERING){
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (playbackState==Player.STATE_READY){
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });
        }

        private void showSubtitle(boolean show, SimpleExoPlayer simpleExoPlayer) {
            if (simpleExoPlayer == null || playerView.getSubtitleView() == null)
                return;

            if (!show) {
                playerView.getSubtitleView().setVisibility(View.GONE);
                return;
            }

            playerView.getSubtitleView().setVisibility(View.VISIBLE);
        }

    }
}
