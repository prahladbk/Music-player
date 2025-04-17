package com.example.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import androidx.media.session.MediaButtonReceiver;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.utils.MediaUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private List<Song> songList = new ArrayList<>();
    private int currentIndex = -1;

    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;
    private boolean shouldResumeAfterFocus = false;

    private final Handler progressHandler = new Handler();
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                progressHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MusicService");
        setSessionToken(mediaSession.getSessionToken());

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(afChangeListener)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    "music_channel",
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                    progressHandler.post(progressRunnable);
                    showNotification(mediaSession.getController().getMetadata(), PlaybackStateCompat.STATE_PLAYING);
                }
            }

            @Override
            public void onPause() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                    progressHandler.removeCallbacks(progressRunnable);
                    showNotification(mediaSession.getController().getMetadata(), PlaybackStateCompat.STATE_PAUSED);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.abandonAudioFocusRequest(focusRequest);
                }
            }

            @Override
            public void onSeekTo(long pos) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo((int) pos);
                    updatePlaybackState(mediaPlayer.isPlaying() ?
                            PlaybackStateCompat.STATE_PLAYING :
                            PlaybackStateCompat.STATE_PAUSED);
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

    private final AudioManager.OnAudioFocusChangeListener afChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    shouldResumeAfterFocus = true;
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                }
                shouldResumeAfterFocus = false;
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (shouldResumeAfterFocus && mediaPlayer != null) {
                    mediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                }
                break;
        }
    };

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
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        try {
            mediaPlayer.setDataSource(song.getData());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();

                MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId())
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration())
                        .putBitmap(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                MediaUtils.getAlbumArt(getApplicationContext(), song.getData())
                        )
                        .build();

                mediaSession.setMetadata(metadata);
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                showNotification(metadata, PlaybackStateCompat.STATE_PLAYING);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
                Song nextSong = getNextSong();
                if (nextSong != null) {
                    currentIndex++;
                    playSong(nextSong);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePlaybackState(int state) {
        PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SEEK_TO |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .setState(state, mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0, 1.0f)
                .build();

        mediaSession.setPlaybackState(playbackState);
    }

    private void showNotification(MediaMetadataCompat metadata, int state) {
        if (metadata == null) return;

        PendingIntent playPauseIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_PLAY_PAUSE
        );
        PendingIntent nextIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        );
        PendingIntent prevIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        );

        int icon = state == PlaybackStateCompat.STATE_PLAYING ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "music_channel")
                .setContentTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .setContentText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                .setLargeIcon(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
                .setSmallIcon(android.R.drawable.ic_media_pause)
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevIntent)
                .addAction(icon, "Play/Pause", playPauseIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", nextIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setOnlyAlertOnce(true)
                .setOngoing(state == PlaybackStateCompat.STATE_PLAYING)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification = builder.build();
        startForeground(1, notification);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        }
        progressHandler.removeCallbacks(progressRunnable);
        mediaSession.release();
        stopForeground(true);
        super.onDestroy();
    }
}
