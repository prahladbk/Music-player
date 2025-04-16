package com.example.musicplayer.ui.player;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.R;
import com.example.musicplayer.utils.MediaUtils;

public class PlayerActivity extends AppCompatActivity {
    private TextView titleText, artistText;
    private ImageView albumArt;
    private ImageButton playPauseBtn, nextBtn, prevBtn;
    private PlayerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        titleText = findViewById(R.id.songTitle);
        artistText = findViewById(R.id.songArtist);
        albumArt = findViewById(R.id.albumArt);
        playPauseBtn = findViewById(R.id.playPause);
        nextBtn = findViewById(R.id.next);
        prevBtn = findViewById(R.id.previous);

        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        viewModel.getCurrentSong().observe(this, song -> {
            titleText.setText(song.getTitle());
            artistText.setText(song.getArtist());
            Bitmap art = MediaUtils.getAlbumArt(this, song.getData());
            albumArt.setImageBitmap(art);
        });

        playPauseBtn.setOnClickListener(v -> viewModel.togglePlayPause());
        nextBtn.setOnClickListener(v -> viewModel.playNext());
        prevBtn.setOnClickListener(v -> viewModel.playPrevious());
    }
}