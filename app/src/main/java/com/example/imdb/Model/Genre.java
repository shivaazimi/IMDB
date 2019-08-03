package com.example.imdb.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Genre extends Model {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public Genre( ) {
        super(GENRE_TYPE);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}