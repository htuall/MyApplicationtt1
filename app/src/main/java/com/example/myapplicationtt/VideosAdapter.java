package com.example.myapplicationtt;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {
    private List<VideoItem> videoItems;
    public static Context context;
    private OnWordListener onWordListener;
    String translatedText;
    private boolean connected;
    public static final String CONNECTIVITY_SERVICE="connectivity";

    public VideosAdapter(List<VideoItem> videoItems, OnWordListener onWordListener) {
        this.videoItems = videoItems;
        this.onWordListener = onWordListener;
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
        Translate translate;

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
                        ClickableSpan clickableSpan;
                        long currentPos = simpleExoPlayer.getCurrentPosition();
                        Collection<Caption> subtitles = srt.captions.values();
                        for (Caption caption : subtitles) {
                            if (currentPos >= caption.start.mseconds
                                    && currentPos <= caption.end.mseconds) {
                                ArrayList<String> arrayList=new ArrayList<>();
                                String fulltext= String.valueOf(Html.fromHtml(caption.content));
                                String[] words=fulltext.split(" ");
                                arrayList.addAll(Arrays.asList(words));
                                SpannableString spannableString = new SpannableString(fulltext);
                                ArrayList<String> brokenDownfulltext=new ArrayList<>(Arrays.asList(fulltext.split(" ")));
                                brokenDownfulltext.retainAll(arrayList);
                                for (int i=0;i<arrayList.size();i++){
                                    int indexOfWord = fulltext.indexOf(arrayList.get(i));
                                    int finalI = i;
                                    final String[] translated = new String[1];
                                    clickableSpan = new ClickableSpan() {
                                        @Override
                                        public void onClick(@NonNull View widget) {
                                            if (checkInternetConnection()) {
                                                //If there is internet connection, get translate service and start translation:
                                                getTranslateService();
                                                translated[0]=translate(arrayList.get(finalI));

                                            } else {
                                                //If not, display "no connection" warning:
                                                translated[0] =context.getResources().getString(R.string.no_connection);
                                            }
                                            Snackbar.make(subtitleText,arrayList.get(finalI)+"\n"+translated[0],Snackbar.LENGTH_LONG).show();
                                            onWordListener.onWord(arrayList.get(finalI)+" "+translated[0]);
                                        }
                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            ds.setUnderlineText(false);
                                        }
                                    };
                                    spannableString.setSpan(clickableSpan, indexOfWord, indexOfWord + arrayList.get(i).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                onTimedText(caption, spannableString);
                                break;
                            } else if (currentPos > caption.end.mseconds) {
                                onTimedText(null, null);
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



        public void onTimedText(Caption text,SpannableString spannableString) {
            if (text == null) {
                subtitleText.setVisibility(View.INVISIBLE);
                return;
            }

            subtitleText.setText(spannableString);
            subtitleText.setMovementMethod(LinkMovementMethod.getInstance());
            //subtitleText.setHighlightColor(android.R.color.holo_red_light);
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
        public void getTranslateService() {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try (InputStream is = context.getResources().openRawResource(R.raw.credentials)) {

                //Get credentials:
                final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

                //Set credentials and get translate service:
                TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
                translate = translateOptions.getService();

            } catch (IOException ioe) {
                ioe.printStackTrace();

            }
        }

        public String translate(String originalText) {

            //Get input text to be translated:
            Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("ru"), Translate.TranslateOption.model("base"));
            translatedText = translation.getTranslatedText();

            //Translated text and original text are set to TextViews:
            return translatedText;

        }

        public boolean checkInternetConnection() {

            //Check internet connection:
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

            //Means that we are connected to a network (mobile or wi-fi)
            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

            return connected;
        }


    }









}
