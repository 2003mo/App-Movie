package com.example.project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.activitys.detail;
import com.example.project.models.Movie;

import java.util.List;

public class MoviesRecyclerAdapter extends RecyclerView.Adapter<MoviesRecyclerAdapter.MovieViewHolder> {

    private final Context context;
    private List<Movie> movies;

    public MoviesRecyclerAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    public void update(List<Movie> newList) {
        movies = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_vertical, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.tvTitle.setText(movie.title);
        holder.tvRating.setText("⭐ " + movie.rating);

        if (movie.posterRes != 0) {
            holder.ivPoster.setImageResource(movie.posterRes);
        } else if (movie.posterUrl != null && !movie.posterUrl.trim().isEmpty()) {
            Glide.with(context).load(movie.posterUrl).centerCrop().into(holder.ivPoster);
        } else {
            holder.ivPoster.setImageResource(R.drawable.mv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = detail.makeIntent(context, movie);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (movies == null) ? 0 : movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvRating;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}