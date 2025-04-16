package com.example.musicplayer.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.data.model.Song;

import java.util.List;

public class MusicViewModel extends AndroidViewModel {
    private final MusicRepository musicRepository;
    private final MutableLiveData<List<Song>> songsLiveData = new MutableLiveData<>();


    public MusicViewModel(@NonNull Application application) {
        super(application);
        musicRepository = new MusicRepository(application);
        loadSongs();
    }

    private void loadSongs() {
        List<Song> songs = musicRepository.getAllSongs();
        Log.d("PBK", "loadSongs: loading"+songs);
        songsLiveData.setValue(songs);
    }

    public LiveData<List<Song>> getSongs() {
        return songsLiveData;
    }
}

