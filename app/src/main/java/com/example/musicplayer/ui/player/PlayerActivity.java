package com.example.musicplayer.ui.player;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private TextView titleText, artistText, currentTimeText, totalTimeText;
    private ImageView albumArt;
    private ImageButton playPauseBtn, nextBtn, prevBtn;
    private SeekBar seekBar;
    private PlayerViewModel viewModel;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isUserSeeking = false;

    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isUserSeeking) {
                viewModel.updateSeekBar(); // Only update if user isn't dragging
            }
            handler.postDelayed(this, 1000);
        }
    };

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
        seekBar = findViewById(R.id.seekBar);
        currentTimeText = findViewById(R.id.currentTime);
        totalTimeText = findViewById(R.id.totalTime);

        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
            if (checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK}, 1001);
            }
        }

        // Receive songs and selected position
        ArrayList<Song> songs = (ArrayList<Song>) getIntent().getSerializableExtra("songs");
        int currentPosition = getIntent().getIntExtra("position", 0);
        if (songs != null) {
            viewModel.setPlaylist(songs, currentPosition);
        }

        // Observe current song and update UI
        viewModel.getCurrentSong().observe(this, song -> {
            if (song != null) {
                titleText.setText(song.getTitle());
                artistText.setText(song.getArtist());
                albumArt.setImageResource(R.drawable.ic_music_placeholder); // Replace with album art loading logic
            }
        });

        // Observe play/pause state
        viewModel.isPlaying().observe(this, playing -> {
            playPauseBtn.setImageResource(playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
        });

        // Observe position updates
        viewModel.getCurrentPosition().observe(this, position -> {
            if (!isUserSeeking) {
                seekBar.setProgress(position);
                currentTimeText.setText(formatTime(position));
            }
        });

        // Observe duration and update SeekBar max
        viewModel.getDuration().observe(this, duration -> {
            if (duration > 0) {
                seekBar.setMax(duration);
                totalTimeText.setText(formatTime(duration));
            }
        });
        viewModel.getAlbumArtBitmap().observe(this, bitmap -> {
            if (bitmap != null) {
                albumArt.setImageBitmap(bitmap);
            } else {
                albumArt.setImageResource(R.drawable.ic_music_placeholder); // fallback
            }
        });

        // Controls
        playPauseBtn.setOnClickListener(v -> viewModel.togglePlayPause());
        nextBtn.setOnClickListener(v -> viewModel.playNext());
        prevBtn.setOnClickListener(v -> viewModel.playPrevious());

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                viewModel.seekTo(seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTimeText.setText(formatTime(progress));
                }
            }
        });

        // Start updating the SeekBar
        handler.post(updateSeekBarRunnable);
    }

    private String formatTime(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}
