package com.example.musicplayer.ui.player;

import android.app.Application;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class PlayerViewModel extends AndroidViewModel {
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> duration = new MutableLiveData<>(0);

    private MediaControllerCompat mediaController;
    private final MediaBrowserCompat browser;

    private List<Song> playlist = new ArrayList<>();
    private int currentIndex = 0;

    private List<Song> pendingPlaylist = null;
    private int pendingIndex = -1;

    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private final MutableLiveData<Bitmap> albumArtBitmap = new MutableLiveData<>();
    public LiveData<Bitmap> getAlbumArtBitmap() {
        return albumArtBitmap;
    }

    public PlayerViewModel(@NonNull Application application) {
        super(application);

        browser = new MediaBrowserCompat(
                application,
                new ComponentName(application, MusicService.class),
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        mediaController = new MediaControllerCompat(application, browser.getSessionToken());

                        if (pendingPlaylist != null) {
                            setPlaylist(pendingPlaylist, pendingIndex);
                            pendingPlaylist = null;
                        }

                        mediaController.registerCallback(new MediaControllerCompat.Callback() {

                            @Override
                            public void onMetadataChanged(@NonNull MediaMetadataCompat metadata) {
                                if (metadata != null) {
                                    // Update duration
                                    duration.postValue((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

                                    // Update album art
                                    Bitmap art = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
                                    if (art != null) {
                                        albumArtBitmap.postValue(art);
                                    } else {
                                        albumArtBitmap.postValue(null); // fallback will be handled in UI
                                    }
                                }
                            }

                            @Override
                            public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                                isPlaying.postValue(state.getState() == PlaybackStateCompat.STATE_PLAYING);
                                currentPosition.postValue((int) state.getPosition());

                                // Update duration from playback state
                                if (state.getState() == PlaybackStateCompat.STATE_PLAYING || state.getState() == PlaybackStateCompat.STATE_PAUSED) {
                                    duration.postValue((int) state.getBufferedPosition());
                                }
                            }
                        });

                        // Start updating the seek bar after connection
                        startUpdatingSeekBar();
                    }
                },
                null
        );
        browser.connect();
    }

    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public LiveData<Boolean> isPlaying() {
        return isPlaying;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public LiveData<Integer> getDuration() {
        return duration;
    }

    private void startUpdatingSeekBar() {
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                // Update current position every 1 second
                if (mediaController != null && mediaController.getPlaybackState() != null) {
                    long pos = mediaController.getPlaybackState().getPosition();
                    currentPosition.postValue((int) pos);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private void stopUpdatingSeekBar() {
        if (handler != null && updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    public void setPlaylist(List<Song> songs, int index) {
        if (mediaController == null) {
            pendingPlaylist = songs;
            pendingIndex = index;
            return;
        }

        playlist = songs;
        currentIndex = index;

        if (!playlist.isEmpty()) {
            setCurrentSong(playlist.get(currentIndex));
        }
    }

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
        currentIndex = playlist.indexOf(song);

        if (mediaController != null) {
            Bundle extras = new Bundle();
            extras.putSerializable("SONG_LIST", new ArrayList<>(playlist));
            mediaController.getTransportControls().playFromMediaId(song.getId(), extras);
        }
    }

    public void togglePlayPause() {
        if (mediaController != null) {
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.getTransportControls().pause();
            } else {
                mediaController.getTransportControls().play();
            }
        }
    }

    public void playNext() {
        if (playlist == null || playlist.isEmpty()) return;

        if (currentIndex < playlist.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0; // Loop back to first song
        }

        setCurrentSong(playlist.get(currentIndex));
    }

    public void playPrevious() {
        if (playlist != null && currentIndex > 0) {
            currentIndex--;
            setCurrentSong(playlist.get(currentIndex));
        }
    }

    public void seekTo(int position) {
        if (mediaController != null) {
            mediaController.getTransportControls().seekTo(position);
        }
    }

    public void updateSeekBar() {
        if (mediaController != null && mediaController.getPlaybackState() != null) {
            long pos = mediaController.getPlaybackState().getPosition();
            currentPosition.postValue((int) pos);

            // Optional: Also update duration in case it changed
            if (mediaController.getMetadata() != null) {
                long durationLong = mediaController.getMetadata().getLong(android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION);
                duration.postValue((int) durationLong);
            }
        }
    }


    public void setSongDuration(int songDuration) {
        duration.postValue(songDuration);
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        stopUpdatingSeekBar();
    }
}

