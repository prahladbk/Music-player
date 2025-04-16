package com.example.musicplayer.data.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//import org.jspecify.annotations.NonNull;


@Entity(tableName = "liked_songs")
public class LikedSong {
    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public String artist;
    public String album;
    public String data;
    public String albumArt;

    public LikedSong(@NonNull String id, String title, String artist, String album, String data, String albumArt) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.albumArt = albumArt;
    }
}
