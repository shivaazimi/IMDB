package com.example.imdb.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imdb.Adapter.VerticalRecyclerAdapter;
import com.example.imdb.Interface.OnLoadMoreListener;
import com.example.imdb.Model.Celebrity;
import com.example.imdb.Model.CelebrityResult;
import com.example.imdb.Model.Genre;
import com.example.imdb.Model.GenreResult;
import com.example.imdb.Model.MovieResult;
import com.example.imdb.Model.ParentModel;
import com.example.imdb.Model.TMDBMovie;
import com.example.imdb.Model.TVShow;
import com.example.imdb.Model.TVShowResult;
import com.example.imdb.R;
import com.example.imdb.Retrofit.ApiCall;
import com.example.imdb.Utility.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class HomePageFragment extends Fragment {

    public RecyclerView recyclerView;
    public VerticalRecyclerAdapter recyclerAdapter;
    public ArrayList<ParentModel> models;
    ArrayList<Genre> genres;
    int dataFetched=0;

    private OnLoadMoreListener mOnLoadMoreListener;


    public boolean reachEnd;
    public int loadedPage;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_reycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.vertical_recycler);


        initialize();


    }

    public void onDataFetched(int dataFetched){
        if (dataFetched==4) {
            //Grid Manager
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);


            //RecycleView Adapter
            recyclerAdapter = new VerticalRecyclerAdapter(getContext(), models);
            recyclerView.setAdapter(recyclerAdapter);

            //item Animator
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            //item Decoration
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(7), false));
        }
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void initialize() {
        models = new ArrayList<>();
        ParentModel.Type[] types = ParentModel.Type.values();
        for (ParentModel.Type type : types) {
            ParentModel model = new ParentModel();
            switch (type) {
                case Movie:
                    model.setTitle(ParentModel.Type.Movie + "");
                    model.setModels(getMovies());
                    break;
                case Celebrity:
                    model.setTitle(ParentModel.Type.Celebrity + "");
                    model.setModels(getCelebrities());
                    break;
                case TVShow:
                    model.setTitle(ParentModel.Type.TVShow + "");
                    model.setModels(getTVShows());
                    break;
                case GENRE:
                    model.setTitle("Genre");
                    model.setModels(getGenre());
                    break;
            }
            models.add(model);
        }
    }

    public List getMovies() {
        loadedPage = 1;
        final ArrayList<TMDBMovie> tmdbMovies = new ArrayList<>();
        ApiCall.Factory.TMDB().popularMovie(loadedPage).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult movieResult = response.body();
                tmdbMovies.addAll(movieResult.getResults());
                onDataFetched(++dataFetched);
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {

            }
        });
        return tmdbMovies;
    }

    public List getCelebrities() {
        loadedPage = 1;
        final ArrayList<Celebrity> celebrities = new ArrayList<>();
        ApiCall.Factory.TMDB().popularPerson(loadedPage).enqueue(new Callback<CelebrityResult>() {

            @Override
            public void onResponse(Call<CelebrityResult> call, Response<CelebrityResult> response) {
                CelebrityResult celebrityResult = response.body();
                celebrities.addAll(celebrityResult.getResults());
                onDataFetched(++dataFetched);
            }

            @Override
            public void onFailure(Call<CelebrityResult> call, Throwable t) {
            }
        });
        return celebrities;
    }

    public List getTVShows() {
        loadedPage = 1;
        final ArrayList<TVShow> tvShows = new ArrayList<>();
        ApiCall.Factory.TMDB().popularTVShow(loadedPage).enqueue(new Callback<TVShowResult>() {
            @Override
            public void onResponse(Call<TVShowResult> call, Response<TVShowResult> response) {
                TVShowResult tvShowResult = response.body();
                tvShows.addAll(tvShowResult.getResults());
                onDataFetched(++dataFetched);
            }

            @Override
            public void onFailure(Call<TVShowResult> call, Throwable t) {
            }
        });
        return tvShows;
    }

    public List getGenre() {
        genres = new ArrayList<>();
        ApiCall.Factory.TMDB().getGenres().enqueue(new Callback<GenreResult>() {
            @Override
            public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                GenreResult genreResult=response.body();
                for(int i=0;i<13;i++){
                    genres.add(genreResult.getGenres().get(i));
                }
                onDataFetched(++dataFetched);
            }

            @Override
            public void onFailure(Call<GenreResult> call, Throwable t) {
            }
        });
        return genres;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnLoadMoreListener = null;
    }

    public int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

}
