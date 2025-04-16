package com.example.musicplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.example.musicplayer.R;

public class MediaUtils {
    public static Bitmap getAlbumArt(Context context, String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            return BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_placeholder);
        }
    }
}
