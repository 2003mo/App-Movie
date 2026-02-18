package com.example.project.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.MoviesRecyclerAdapter;
import com.example.project.db.FavoritesDB;
import com.example.project.models.Movie;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private RecyclerView rv;
    private TextView tvEmpty;

    private MoviesRecyclerAdapter moviesRecyclerAdapter;
    private final ArrayList<Movie> favs = new ArrayList<>();

    private FavoritesDB db;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle s) {

        View v = inf.inflate(R.layout.fragment_favorites, container, false);

        tvEmpty = v.findViewById(R.id.tvEmpty);
        rv = v.findViewById(R.id.rvFavs);

        db = new FavoritesDB(requireContext());

        moviesRecyclerAdapter = new MoviesRecyclerAdapter(requireContext(), favs);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(moviesRecyclerAdapter);

        refresh();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
    private void refresh() {
        favs.clear();
        favs.addAll(db.getAllFavMovies());

        if (moviesRecyclerAdapter != null) {
            moviesRecyclerAdapter.update(favs);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(favs.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}