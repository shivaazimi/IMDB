package com.example.imdb.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ToggleButton;

import com.example.imdb.Adapter.HorizontalRecyclerAdapter;
import com.example.imdb.Model.Celebrity;
import com.example.imdb.Model.CelebrityResult;
import com.example.imdb.Model.Model;
import com.example.imdb.Model.MovieResult;
import com.example.imdb.Model.MultiMediaType;
import com.example.imdb.Model.MultiSearchResult;
import com.example.imdb.Model.TMDBMovie;
import com.example.imdb.Model.TVShow;
import com.example.imdb.Model.TVShowResult;
import com.example.imdb.R;
import com.example.imdb.Retrofit.ApiCall;
import com.example.imdb.Utility.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    public static final String Movie = "Movie";
    public static final String Celebrity = "Celebrity";
    public static final String TVShow = "TVShow";

    public RecyclerView recyclerView;
    public GridLayoutManager gridLayoutManager;
    public HorizontalRecyclerAdapter recyclerAdapter;
    ToggleButton movie;
    ToggleButton celebrity;
    ToggleButton tvShow;

    ArrayList<Model> models = new ArrayList<>();
    String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final SearchView search = findViewById(R.id.search);

        movie = findViewById(R.id.movie);
        movie.setText(Movie);

        celebrity = findViewById(R.id.celebrity);
        celebrity.setText(Celebrity);

        tvShow = findViewById(R.id.tvShow);
        tvShow.setText(TVShow);


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("onQueryTextSubmit", "*" + s + "*");
                models.clear();
                onBindRecyclerView();
                getData(s);
                search.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    public void onBindRecyclerView() {
        recyclerView = findViewById(R.id.grid_recycler);

        if (models != null)
            //Grid Manager
            gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //RecycleView Adapter
        recyclerAdapter = new HorizontalRecyclerAdapter(this, models);
        recyclerView.setAdapter(recyclerAdapter);

        //item Animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //item Decoration
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), false));

//        recyclerView.setVisibility(View.GONE);
    }

    public void getData(String query) {

        boolean isFiltered = false;
        if (movie.isChecked()) {
            isFiltered = true;
            ApiCall.Factory.TMDB().searchMovie(query).enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    MovieResult movie = response.body();
                    models.addAll(movie.getResults());
                    recyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {

                }
            });
        }
        if (celebrity.isChecked()) {
            isFiltered = true;
            ApiCall.Factory.TMDB().getCelebrity(query).enqueue(new Callback<CelebrityResult>() {
                @Override
                public void onResponse(Call<CelebrityResult> call, Response<CelebrityResult> response) {
                    CelebrityResult celebrity = response.body();
                    models.addAll(celebrity.getResults());
                    recyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<CelebrityResult> call, Throwable t) {
                }
            });
        }
        if (tvShow.isChecked()) {
            isFiltered = true;
            ApiCall.Factory.TMDB().searchTVShow(query).enqueue(new Callback<TVShowResult>() {
                @Override
                public void onResponse(Call<TVShowResult> call, Response<TVShowResult> response) {
                    TVShowResult tvShowResult = response.body();
                    models.addAll(tvShowResult.getResults());
                    recyclerAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<TVShowResult> call, Throwable t) {

                }
            });
        }
        if (!isFiltered) {
            ApiCall.Factory.TMDB().searchMultiType(query).enqueue(new Callback<MultiSearchResult>() {
                @Override
                public void onResponse(Call<MultiSearchResult> call, Response<MultiSearchResult> response) {
                    MultiSearchResult multiSearchResult = response.body();
                    setMediaType(multiSearchResult.getResults());
                }

                @Override
                public void onFailure(Call<MultiSearchResult> call, Throwable t) {

                }
            });
        }
    }

    public void setMediaType(List<MultiMediaType> mediaTypes) {
        for (MultiMediaType mediaType : mediaTypes) {
            if (mediaType.getMediaType().equals("movie"))
                models.add(createTMDBMovie(mediaType));
            else if (mediaType.getMediaType().equals("tv"))
                models.add(createTVShow(mediaType));
            else if (mediaType.getMediaType().equals("person"))
                models.add(createCelebrity(mediaType));
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    public int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()));

    }

    public com.example.imdb.Model.TVShow createTVShow(MultiMediaType mediaType) {
        TVShow tvShow = new TVShow();
        tvShow.setName(mediaType.getName());
        tvShow.setPosterPath(mediaType.getPosterPath());
        tvShow.setOverview(mediaType.getOverview());
        tvShow.setVoteAverage(mediaType.getVoteAverage());
        return tvShow;
    }

    public Celebrity createCelebrity(MultiMediaType mediaType) {
        Celebrity celebrity = new Celebrity();
        celebrity.setId(mediaType.getId());
        celebrity.setName(mediaType.getName());
        celebrity.setProfilePath(mediaType.getProfilePath());
        celebrity.setPopularity(mediaType.getPopularity());
        return celebrity;
    }

    public TMDBMovie createTMDBMovie(MultiMediaType mediaType) {
        TMDBMovie tmdbMovie = new TMDBMovie();
        tmdbMovie.setId(mediaType.getId());
        tmdbMovie.setTitle(mediaType.getTitle());
        tmdbMovie.setOverview(mediaType.getOverview());
        tmdbMovie.setPosterPath(mediaType.getPosterPath());
        tmdbMovie.setVoteAverage(mediaType.getVoteAverage());
        return tmdbMovie;
    }

}
