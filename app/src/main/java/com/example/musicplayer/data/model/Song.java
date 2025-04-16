package com.example.musicplayer.data.model;
public class Song {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String data;
    private String albumArt;
    private boolean liked=true;
    public Song(String id, String title, String artist, String album, String data, String albumArt) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.albumArt = albumArt;
    }

//    Getters

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArt() {
        return albumArt;
    }

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
}

