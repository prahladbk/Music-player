package com.example.musicplayer.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musicplayer.data.model.LikedSong;

import java.util.List;

@Dao
public interface LikedSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LikedSong song);

    @Delete
    void delete(LikedSong song);

    @Query("SELECT * FROM liked_songs")
    LiveData<List<LikedSong>> getAllLikedSongs();
}
