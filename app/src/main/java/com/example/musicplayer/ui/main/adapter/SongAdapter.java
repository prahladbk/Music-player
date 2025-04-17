package com.example.musicplayer.ui.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.data.db.AppDatabase;
import com.example.musicplayer.data.db.SongDao;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.ui.player.PlayerActivity;
import com.example.musicplayer.utils.MediaUtils;

//import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private static List<Song> songList;
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
        ImageView albumArt,likeBtn;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            albumArt = itemView.findViewById(R.id.imageAlbumArt);
            likeBtn = itemView.findViewById(R.id.imageLike);
        }
        public void bind(final Song song, final OnItemClickListener listener) {
            title.setText(song.getTitle());

            if (song.getAlbumArt() != null && !song.getAlbumArt().isEmpty()) {
                try {
                    Uri uri = Uri.parse(song.getAlbumArt());
                    albumArt.setImageURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    albumArt.setImageResource(R.drawable.ic_music_placeholder);
                }
            } else {
                albumArt.setImageResource(R.drawable.ic_music_placeholder);
            }

            // 👉 Set the initial like icon based on song's liked status
            if (song.isLiked()) {
                likeBtn.setImageResource(R.drawable.baseline_favorite_fill);
            } else {
                likeBtn.setImageResource(R.drawable.baseline_favorite_border);
            }

            // Song item click
            itemView.setOnClickListener(v -> {
                listener.onItemClick(song);
                int position = getBindingAdapterPosition();
                Context context = itemView.getContext();
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("songs", new ArrayList<>(songList));
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

            // Like button click
            likeBtn.setOnClickListener(v -> {
                Context context = itemView.getContext();
                AppDatabase db = AppDatabase.getInstance(context);
                SongDao dao = db.songDao();

                new Thread(() -> {
                    Song dbSong = dao.getSongById(song.getId());
                    if (dbSong != null) {
                        boolean newLikedState = !dbSong.isLiked();
                        dbSong.setLiked(newLikedState);
                        dao.updateSong(dbSong);

                        // ✅ Optional: update the current song object's liked state too
                        song.setLiked(newLikedState);

                        likeBtn.post(() -> {
                            if (newLikedState) {
                                likeBtn.setImageResource(R.drawable.baseline_favorite_fill);
                            } else {
                                likeBtn.setImageResource(R.drawable.baseline_favorite_border);
                            }
                        });
                    }
                }).start();
            });
        }


//        public void bind(final Song song, final OnItemClickListener listener) {
//            title.setText(song.getTitle());
//
//            if (song.getAlbumArt() != null && !song.getAlbumArt().isEmpty()) {
//                try {
//                    Uri uri = Uri.parse(song.getAlbumArt());
//                    albumArt.setImageURI(uri);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    albumArt.setImageResource(R.drawable.ic_music_placeholder);
//                }
//            } else {
//                albumArt.setImageResource(R.drawable.ic_music_placeholder);
//            }
//
//
//
//            itemView.setOnClickListener(v -> {
//                listener.onItemClick(song); // still notifies ViewModel/Fragment if needed
//                int position = getBindingAdapterPosition();
//                // Launch PlayerActivity with song details
//                Context context = itemView.getContext();
//                Log.d("PBK", "bind: "+new ArrayList<>(songList));
//                Log.d("PBK", "bind: "+position);
//                Intent intent = new Intent(context, PlayerActivity.class);
//                intent.putExtra("songs", new ArrayList<>(songList)); // Serializable
//                intent.putExtra("position", position);
//                context.startActivity(intent);
//
//            });
//            // Handle like button click
//            likeBtn.setOnClickListener(v -> {
//                Context context = itemView.getContext();
//                AppDatabase db = AppDatabase.getInstance(context);
//                SongDao dao = db.songDao();
//
//                new Thread(() -> {
//                    Song dbSong = dao.getSongById(song.getId());
//                    if (dbSong != null) {
//                        dbSong.setLiked(!dbSong.isLiked());
//                        dao.updateSong(dbSong);
//
//                        // update UI on main thread
//                        likeBtn.post(() -> {
//                            if (dbSong.isLiked()) {
//                                likeBtn.setImageResource(R.drawable.baseline_favorite_fill);
//                            } else {
//                                likeBtn.setImageResource(R.drawable.baseline_favorite_border);
//                            }
//                        });
//                    }
//                }).start();
//            });
//
//
//
//        }

    }
}
