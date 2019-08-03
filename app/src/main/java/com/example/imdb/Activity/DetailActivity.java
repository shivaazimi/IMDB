package com.example.imdb.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imdb.Adapter.HorizontalRecyclerAdapter;
import com.example.imdb.Model.Celebrity;
import com.example.imdb.Model.CelebrityDetail;
import com.example.imdb.Model.CelebrityResult;
import com.example.imdb.Model.Model;
import com.example.imdb.Model.TMDBMovie;
import com.example.imdb.Model.TMDBMovieDetail;
import com.example.imdb.Model.TVShowDetail;
import com.example.imdb.Model.TVShowResult;
import com.example.imdb.Model.Video;
import com.example.imdb.Model.VideoResult;
import com.example.imdb.Model.YoutubeResult;
import com.example.imdb.Model.YoutubeVideo;
import com.example.imdb.R;
import com.example.imdb.Retrofit.ApiCall;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    int type, id;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        type = extras.getInt("TYPE");
        id = extras.getInt("ID");

        if (type == Model.TMDB_MOVIE_TYPE) {
            setContentView(R.layout.movie_detail);
            getMovie(extras.getInt("ID"));
        } else if (type == Model.CELEBRITY_TYPE) {
            setContentView(R.layout.celebrity_detail);
            getPerson(extras.getString("NAME"));
        } else if (type == Model.TVSHOW_TYPE) {
            setContentView(R.layout.tvshow_detail);
            getTVShow(id);
        }else if(type==Model.GENRE_TYPE){

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Details");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void getMovieByGenre(){

    }

    public void getMovie(int id) {
        ApiCall.Factory.TMDB().getMovie(id).enqueue(new Callback<TMDBMovieDetail>() {
            @Override
            public void onResponse(Call<TMDBMovieDetail> call, Response<TMDBMovieDetail> response) {
                TMDBMovieDetail movie = response.body();
                onBindViewMovie(movie);
            }

            @Override
            public void onFailure(Call<TMDBMovieDetail> call, Throwable t) {

            }
        });
    }

    public void getPerson(String name) {

        ApiCall.Factory.TMDB().getCelebrity(name).
                enqueue(new Callback<CelebrityResult>() {
                    @Override
                    public void onResponse(Call<CelebrityResult> call, Response<CelebrityResult> response) {
                        if (response.isSuccessful()) {
                            CelebrityResult celebrityResult = response.body();
                            onBindViewPerson(celebrityResult.getResults().get(0));
                        }
                    }

                    @Override
                    public void onFailure(Call<CelebrityResult> call, Throwable t) {
                    }
                });
    }

    public void getTVShow(int id) {
        ApiCall.Factory.TMDB().getTVShow(id).enqueue(new Callback<TVShowDetail>() {
            @Override
            public void onResponse(Call<TVShowDetail> call, Response<TVShowDetail> response) {
                TVShowDetail tvShowDetail = response.body();
                onBindTVShow(tvShowDetail);
            }

            @Override
            public void onFailure(Call<TVShowDetail> call, Throwable t) {

            }
        });
    }

    public void onBindDetail(String Title, String poster, String Overview, String Title1, String Text1) {

        //Title
        TextView title = findViewById(R.id.title);
        title.setText(Title);
        name = Title;

        //Poster
        String posterPath = "https://image.tmdb.org/t/p/original" + poster;
        final ImageView imageView = findViewById(R.id.poster);
        Picasso.with(getBaseContext()).load(posterPath).into(imageView);

        //OverView
        TextView overview = findViewById(R.id.overview);
        overview.setText(Overview);

        //Part1
        TextView title1 = findViewById(R.id.title1);
        title1.setText(Title1);
        TextView text1 = findViewById(R.id.text1);
        text1.setText(Text1);

    }

    public void onBindViewMovie(TMDBMovieDetail movie) {

        onBindDetail(movie.getTitle(), movie.getPosterPath(), movie.getOverview(), "Release Date",
                movie.getReleaseDate().replaceAll("-", "/"));

        //Time
        TextView runtime = findViewById(R.id.title2);
        runtime.setText("Runtime");
        TextView time = findViewById(R.id.text2);
        time.setText(movie.getRuntime() / 60 + " hr " + movie.getRuntime() % 60 + " min");

        //Trailer
        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(ApiCall.YOUTUBE_API_KEY, this);

        //Rating
        RatingBar rating = findViewById(R.id.ratingBar);
        double average = movie.getVoteAverage() / 10;
        rating.setRating((float) average);

        TextView averageVote = findViewById(R.id.averageVote);
        averageVote.setText(movie.getVoteAverage() + "");
        TextView totalVote = findViewById(R.id.totalVote);
        totalVote.setText(movie.getVoteCount() + "");

        //Genre
        TextView genreTitle = findViewById(R.id.recycleTitle);
        genreTitle.setText("Genre");
        onBindRecyclerList((ArrayList) movie.getGenres(), "recyclerView");
        layout_below("trailerContainer", "list");


    }

    public void layout_below(String below, String id) {
        RelativeLayout relativeLayout = findViewById(getResources().getIdentifier(id, "id", getPackageName()));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, getResources().getIdentifier(below, "id", getPackageName()));
    }

    public void onBindViewPerson(final Celebrity celebrity) {


        //Known For
        onBindRecyclerList((ArrayList) celebrity.getKnownFor(), "recyclerView");
        layout_below("background", "list");

        //gender
        TextView genderTitle = findViewById(R.id.title2);
        genderTitle.setText("Gender");
        final TextView gender = findViewById(R.id.text2);

        //popularity
        TextView popularityTitle = findViewById(R.id.totalVote);
        popularityTitle.setText("Popularity");
        final TextView popularity = findViewById(R.id.averageVote);

        //rating
        RatingBar rating = findViewById(R.id.ratingBar);
        rating.setRating(1);

        //RecyclerTitle
        TextView recuclerTitle = findViewById(R.id.recycleTitle);
        recuclerTitle.setText("Known For");

        ApiCall.Factory.TMDB().getPerson(celebrity.getId()).enqueue(new Callback<CelebrityDetail>() {
            @Override
            public void onResponse(Call<CelebrityDetail> call, Response<CelebrityDetail> response) {
                CelebrityDetail celebrityDetail = response.body();
                String birthday = "";
                if (celebrityDetail.getBirthday() != null)
                    birthday = (String) celebrityDetail.getBirthday();

                onBindDetail(celebrity.getName(), celebrity.getProfilePath(), celebrityDetail.getBiography(),
                        "birthDay", birthday.replaceAll("-", "/"));

                popularity.setText(celebrityDetail.getPopularity() + "");
                if (celebrityDetail.getGender() == 1)
                    gender.setText("Femail");
                else
                    gender.setText("Mail");
            }

            @Override
            public void onFailure(Call<CelebrityDetail> call, Throwable t) {

            }
        });

    }

    public void onBindTVShow(TVShowDetail tvshow) {

        onBindDetail(tvshow.getName(), tvshow.getPosterPath(), tvshow.getOverview(), "First Air Date"
                , tvshow.getFirstAirDate().replaceAll("-", "/"));

        //Season
        TextView season = findViewById(R.id.title2);
        season.setText("Number of Season");
        TextView time = findViewById(R.id.text2);
        time.setText(tvshow.getNumberOfSeasons() + "");

        //Rating
        RatingBar rating = findViewById(R.id.ratingBar);
        double average = tvshow.getVoteAverage() / 10;
        rating.setRating((float) average);

        TextView averageVote = findViewById(R.id.averageVote);
        averageVote.setText(tvshow.getVoteAverage() + "");
        TextView totalVote = findViewById(R.id.totalVote);
        totalVote.setText(tvshow.getVoteCount() + "");

        //Trailer
        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(ApiCall.YOUTUBE_API_KEY, this);

        layout_below("list", "trailerContainer");

        //RecyclerTitle
        TextView recuclerTitle = findViewById(R.id.recycleTitle);
        recuclerTitle.setText("Season");
        //GenreList
        onBindRecyclerList((ArrayList) tvshow.getSeasons(), "recyclerView");
        layout_below("background", "list");
        //SeasonList
        onBindRecyclerList((ArrayList) tvshow.getGenres(), "seasonRecycler");
    }

    public void onBindRecyclerList(ArrayList list, String id) {

        //RecycleView
        RecyclerView recyclerView = findViewById(getResources().getIdentifier(id, "id", getPackageName()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        //RecycleViewAdapter
        final HorizontalRecyclerAdapter recyclerAdapter = new HorizontalRecyclerAdapter(this, list);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerAdapter);

        //item Animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }



    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {

        if (type == Model.TMDB_MOVIE_TYPE) {
            ApiCall.Factory.TMDB().getMovieVideo(id).enqueue(new Callback<VideoResult>() {
                @Override
                public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
                    VideoResult videoResult = response.body();
                    List<Video> videos = videoResult.getResults();
                    if (videos.size() > 0)
                        youTubePlayer.loadVideo(videos.get(0).getKey());
                }

                @Override
                public void onFailure(Call<VideoResult> call, Throwable t) {

                }
            });
        } else if (type == Model.TVSHOW_TYPE) {
            ApiCall.Factory.TMDB().getTvVideo(id).enqueue(new Callback<VideoResult>() {
                @Override
                public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
                    VideoResult videoResult = response.body();
                    List<Video> videos = videoResult.getResults();
                    if (videos.size() > 0)
                        youTubePlayer.loadVideo(videos.get(0).getKey());

                }

                @Override
                public void onFailure(Call<VideoResult> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            String errorMessage = String.format("couldn't load Trailer.", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
