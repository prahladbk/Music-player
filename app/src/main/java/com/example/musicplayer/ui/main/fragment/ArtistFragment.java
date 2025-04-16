package com.example.musicplayer.ui.main.fragment;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.CommonItem;
import com.example.musicplayer.ui.main.adapter.CommonAdapter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ArtistFragment extends Fragment {
    private RecyclerView recyclerView;

    public ArtistFragment() {
        super(R.layout.fragment_artist);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Setup adapter here
        List<CommonItem> albumList = new ArrayList<>();
        albumList.add(new CommonItem("Album One", "10 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Two", "8 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Three", "12 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album One", "10 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Two", "8 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Three", "12 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album One", "10 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Two", "8 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Three", "12 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album One", "10 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Two", "8 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Three", "12 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album One", "10 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Two", "8 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Three", "12 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album One", "10 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Two", "8 songs", R.drawable.ic_music_placeholder));
        albumList.add(new CommonItem("Album Three", "12 songs", R.drawable.ic_music_placeholder));

        CommonAdapter adapter = new CommonAdapter(albumList, item -> {
            // Handle click event for album
            Toast.makeText(getContext(), "Clicked: ", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
    }
}