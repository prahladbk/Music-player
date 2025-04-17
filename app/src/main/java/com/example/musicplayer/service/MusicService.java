package com.example.musicplayer.service;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.media.MediaBrowserServiceCompat;

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
                // Update the playback state with the current position
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
        }

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                    progressHandler.post(progressRunnable);
                }
            }

            @Override
            public void onPause() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                    progressHandler.removeCallbacks(progressRunnable);
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

//private void playSong(Song song) {
//    if (mediaPlayer != null) {
//        mediaPlayer.reset();
//    } else {
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////        mediaPlayer.setOnCompletionListener(mp -> playNext()); // Optional: auto play next
//    }
//
//    try {
//        mediaPlayer.setDataSource(song.getData());
//        mediaPlayer.prepareAsync();
//
//        mediaPlayer.setOnPreparedListener(mp -> {
//            mp.start();
//
//            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
//                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId())
//                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
//                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
//                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration())
//                    .putBitmap(
//                            MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
//                            MediaUtils.getAlbumArt(getApplicationContext(), song.getData()) // ✅ this line
//                    )
//                    .build();
//
//            mediaSession.setMetadata(metadata);
//
//            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
//        });
//
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}

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
        });

        // ✅ Auto play next song when this one finishes
        mediaPlayer.setOnCompletionListener(mp -> {
            updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
//            playNext(); // Make sure this method exists and plays the next song
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
        super.onDestroy();
    }
}
