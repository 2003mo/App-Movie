package com.example.project.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.MoviesRecyclerAdapter;
import com.example.project.api.MovieApiService;
import com.example.project.data.MoviesRepository;
import com.example.project.models.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopRatedFragment extends Fragment {
    RecyclerView rv;
    private MoviesRecyclerAdapter adapter;
    private ArrayList<Movie> top = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle s) {
        View v = inf.inflate(R.layout.fragment_top_rated, container, false);
        rv = v.findViewById(R.id.rvTop);

        adapter = new MoviesRecyclerAdapter(requireContext(), top);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        MovieApiService.fetchMovies(new MovieApiService.MoviesCallback() {
            @Override
            public void onSuccess(ArrayList<Movie> movies) {
                ArrayList<Movie> local = MoviesRepository.getAll();
                top.clear();
                top.addAll(local);
                top.addAll(movies);

                sortRatingDesc(top);
                adapter.update(top);
            }
            @Override
            public void onError(String error) {
                top.clear();
                top.addAll(MoviesRepository.getAll());
                sortRatingDesc(top);
                adapter.update(top);
            }
        });
        return v;
    }
    private void sortRatingDesc(ArrayList<Movie> list) {
        Collections.sort(list, new Comparator<Movie>() {
            @Override
            public int compare(Movie a, Movie b) {
                float ra = getRating(a);
                float rb = getRating(b);
                return Float.compare(rb, ra);
            }
        });
    }
    private float getRating(Movie m) {
        try {
            return Float.parseFloat(m.rating);
        } catch (Exception e) {
            return 0f;
        }
    }
}