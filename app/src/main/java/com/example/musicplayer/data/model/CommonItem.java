package com.example.musicplayer.data.model;

public class CommonItem {
    private String title;
    private String subtitle;
    private int imageResId; // Optional: can be used for icons

    public CommonItem(String title, String subtitle, int imageResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getImageResId() {
        return imageResId;
    }
}
