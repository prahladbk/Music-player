package com.example.musicplayer.ui.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;

import com.example.musicplayer.R;
import com.example.musicplayer.ui.main.fragment.AlbumFragment;
import com.example.musicplayer.ui.main.fragment.ArtistFragment;
import com.example.musicplayer.ui.main.fragment.LikedSongsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.musicplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Log.d("PBK", "onCreate: "+R.id.menu_albums);
        Log.d("PBK", "onCreate: "+R.id.menu_liked);
        Log.d("PBK", "onCreate: "+R.id.menu_artists);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case 2131231008:
                    loadFragment(new AlbumFragment());
                    return true;
                case 2131231009:
                    loadFragment(new ArtistFragment());
                    return true;
                case 2131231010:
                    loadFragment(new LikedSongsFragment());
                    return true;
            }
            return false;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            // Load AlbumFragment temporarily until permission is granted
            loadFragment(new AlbumFragment());
        } else {
            bottomNavigationView.setSelectedItemId(R.id.menu_albums);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bottomNavigationView.setSelectedItemId(R.id.menu_albums);
            } else {
                // Optionally show a message or close the app
                Log.e("MainActivity", "Permission denied. Cannot load songs.");
            }
        }
    }



    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}