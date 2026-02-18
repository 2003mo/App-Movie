package com.example.project.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;
import com.example.project.fragments.FavoritesFragment;
import com.example.project.fragments.HomeFragment;
import com.example.project.fragments.SearchFragment;
import com.example.project.fragments.TopRatedFragment;

public class MainActivity extends AppCompatActivity {

    ImageView iv_home, iv_search, iv_fav, iv_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        iv_home = findViewById(R.id.iv_home);
        iv_search = findViewById(R.id.iv_search);
        iv_fav = findViewById(R.id.iv_fav);
        iv_settings = findViewById(R.id.iv_settings);

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer , new SearchFragment())
                        .addToBackStack(null).commit();
            }
        });
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new FavoritesFragment())
                        .addToBackStack(null)
                        .commit();

            }
        });
        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_settings = new Intent(MainActivity.this , settings.class);
                startActivity(i_settings);
            }
        });

        if (savedInstanceState == null) {
            navigateTo(getIntent().getStringExtra("dest"));
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        navigateTo(intent.getStringExtra("dest"));
    }

    private void navigateTo(String dest) {
        if ("search".equals(dest)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new SearchFragment())
                    .commit();
        } else if ("favorites".equals(dest)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new FavoritesFragment())
                    .commit();
        } else if ("top_rated".equals(dest)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new TopRatedFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
        }
    }
}
