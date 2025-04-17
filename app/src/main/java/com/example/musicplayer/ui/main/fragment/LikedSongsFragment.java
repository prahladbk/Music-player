package com.example.musicplayer.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.data.repository.MusicViewModel;
import com.example.musicplayer.ui.main.adapter.SongAdapter;

import java.util.List;

public class LikedSongsFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicViewModel viewModel;

    public LikedSongsFragment() {
        super(R.layout.fragment_liked_songs);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);

        viewModel.getLikedSongs().observe(getViewLifecycleOwner(), likedSongs -> {
            if (likedSongs == null || likedSongs.isEmpty()) {
                Toast.makeText(getContext(), "No liked songs found", Toast.LENGTH_SHORT).show();
                return;
            }

            SongAdapter adapter = new SongAdapter(requireContext(), likedSongs, song -> {
                // Optionally handle song click here (already handled in adapter to launch PlayerActivity)
            });

            recyclerView.setAdapter(adapter);
        });
    }
}
