package com.example.musicplayer.ui.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.utils.MediaUtils;

//import org.jspecify.annotations.NonNull;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongAdapter(Context context, List<Song> songs, OnItemClickListener listener) {
        this.context = context;
        this.songList = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.bind(song, listener);
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView albumArt;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            albumArt = itemView.findViewById(R.id.imageAlbumArt);
        }

        public void bind(final Song song, final OnItemClickListener listener) {
            title.setText(song.getTitle());

            Bitmap art = null;
            if (song.getAlbumArt() != null) {
                art = MediaUtils.getAlbumArt(itemView.getContext(), song.getAlbumArt());
            }

            if (art != null) {
                albumArt.setImageBitmap(art);
            } else {
                albumArt.setImageResource(R.drawable.ic_music_placeholder);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(song));
        }

    }
}
