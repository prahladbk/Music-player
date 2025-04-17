package com.example.musicplayer.ui.player;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.utils.MediaUtils;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private TextView titleText, artistText;
    private ImageView albumArt;
    private ImageButton playPauseBtn, nextBtn, prevBtn;
    private PlayerViewModel viewModel;

    private ArrayList<Song> songs;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Init UI
        titleText = findViewById(R.id.songTitle);
        artistText = findViewById(R.id.songArtist);
        albumArt = findViewById(R.id.albumArt);
        playPauseBtn = findViewById(R.id.playPause);
        nextBtn = findViewById(R.id.next);
        prevBtn = findViewById(R.id.previous);

        // Get data from intent
//        songs = (ArrayList<Song>) getIntent().getSerializableExtra("songs");
//        Log.d("PBK", "onCreate: playerActivity"+songs);
//        currentPosition = getIntent().getIntExtra("position", 0);
//        Log.d("PBK", "onCreate: playerActivity"+currentPosition);

        try {
            songs = (ArrayList<Song>) getIntent().getSerializableExtra("songs");
            currentPosition = getIntent().getIntExtra("position", 0);
            Log.d("PBK", "onCreate: Songs received = " + songs);
            Log.d("PBK", "onCreate: Songs received = " + currentPosition);
        } catch (Exception e) {
            Log.e("PBK", "Error retrieving intent extras", e);
        }
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        if (songs != null && !songs.isEmpty()) {

            viewModel.setPlaylist(songs, currentPosition);
            Log.d("PBK", "if view model ");
        }

        Log.d("PBK", "onCreate: in view");

        // ViewModel
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        // Observe current song changes
        viewModel.getCurrentSong().observe(this, song -> {
            if (song != null) {
                titleText.setText(song.getTitle());
                artistText.setText(song.getArtist());
                Log.d("PBK", "onCreate: vm test");

                if (song.getAlbumArt() != null && !song.getAlbumArt().isEmpty()) {
                    try {
                        Uri albumUri = Uri.parse(song.getAlbumArt());
                        albumArt.setImageURI(albumUri);
                    } catch (Exception e) {
                        albumArt.setImageResource(R.drawable.ic_music_placeholder);
                        e.printStackTrace();
                    }
                } else {
                    albumArt.setImageResource(R.drawable.ic_music_placeholder);
                }
            }
        });
        viewModel.isPlaying().observe(this, isPlaying -> {
            if (isPlaying != null) {
                if (isPlaying) {
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_pause); // your pause icon
                } else {
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_play); // your play icon
                }
            }
        });



        // Playback controls
        playPauseBtn.setOnClickListener(v -> viewModel.togglePlayPause());
        nextBtn.setOnClickListener(v -> viewModel.playNext());
        prevBtn.setOnClickListener(v -> viewModel.playPrevious());
    }

    private void playSongAtPosition(int position) {
        if (songs != null && position >= 0 && position < songs.size()) {
            currentPosition = position;
            Song currentSong = songs.get(position);
            viewModel.setCurrentSong(currentSong);
        }
    }
}
