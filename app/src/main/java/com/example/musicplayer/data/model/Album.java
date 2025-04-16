package com.example.musicplayer.data.model;

public class Album {
    private String name;
    private String albumArt;

    public Album(String name, String albumArt) {
        this.name = name;
        this.albumArt = albumArt;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getName() {
        return name;
    }
    // Getters and Setters
}
