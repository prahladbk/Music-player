package com.example.musicplayer.ui.main.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.CommonItem;
import com.example.musicplayer.data.model.Song;
import com.example.musicplayer.data.repository.MusicViewModel;
import com.example.musicplayer.ui.main.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlbumFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicViewModel viewModel;

    public AlbumFragment() {
        super(R.layout.fragment_album);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
        Log.d("PBK", "onViewCreated: AlbumFragment");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class); // Shared ViewModel
        Log.d("PBK", "Album Fragment loaded: ");

        viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            Log.d("PBK", "Observed songs: " + (songs != null ? songs.size() : "null"));
            if (songs == null || songs.isEmpty()) return;

            // Map to hold album name and song count
            Map<String, Integer> albumCountMap = new HashMap<>();

            for (Song song : songs) {
                String album = song.getAlbum();
                albumCountMap.put(album, albumCountMap.getOrDefault(album, 0) + 1);
            }

            List<CommonItem> albumItems = new ArrayList<>();
            for (String album : albumCountMap.keySet()) {
                int count = albumCountMap.get(album);
                albumItems.add(new CommonItem(
                        album,
                        count + " song" + (count > 1 ? "s" : ""),
                        R.drawable.ic_music_placeholder
                ));
            }

            CommonAdapter adapter = new CommonAdapter(albumItems, item -> {
                SongListFragment songsFragment = SongListFragment.newInstance("album", item.getTitle());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, songsFragment)
                        .addToBackStack(null)
                        .commit();
            });

            recyclerView.setAdapter(adapter);
        });
//        }
    }
}


//public class AlbumFragment extends Fragment {
//    private RecyclerView recyclerView;
//    private MusicViewModel viewModel;
//
//    public AlbumFragment() {
//        super(R.layout.fragment_album);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
////        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
////                == PackageManager.PERMISSION_GRANTED) {
//        Log.d("PBK", "onViewCreated: AlbumFragment");
//
//            recyclerView = view.findViewById(R.id.recyclerView);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//            viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class); // Shared ViewModel
//            Log.d("PBK", "Album Fragment loaded: ");
//
//            viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
//                Log.d("PBK", "Observed songs: " + (songs != null ? songs.size() : "null"));
//                if (songs == null || songs.isEmpty()) return;
//
//                List<CommonItem> albumItems = new ArrayList<>();
//                Set<String> albumSet = new HashSet<>();
//
//                for (Song song : songs) {
//                    if (albumSet.add(song.getAlbum())) {
//                        albumItems.add(new CommonItem(song.getAlbum(), song.getArtist(), R.drawable.ic_music_placeholder));
//                    }
//                }
//                CommonAdapter adapter = new CommonAdapter(albumItems, item -> {
//                    SongListFragment songsFragment = SongListFragment.newInstance("album", item.getTitle());
//                    requireActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, songsFragment)
//                            .addToBackStack(null)
//                            .commit();
//                });
//
//                recyclerView.setAdapter(adapter);
//            });
////        }
//    }
//}
