package com.example.musicplayer.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayer.data.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicRepository {
    private Context context;

    public MusicRepository(Context context) {
        this.context = context;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                // Get album art URI using albumId
                Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArt = Uri.withAppendedPath(albumArtUri, String.valueOf(albumId));

                songs.add(new Song(id, title, artist, album, data, albumArt.toString(),true));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return songs;
    }

    private String getAlbumArt(long albumId) {
        String albumArt = null;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = Uri.withAppendedPath(sArtworkUri, String.valueOf(albumId));

        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            albumArt = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
            cursor.close();
        }

        return uri.toString(); // Returning content URI instead of file path
    }
}
