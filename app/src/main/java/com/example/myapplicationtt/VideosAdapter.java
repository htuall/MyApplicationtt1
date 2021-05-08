package com.example.myapplicationtt;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtt.utils.Caption;
import com.example.myapplicationtt.utils.FormatSRT;
import com.example.myapplicationtt.utils.TimedTextObject;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
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

    class VideoViewHolder extends RecyclerView.ViewHolder{
        private PlayerView playerView;
        private ProgressBar progressBar;
        private TextView subtitleText;
        public TimedTextObject srt;
        private Handler subtitleDisplayHandler = new Handler();

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView=itemView.findViewById(R.id.player_view);
            progressBar=itemView.findViewById(R.id.progress_bar);
            subtitleText=itemView.findViewById(R.id.subtitleText);
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

            Runnable subtitleProcessesor = new Runnable() {

                @Override
                public void run() {
                    if (simpleExoPlayer != null) {
                        long currentPos = simpleExoPlayer.getCurrentPosition();
                        Collection<Caption> subtitles = srt.captions.values();
                        for (Caption caption : subtitles) {
                            if (currentPos >= caption.start.mseconds
                                    && currentPos <= caption.end.mseconds) {
                                onTimedText(caption);
                                break;
                            } else if (currentPos > caption.end.mseconds) {
                                onTimedText(null);
                            }
                        }
                    }
                    subtitleDisplayHandler.postDelayed(this, 100);
                }
            };

            DefaultHttpDataSourceFactory factory= new DefaultHttpDataSourceFactory(//Deprecated.Use DefaultHttpDataSource.Factory instead.
                    "exoplayer_video"
            );

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();//Factory for arrays of Extractor instances.

            MediaSource mediaSource= new ExtractorMediaSource(videoItem.videoUrl,
                    factory, extractorsFactory, null, null);

            playerView.setPlayer(simpleExoPlayer);

            simpleExoPlayer.prepare(mediaSource);
            showSubtitle(false, simpleExoPlayer);

            playerView.setKeepScreenOn(true);


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


            class SubtitleProcessingTask extends AsyncTask<String, Void, Void> {

                @Override
                protected void onPreExecute() {
                    subtitleText.setText("Loading...");
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(String... params) {
                    try {
                        InputStream stream = new URL(params[0]).openStream();
                        FormatSRT formatSRT = new FormatSRT();
                        srt = formatSRT.parseFile("",stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("RRR", "error");
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (null != srt) {
                        subtitleText.setText("");
                        Toast.makeText(context.getApplicationContext(), "Loaded!",
                                Toast.LENGTH_SHORT).show();
                        subtitleDisplayHandler.post(subtitleProcessesor);
                    }
                    super.onPostExecute(result);
                }
            }
            SubtitleProcessingTask subsFetchTask = new SubtitleProcessingTask();
            subsFetchTask.execute(videoItem.subtitleUrl);

        }

        public void onTimedText(Caption text) {
            if (text == null) {
                subtitleText.setVisibility(View.INVISIBLE);
                return;
            }
            subtitleText.setText(Html.fromHtml(text.content));
            subtitleText.setVisibility(View.VISIBLE);
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
