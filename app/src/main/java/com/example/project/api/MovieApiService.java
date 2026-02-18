package com.example.project.api;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.project.models.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieApiService {

    public interface MoviesCallback {
        void onSuccess(ArrayList<Movie> movies);
        void onError(String error);
    }

    private static final String API_KEY = "4d0a7d8a2948c13294b35294af659e04";
    private static final String IMG_BASE = "https://image.tmdb.org/t/p/w500";

    public static void fetchMovies(MoviesCallback cb) {
        String url = AppController.BASE_URL + "movie/popular?api_key=" + API_KEY;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        ArrayList<Movie> list = new ArrayList<>();

                        JSONArray results = response.optJSONArray("results");
                        if (results == null) {
                            cb.onError("No results");
                            return;
                        }

                        int limit = Math.min(results.length(), 30);

                        for (int i = 0; i < limit; i++) {
                            JSONObject obj = results.getJSONObject(i);

                            int id = obj.optInt("id", 0);
                            String title = obj.optString("title", "No Title");
                            double vote = obj.optDouble("vote_average", 0.0);
                            String rating = String.valueOf(vote);

                            String posterPath = obj.optString("poster_path", "");
                            String posterUrl = posterPath.isEmpty() ? null : (IMG_BASE + posterPath);

                            Movie m = new Movie(id, 0 , title, rating, posterUrl);

                            // اختياري: overview كسُمري مبدئي (قبل details)
                            m.summary = obj.optString("overview", "");

                            list.add(m);
                        }

                        cb.onSuccess(list);

                    } catch (Exception e) {
                        cb.onError(e.getMessage());
                    }
                },
                error -> cb.onError(String.valueOf(error))
        );

        AppController.getInstance().addToRequestQueue(req, "tmdb_popular");
    }
}