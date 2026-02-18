package com.example.project.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.db.FavoritesDB;
import com.example.project.models.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class detail extends AppCompatActivity {

    private static final String TMDB_KEY = "4d0a7d8a2948c13294b35294af659e04";
    private TextView movieTitle, tvRating, tvDuration, summaryText;
    private ImageView iv_home , iv_search , iv_fav , iv_settings , posterImage , btnFav;
    private ImageButton bt_back;
    private LinearLayout genreLayout;
    private Button btnWatchTrailer;
    private FavoritesDB favDB;
    private String movieTitleStr;
    private int movieId = 0;
    private String rating = "";
    private int posterRes = 0;
    private String posterUrl = null;
    private String currentSummary = "";
    private String currentDuration = "";
    private ArrayList<String> currentGenres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
        bt_back = findViewById(R.id.bt_back);
        iv_home = findViewById(R.id.iv_home);
        iv_search = findViewById(R.id.iv_search);
        iv_fav = findViewById(R.id.iv_fav);
        iv_settings = findViewById(R.id.iv_settings);
        btnWatchTrailer = findViewById(R.id.btnWatchTrailer);
        btnFav = findViewById(R.id.btnFav);
        movieTitle = findViewById(R.id.movieTitle);
        posterImage = findViewById(R.id.posterImage);
        tvRating = findViewById(R.id.tvRating);
        tvDuration = findViewById(R.id.tvDuration);
        summaryText = findViewById(R.id.summaryText);
        genreLayout = findViewById(R.id.genreLayout);

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bln;
                if (favDB.isFav(movieTitleStr)) {
                    favDB.removeFav(movieTitleStr);
                    bln = false;
                } else {
                    Movie favMovie = new Movie(posterRes, movieTitleStr, rating);
                    favMovie.posterUrl = posterUrl;
                    favMovie.summary = currentSummary;
                    favMovie.duration = currentDuration;
                    favMovie.genres = currentGenres;

                    favDB.addMovieToFav(favMovie);
                    bln = true;
                }
                updateFavIcon(bln);
                Toast.makeText(detail.this, bln ? "Added to favorites" : "Removed from favorites",
                        Toast.LENGTH_SHORT).show();
            }
        });

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_home = new Intent(detail.this , MainActivity.class);
                i_home.putExtra("dest" , "home");
                i_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i_home);
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_search = new Intent(detail.this , MainActivity.class);
                i_search.putExtra("dest" , "search");
                i_search.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i_search);            }
        });
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_fav = new Intent(detail.this , MainActivity.class);
                i_fav.putExtra("dest" , "favorites");
                i_fav.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i_fav);
            }
        });
        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_settings = new Intent(detail.this , settings.class);
                startActivity(i_settings);
            }
        });
        btnWatchTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieId != 0) {
                    openTrailer(movieId);
                } else {
                    Toast.makeText(detail.this, "No trailer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle b = getIntent().getExtras();
        if (b == null) {
            Toast.makeText(this, "No details received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        movieId = b.getInt("movie_id", 0);

        String title = b.getString("title", "");
        rating = b.getString("rating", "");
        posterRes = b.getInt("posterRes", 0);

        posterUrl = b.getString("posterUrl", null);

        currentSummary = b.getString("summary", "");
        currentDuration = b.getString("duration", "");
        currentGenres = b.getStringArrayList("genres");
        if (currentGenres == null) currentGenres = new ArrayList<>();

        movieTitleStr = title;

        movieTitle.setText(title);
        tvRating.setText("⭐ " + rating);
        tvDuration.setText("⏱️ " + (currentDuration.isEmpty() ? "N/A" : currentDuration));
        summaryText.setText(currentSummary.isEmpty() ? "No summary available." : currentSummary);

        if (posterRes != 0) {
            posterImage.setImageResource(posterRes);
        } else if (posterUrl != null && !posterUrl.trim().isEmpty()) {
            Glide.with(this).load(posterUrl).into(posterImage);
        } else {
            posterImage.setImageResource(R.drawable.mv);
        }

        bindGenres(currentGenres);

        if (currentSummary.isEmpty() && movieId != 0) {
            fetchDetailsFromTMDB(movieId);
        }

        favDB = new FavoritesDB(this);
        boolean isFav = favDB.isFav(movieTitleStr);
        updateFavIcon(isFav);



    }
    private void bindGenres(ArrayList<String> genres) {
        if (genres == null || genres.isEmpty()) return;
        genreLayout.removeAllViews();
        float d = getResources().getDisplayMetrics().density;
        for (String g : genres) {
            TextView chip = new TextView(this);
            chip.setText(g);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
            chip.setTextColor(0xFFFFFFFF);
            chip.setBackgroundColor(0xFF252836);
            int pad = (int) (8 * d);
            chip.setPadding(pad, pad, pad, pad);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, (int) (8 * d), 0);
            chip.setLayoutParams(lp);
            genreLayout.addView(chip);
        }
    }

    private void updateFavIcon(boolean isFav) {
        if (isFav)
            btnFav.setColorFilter(Color.RED);
        else
            btnFav.setColorFilter(Color.GRAY);

    }

    private void fetchDetailsFromTMDB(int movieId) {

        String url = "https://api.themoviedb.org/3/movie/"+ movieId +"?api_key=" + TMDB_KEY;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,null,res -> {
            String overview = res.optString("overview", "");
            int runtime = res.optInt("runtime", 0);
            summaryText.setText(overview);
            tvDuration.setText(runtime > 0 ? "⏱️ " + runtime + " min" : "⏱️ N/A");
            currentSummary = overview;
            currentDuration = runtime + " min";
            JSONArray gArr = res.optJSONArray("genres");
            ArrayList<String> g = new ArrayList<>();
            if (gArr != null) {
                for (int i = 0; i < gArr.length(); i++) {
                    JSONObject o = gArr.optJSONObject(i);
                    if (o != null)
                        g.add(o.optString("name"));
                }
            }
            currentGenres = g;
            bindGenres(g);
        },err -> Toast.makeText(this,"Error loading details", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(req);
    }

    private void openTrailer(int movieId) {

        String url ="https://api.themoviedb.org/3/movie/"+ movieId +"/videos?api_key=" + TMDB_KEY;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,null,res -> {
            JSONArray results = res.optJSONArray("results");
            if (results == null) return;

            for (int i = 0; i < results.length(); i++) {
                JSONObject v = results.optJSONObject(i);
                if (v == null) continue;
                String site = v.optString("site");
                String key = v.optString("key");
                if ("YouTube".equals(site)) {
                    String yt = "https://www.youtube.com/watch?v=" + key;
                    startActivity(new Intent( Intent.ACTION_VIEW , Uri.parse(yt)));
                    return;
                }
            }
        },err ->Toast.makeText(this,"Trailer not found",Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(req);
    }

    public static Intent makeIntent(Context ctx, Movie m) {

        Intent i = new Intent(ctx, detail.class);

        i.putExtra("movie_id", m.id);
        i.putExtra("title", m.title);
        i.putExtra("rating", m.rating);
        i.putExtra("posterRes", m.posterRes);
        i.putExtra("posterUrl", m.posterUrl);
        i.putExtra("summary", m.summary);
        i.putExtra("duration", m.duration);

        if (m.genres != null) {
            i.putStringArrayListExtra("genres", m.genres);
        }

        return i;
    }}