package com.example.imdb.Model;

public class Model {
    public static final int LOADING_TYPE=0;
    public static final int MOVIE_TYPE=1;
    public static final int CELEBRITY_TYPE=2;
    public static final int TVSHOW_TYPE=3;
    public static final int TMDB_MOVIE_TYPE =4;
    public static final int GENRE_TYPE=5;
    public static final int SEASON_TYPE=6;
    public static final int Filter_TYPE=7;



    public int TYPE;

    public Model(int TYPE){
        this.TYPE=TYPE;
    }

    public int getTYPE() {
        return TYPE;
    }
}
