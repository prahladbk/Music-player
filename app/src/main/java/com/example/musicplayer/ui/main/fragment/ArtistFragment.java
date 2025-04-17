package com.example.musicplayer.ui.main.fragment;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.CommonItem;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.data.repository.MusicViewModel;
import com.example.musicplayer.ui.main.adapter.CommonAdapter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArtistFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicViewModel viewModel;

    public ArtistFragment() {
        super(R.layout.fragment_artist);
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);

        viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs == null || songs.isEmpty()) return;

            // Map to hold artist name and song count
            Map<String, Integer> artistCountMap = new HashMap<>();

            for (Song song : songs) {
                String artist = song.getArtist();
                artistCountMap.put(artist, artistCountMap.getOrDefault(artist, 0) + 1);
            }

            List<CommonItem> artistList = new ArrayList<>();
            for (String artist : artistCountMap.keySet()) {
                int count = artistCountMap.get(artist);
                artistList.add(new CommonItem(
                        artist,
                        count + " song" + (count > 1 ? "s" : ""),
                        R.drawable.ic_music_placeholder
                ));
            }

            CommonAdapter adapter = new CommonAdapter(artistList, item -> {
                SongListFragment songsFragment = SongListFragment.newInstance("artist", item.getTitle());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, songsFragment)
                        .addToBackStack(null)
                        .commit();
            });

            recyclerView.setAdapter(adapter);
        });
    }
}



//public class ArtistFragment extends Fragment {
//    private RecyclerView recyclerView;
//    private MusicViewModel viewModel;
//
//    public ArtistFragment() {
//        super(R.layout.fragment_artist);
//    }
//
//    @Override
//    public void onViewCreated(@androidx.annotation.NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
//
//        viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
//            if (songs == null || songs.isEmpty()) return;
//
//            List<CommonItem> artistList = new ArrayList<>();
//            Set<String> artistSet = new HashSet<>();
//
//            for (Song song : songs) {
//                if (artistSet.add(song.getArtist())) {
//                    artistList.add(new CommonItem(song.getArtist(), "Artist", R.drawable.ic_music_placeholder));
//                }
//            }
//
//            CommonAdapter adapter = new CommonAdapter(artistList, item -> {
//                SongListFragment songsFragment = SongListFragment.newInstance("artist", item.getTitle());
//                requireActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, songsFragment)
//                        .addToBackStack(null)
//                        .commit();
//            });
//
//            recyclerView.setAdapter(adapter);
//        });
//    }
//}
