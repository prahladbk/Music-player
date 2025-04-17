package com.example.musicplayer.service;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.media.MediaBrowserServiceCompat;

import com.example.musicplayer.data.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private List<Song> songList = new ArrayList<>();
    private int currentIndex = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MusicService");
        setSessionToken(mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPlay() {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                }
            }

            @Override
            public void onPause() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                }
            }

            @Override
            public void onSkipToNext() {
                Song nextSong = getNextSong();
                if (nextSong != null) {
                    currentIndex++;
                    playSong(nextSong);
                }
            }

            @Override
            public void onSkipToPrevious() {
                Song prevSong = getPreviousSong();
                if (prevSong != null) {
                    currentIndex--;
                    playSong(prevSong);
                }
            }

            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                // You must pass the song list from the client side (ViewModel/Activity) using custom actions or similar
                if (extras != null && extras.containsKey("SONG_LIST")) {
                    songList = (List<Song>) extras.getSerializable("SONG_LIST");
                }

                Song song = getSongById(mediaId);
                if (song != null) {
                    currentIndex = songList.indexOf(song);
                    playSong(song);
                }
            }
        });

        mediaSession.setActive(true);
    }

    private Song getSongById(String mediaId) {
        for (Song song : songList) {
            if (song.getId().equals(mediaId)) {
                return song;
            }
        }
        return null;
    }

    private Song getNextSong() {
        if (currentIndex < songList.size() - 1) {
            return songList.get(currentIndex + 1);
        }
        return null;
    }

    private Song getPreviousSong() {
        if (currentIndex > 0) {
            return songList.get(currentIndex - 1);
        }
        return null;
    }

    private void playSong(Song song) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
            }

            mediaPlayer.setDataSource(song.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();

            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);

        } catch (IOException e) {
            Log.e("MusicService", "Error playing song: " + e.getMessage());
        }
    }

    private void updatePlaybackState(int state) {
        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setState(state,
                                mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0,
                                1)
                        .setActions(
                                PlaybackStateCompat.ACTION_PLAY |
                                        PlaybackStateCompat.ACTION_PAUSE |
                                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        )
                        .build()
        );
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(new ArrayList<>());
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
