package com.example.musicplayer.service;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MusicService");
        setSessionToken(mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                mediaPlayer.start();
                mediaSession.setPlaybackState(
                        new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_PLAYING,
                                        mediaPlayer.getCurrentPosition(), 1)
                                .build()
                );
            }

            @Override
            public void onPause() {
                mediaPlayer.pause();
                mediaSession.setPlaybackState(
                        new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_PAUSED,
                                        mediaPlayer.getCurrentPosition(), 1)
                                .build()
                );
            }

            @Override
            public void onSkipToNext() {
                // handle next
            }

            @Override
            public void onSkipToPrevious() {
                // handle previous
            }
        });

        mediaSession.setActive(true);
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(new ArrayList<>()); // Implement if needed
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaSession.release();
        super.onDestroy();
    }
}
