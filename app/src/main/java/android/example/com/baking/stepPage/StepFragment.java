/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.stepPage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.example.com.baking.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media.session.MediaButtonReceiver;

import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class StepFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = StepFragment.class.getSimpleName();
    private boolean playWhenReady = false;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private static String mStepInstruction;
    private static String mVideoURL;
    private boolean mTwoPane;

    private PlayerView mPlayerView;
    private TextView mNextPreviousStepTextView;
    private TextView mStepInstructionTextView;
    private SimpleExoPlayer mPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private PlaybackStateListener playbackStateListener;


    public void setStepInstruction(String stepInstruction) {
        mStepInstruction = stepInstruction;
    }
    public void setVideoURL(String videoURL) { mVideoURL = videoURL; }
    public void setTwoPane(boolean twoPane) { mTwoPane = twoPane; }

    public StepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        mPlayerView = rootView.findViewById(R.id.video_view);
        mStepInstructionTextView = rootView.findViewById(R.id.tv_step_instruction);
        mNextPreviousStepTextView = rootView.findViewById(R.id.tv_step_next_previous);

        playbackStateListener = new PlaybackStateListener();
        initializeMediaSession();

        mStepInstructionTextView.setText(mStepInstruction);
        if (mTwoPane) {
            mNextPreviousStepTextView.setVisibility(View.GONE);

            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2f);
            mStepInstructionTextView.setLayoutParams(layoutParams);
        } else {
            mNextPreviousStepTextView.setOnClickListener(this);
        }

        return rootView;
    }

    /**
     * When the phone is rotated
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mTwoPane) {
            if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                hideSystemUi();
                mStepInstructionTextView.setVisibility(View.GONE);
                mNextPreviousStepTextView.setVisibility(View.GONE);
            } else {
                showSystemUi();
                mStepInstructionTextView.setVisibility(View.VISIBLE);
                mNextPreviousStepTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mTwoPane && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
        }

        if (mPlayer == null) {
            initializePlayer(mVideoURL);
        }
    }

    private void initializePlayer(final String videoURL) {
        if (mPlayer == null) {
            mPlayer = new SimpleExoPlayer.Builder(getContext()).build();
            mPlayerView.setPlayer(mPlayer);

            ControlDispatcher mControlDispatcher = new ControlDispatcher(videoURL);
            mPlayerView.setControlDispatcher(mControlDispatcher);
            mPlayer.addListener(playbackStateListener);

            if (!videoURL.equals("")) {
                MediaItem mediaItem = MediaItem.fromUri(videoURL);
                mPlayer.setMediaItem(mediaItem);

                mPlayer.setPlayWhenReady(playWhenReady);
                mPlayer.seekTo(currentWindow, playbackPosition);
                mPlayer.prepare();
            } else {
                Toast.makeText(getContext(), getString(R.string.video_not_available), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Modify the behavior for the play button under special conditions
     * Here deal with this case the play button is clicked but network or video is not availablw
     */
    private class ControlDispatcher extends DefaultControlDispatcher {
        String videoURL;

        public ControlDispatcher(String videoURL) {
            super();
            this.videoURL = videoURL;
        }

        @Override
        public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
            ConnectivityManager cm = (ConnectivityManager) getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (playWhenReady) {
                if (!isConnected) {
                    toastErrorMessage(getString(R.string.network_not_available));
                } else if (videoURL.equals("")) {
                    toastErrorMessage(getString(R.string.video_not_available));
                }
            }
            return super.dispatchSetPlayWhenReady(player, playWhenReady);
        }

        private void toastErrorMessage(String errorMessage) {
            Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        Toast.makeText(getContext().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                    super.handleMessage(msg);
                }
            };

            Message msg = handler.obtainMessage();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
            releasePlayer();
            mMediaSession.setActive(false);
    }


    private void releasePlayer() {
        if (mPlayer != null) {
            playWhenReady = mPlayer.getPlayWhenReady();
            playbackPosition = mPlayer.getCurrentPosition();
            currentWindow = mPlayer.getCurrentWindowIndex();
            mPlayer.removeListener(playbackStateListener);
            mPlayer.release();
            mPlayer = null;
        }
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }
    }

    private class PlaybackStateListener implements Player.EventListener {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            mStateBuilder.setState(playbackState, mPlayer.getCurrentPosition(), 1f);
            mMediaSession.setPlaybackState(mStateBuilder.build());
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mPlayer.getCurrentPosition(), 1f);
                Log.d(TAG, "changed state to: PLAYING");
            }
            else {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mPlayer.getCurrentPosition(), 1f);
                Log.d(TAG, "changed state to: PAUSED");
            }
            mMediaSession.setPlaybackState(mStateBuilder.build());
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN

        );
    }

    private void showSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onClick(View view) {
        // Go back to the up activity
        getActivity().onBackPressed();
    }
}
