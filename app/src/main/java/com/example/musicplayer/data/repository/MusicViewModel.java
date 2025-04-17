package com.example.musicplayer.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import android.content.Context;


import com.example.musicplayer.data.db.AppDatabase;
import com.example.musicplayer.data.db.SongDao;
import com.example.musicplayer.data.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MusicViewModel extends AndroidViewModel {
    private final MusicRepository musicRepository;
    private final MutableLiveData<List<Song>> songsLiveData = new MutableLiveData<>();
    private final SongDao songDao;


    public MusicViewModel(@NonNull Application application) {
        super(application);
        musicRepository = new MusicRepository(application);
        loadSongs();
        AppDatabase db = AppDatabase.getInstance(application);
        songDao = db.songDao();
    }

//    private void loadSongs() {
//        List<Song> songs = musicRepository.getAllSongs();
//        Log.d("PBK", "loadSongs: loading"+songs);
//        songsLiveData.setValue(songs);
//    }
private void loadSongs() {
    Executors.newSingleThreadExecutor().execute(() -> {
        List<Song> mediaSongs = musicRepository.getAllSongs(); // fetch from MediaStore
        Log.d("PBK", "loadSongs: loaded from MediaStore: " + mediaSongs.size());

        for (Song song : mediaSongs) {
            Song existing = songDao.getSongById(song.getId());
            if (existing == null) {
                songDao.insertSong(song);
            }
        }

        // Fetch all from Room and post to LiveData
//        List<Song> allSongsFromDb = songDao.getAllSongs();
        List<Song> allSongsFromDb = songDao.getAllSongsSync();
        songsLiveData.postValue(allSongsFromDb);
        Log.d("PBK", "loadSongs: loaded from Room: " + allSongsFromDb.size());

        songsLiveData.postValue(allSongsFromDb);
    });
}



    public LiveData<List<Song>> getSongs() {
        return songsLiveData;
    }
    public LiveData<List<Song>> getLikedSongs() {
        return songDao.getLikedSongs();
    }

    public void updateSong(Song song) {
        Executors.newSingleThreadExecutor().execute(() -> songDao.updateSong(song));
    }

    public void insertSong(Song song) {
        Executors.newSingleThreadExecutor().execute(() -> songDao.insertSong(song));
    }


}

