package com.example.musicplayer.ui.player;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.service.MusicService;

//import org.jspecify.annotations.NonNull;

public class PlayerViewModel extends AndroidViewModel {
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private MediaControllerCompat mediaController;
    private final MediaBrowserCompat browser;

    public PlayerViewModel(@NonNull Application application) {
        super(application);

        browser = new MediaBrowserCompat(
                application,
                new ComponentName(application, MusicService.class),
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        mediaController = new MediaControllerCompat(application, browser.getSessionToken());

                        mediaController.registerCallback(new MediaControllerCompat.Callback() {
                            @Override
                            public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                                isPlaying.postValue(state.getState() == PlaybackStateCompat.STATE_PLAYING);
                            }
                        });
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

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
        if (mediaController != null) {
            mediaController.getTransportControls().play();
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
        if (mediaController != null)
            mediaController.getTransportControls().skipToNext();
    }

    public void playPrevious() {
        if (mediaController != null)
            mediaController.getTransportControls().skipToPrevious();
    }
}