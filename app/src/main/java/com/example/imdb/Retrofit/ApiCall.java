package com.example.imdb.Retrofit;

import android.content.Intent;
import android.support.v7.widget.CardView;

import com.example.imdb.Model.CelebrityDetail;
import com.example.imdb.Model.CelebrityResult;
import com.example.imdb.Model.Genre;
import com.example.imdb.Model.GenreResult;
import com.example.imdb.Model.MovieResult;
import com.example.imdb.Model.MultiSearchResult;
import com.example.imdb.Model.OMDBMovie;
import com.example.imdb.Model.SearchResult;
import com.example.imdb.Model.TMDBMovieDetail;
import com.example.imdb.Model.TVShowDetail;
import com.example.imdb.Model.TVShowResult;
import com.example.imdb.Model.VideoResult;
import com.example.imdb.Model.YoutubeResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCall {

    //OMDB
    String OMDB_BASE_URL = "http://www.omdbapi.com";
    String OMDB_API_KEY ="?apikey="+"36b6dc1f"+"&";


    @GET("/"+ OMDB_API_KEY)
    Call<SearchResult> search(@Query("s") String query, @Query("type") String type, @Query("page") int page);

    @GET("/"+ OMDB_API_KEY)
    Call<OMDBMovie> getMovie(@Query("i") String imdbId);


    //TMDB
    String TMDB_BASE_URL="https://api.themoviedb.org/3/";
    String TMDB_API_KEY="?api_key="+"424071afff63d5e333e67ffc70d38502"+"&";

    @GET("person/popular"+ TMDB_API_KEY)
    Call<CelebrityResult> popularPerson(@Query("page") int page);

    @GET("movie/top_rated"+ TMDB_API_KEY)
    Call<MovieResult> popularMovie(@Query("page") int page);

    @GET("tv/popular"+TMDB_API_KEY)
    Call<TVShowResult> popularTVShow(@Query("page") int page);

    @GET("genre/movie/list"+TMDB_API_KEY)
    Call<GenreResult> getGenres();

    @GET("movie/{movie_id}"+TMDB_API_KEY)
    Call<TMDBMovieDetail> getMovie(@Path("movie_id") int id);

    @GET("person/{person_id}"+TMDB_API_KEY)
    Call<CelebrityDetail> getPerson(@Path("person_id") int id);

    @GET("tv/{tv_id}"+TMDB_API_KEY)
    Call<TVShowDetail> getTVShow(@Path("tv_id") int id);

    @GET("search/person"+TMDB_API_KEY)
    Call<CelebrityResult> getCelebrity(@Query("query") String query);


    @GET("movie/{movie_id}/videos"+TMDB_API_KEY)
    Call<VideoResult> getMovieVideo(@Path("movie_id") int id);


    @GET(" tv/{tv_id}/videos"+TMDB_API_KEY)
    Call<VideoResult> getTvVideo(@Path("tv_id") int id);

    @GET("search/movie"+ TMDB_API_KEY)
    Call<MovieResult> searchMovie(@Query("query") String query);

    @GET("search/person"+ TMDB_API_KEY)
    Call<CelebrityResult> searchCelebrity(@Query("query") String query);

    @GET("search/tv"+ TMDB_API_KEY)
    Call<TVShowResult> searchTVShow(@Query("query") String query);

    @GET("search/multi"+ TMDB_API_KEY)
    Call<MultiSearchResult> searchMultiType(@Query("query") String query);

    @GET("discover/movie"+ TMDB_API_KEY)
    Call<MovieResult> searchGenre(@Query("with_genres") String genre);


    //YOUTUBE
    public static final String YOUTUBE_API_KEY="?key="+"AIzaSyDTDx5J_-jzn9mdX1sCJT16os94RBeL7a8"+"&";
    String YOUTUBE_BASE_URL="https://www.googleapis.com/youtube/v3/";


    @GET("search"+YOUTUBE_API_KEY)
    Call<YoutubeResult> searchVideo(@Query("part") String part,
                                    @Query("order") String order,
                                    @Query("q") String query,
                                    @Query("type") String type);

    class Factory {
        public static ApiCall omdb;
        public static ApiCall tmdb;
        public static ApiCall youtube;

        public static ApiCall OMDB() {
            if (omdb == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create()).baseUrl(OMDB_BASE_URL).build();
                omdb = retrofit.create(ApiCall.class);
                return omdb;
            } else {
                return omdb;
            }
        }

        public static ApiCall TMDB() {
            if (tmdb == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create()).baseUrl(TMDB_BASE_URL).build();
                tmdb = retrofit.create(ApiCall.class);
                return tmdb;
            } else {
                return tmdb;
            }
        }

        public static ApiCall YOUTUBE() {
            if (youtube == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create()).baseUrl(YOUTUBE_BASE_URL).build();
                youtube = retrofit.create(ApiCall.class);
                return youtube;
            } else {
                return youtube;
            }
        }
    }
}

