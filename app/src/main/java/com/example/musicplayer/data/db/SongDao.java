package com.example.musicplayer.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicplayer.data.model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(Song song);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);

    @Query("SELECT * FROM songs")
    LiveData<List<Song>> getAllSongs();  // For observing in UI

    @Query("SELECT * FROM songs")
    List<Song> getAllSongsSync();        // For ViewModel internal logic

    @Query("SELECT * FROM songs WHERE liked = 1")
    LiveData<List<Song>> getLikedSongs();

    @Update
    void updateSong(Song song);

    @Query("SELECT * FROM songs WHERE id = :id")
    Song getSongById(String id);
}




