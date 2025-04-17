package com.example.musicplayer.ui.player;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.service.MusicService;

import java.util.ArrayList;
import java.util.List;

//import org.jspecify.annotations.NonNull;

public class PlayerViewModel extends AndroidViewModel {
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private MediaControllerCompat mediaController;
    private final MediaBrowserCompat browser;
    private List<Song> pendingPlaylist = null;
    private int pendingIndex = -1;


    private List<Song> playlist = new ArrayList<>();
    private int currentIndex = 0;

    public PlayerViewModel(@NonNull Application application) {
        super(application);

        browser = new MediaBrowserCompat(
                application,
                new ComponentName(application, MusicService.class),
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        mediaController = new MediaControllerCompat(application, browser.getSessionToken());
                        if (pendingPlaylist != null && !pendingPlaylist.isEmpty() && pendingIndex >= 0) {
                            setPlaylist(pendingPlaylist, pendingIndex);
                            pendingPlaylist = null;
                            pendingIndex = -1;
                        }


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
        if (playlist != null) {
            currentIndex = playlist.indexOf(song);
        }
        if (mediaController != null) {
            Bundle extras = new Bundle();
            extras.putSerializable("SONG_LIST", new ArrayList<>(playlist)); // Song must implement Serializable
            mediaController.getTransportControls().playFromMediaId(song.getId(), extras);
        }
    }
public void setPlaylist(List<Song> songs, int index) {
    if (mediaController == null) {
        // Wait until onConnected() is called
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


    public void togglePlayPause() {
        if (mediaController != null) {
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.getTransportControls().pause();
                Log.d("PBK", "togglePlayPause: pause");
            } else {
                mediaController.getTransportControls().play();
                Log.d("PBK", "togglePlayPause: play");
            }
        }
    }

    public void playNext() {
//        if (mediaController != null)
//            mediaController.getTransportControls().skipToNext();
        if (playlist != null && currentIndex < playlist.size() - 1) {
            currentIndex++;
            setCurrentSong(playlist.get(currentIndex));
        }
    }

    public void playPrevious() {
//        if (mediaController != null)
//            mediaController.getTransportControls().skipToPrevious();
        if (playlist != null && currentIndex > 0) {
            currentIndex--;
            setCurrentSong(playlist.get(currentIndex));
        }
    }
}