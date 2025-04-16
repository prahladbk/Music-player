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

                String albumArt = null; // Fetch album art later
                songs.add(new Song(id, title, artist, album, data, albumArt));
                Log.d("PBK", "Fetched song: " + title + " by " + artist);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return songs;
    }
}
