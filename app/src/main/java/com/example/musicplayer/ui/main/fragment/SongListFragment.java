package com.example.musicplayer.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.data.repository.MusicViewModel;
import com.example.musicplayer.ui.main.adapter.SongAdapter;

import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends Fragment {
    private static final String ARG_FILTER_TYPE = "filter_type";
    private static final String ARG_FILTER_VALUE = "filter_value";

    private RecyclerView recyclerView;
    private MusicViewModel viewModel;

    public static SongListFragment newInstance(String filterType, String filterValue) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER_TYPE, filterType);
        args.putString(ARG_FILTER_VALUE, filterValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);

        String filterType = getArguments().getString(ARG_FILTER_TYPE);
        String filterValue = getArguments().getString(ARG_FILTER_VALUE);

        viewModel.getSongs().observe(getViewLifecycleOwner(), allSongs -> {
            List<Song> filteredSongs = new ArrayList<>();
            for (Song song : allSongs) {
                if ("album".equals(filterType) && song.getAlbum().equals(filterValue)) {
                    filteredSongs.add(song);
                } else if ("artist".equals(filterType) && song.getArtist().equals(filterValue)) {
                    filteredSongs.add(song);
                } else if ("liked".equals(filterType) && song.isLiked()) {
                    filteredSongs.add(song);
                }
            }
            Log.d("PBK", "Filtered songs size: " + filteredSongs.size());


            SongAdapter adapter = new SongAdapter(requireContext(), filteredSongs, song -> {
                // Handle song click (e.g., play song or open PlayerActivity)
            });

            recyclerView.setAdapter(adapter);
        });
    }
}