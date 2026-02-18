package com.example.project.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapters.MoviesRecyclerAdapter;
import com.example.project.api.MovieApiService;
import com.example.project.data.MoviesRepository;
import com.example.project.models.Movie;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    EditText et_search_home;
    Button btn_top_rated;
    RecyclerView recyclerView;
    private MoviesRecyclerAdapter moviesRecyclerAdapter;
    private ArrayList<Movie> movieArrayList;
    private ArrayList<Movie> moviefilter;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle s) {
        View v = inf.inflate(R.layout.activity_home_fragment, container, false);

        et_search_home = v.findViewById(R.id.et_search_home);
        btn_top_rated = v.findViewById(R.id.btn_top_rated);
        recyclerView = v.findViewById(R.id.recycler_movies);

        movieArrayList = new ArrayList<>();
        moviefilter = new ArrayList<>();

        moviesRecyclerAdapter = new MoviesRecyclerAdapter(requireContext(), moviefilter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(moviesRecyclerAdapter);

        MovieApiService.fetchMovies(new MovieApiService.MoviesCallback() {
            @Override
            public void onSuccess(ArrayList<Movie> movies) {
                ArrayList<Movie> local = MoviesRepository.getAll();

                movieArrayList.clear();
                movieArrayList.addAll(local);
                movieArrayList.addAll(movies);

                moviefilter.clear();
                moviefilter.addAll(movieArrayList);

                moviesRecyclerAdapter.update(moviefilter);
            }

            @Override
            public void onError(String error) {
                movieArrayList = MoviesRepository.getAll();
                moviefilter.clear();
                moviefilter.addAll(movieArrayList);
                moviesRecyclerAdapter.update(moviefilter);
            }
        });

        et_search_home.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        btn_top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new TopRatedFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        return v;
    }
    private void filter(String text) {
        moviefilter.clear();
        if (text == null || text.isEmpty()) {
            moviefilter.addAll(movieArrayList);

        } else {
            text = text.toLowerCase();
            for (Movie movie : movieArrayList) {
                if (movie.title != null && movie.title.toLowerCase().contains(text)) {
                    moviefilter.add(movie);
                }
            }
        }
        moviesRecyclerAdapter.update(moviefilter);
    }
}