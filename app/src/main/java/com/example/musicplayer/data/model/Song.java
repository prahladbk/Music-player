package com.example.musicplayer.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "songs")
public class Song implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String artist;
    private String album;
    private String data;
    private String albumArt;
    private boolean liked;

    public Song(@NonNull String id, String title, String artist, String album, String data, String albumArt, boolean liked) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.albumArt = albumArt;
        this.liked = liked;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getData() {
        return data;
    }

    public boolean isLiked() {
        return liked;
    }

    // Setters (Room needs these if you want to update fields)
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    // You can add other setters as needed
}
