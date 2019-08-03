package com.example.imdb.Activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.imdb.Adapter.ViewPagerAdapter;
import com.example.imdb.Fragment.GridRecyclerFragment;
import com.example.imdb.Fragment.HomePageFragment;
import com.example.imdb.Model.Model;
import com.example.imdb.Model.OMDBMovie;
import com.example.imdb.Model.SearchResult;
import com.example.imdb.R;
import com.example.imdb.Retrofit.ApiCall;
import com.example.imdb.Interface.OnLoadMoreListener;
import com.example.imdb.Utility.NetworkReceiver;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;
    public Toolbar toolbar;
    public ViewPager viewPager;
    public TabLayout tabLayout;
    public SearchView searchView;
    public NetworkReceiver receiver;

    //Fragments
    public GridRecyclerFragment gridFragment;
    public HomePageFragment homePageFragment;

    //OMDBMovie Tab
    public static ArrayList<Model> movies;
    public SearchResult searchResult;
    public final String TAG = "MainActivity";

    public boolean reachEnd;
    public String latestQuery;
    public int loadedPage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        movies = new ArrayList<>();

        gridFragment = new GridRecyclerFragment();
        homePageFragment = new HomePageFragment();
        setUpLazyLoad();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
        progressDialog.setCancelable(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        //Network Connection
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

    }

    public void setUpLazyLoad() {
        gridFragment.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!reachEnd) {
//                    movies.add(null);
//                    gridFragment.recyclerAdapter.notifyItemInserted(movies.size() - 1);
                    getData(latestQuery, false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),SearchActivity.class);
                startActivity(intent);
            }
        });

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity.this.latestQuery = query;
                getData(query, true);
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    public void getData(final String query, boolean newQuery) {
        if (newQuery) {
            movies.clear();
            gridFragment.recyclerAdapter.notifyDataSetChanged();
            loadedPage = 0;
            reachEnd = false;
            ApiCall.Factory.OMDB().search(query, "movie", 1).enqueue(new Callback<SearchResult>() {
                @Override
                public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                    searchResult = response.body();
                    if (searchResult.getResponse().equals("True")) {
                        //OMDBMovie Found
                        loadedPage = 1;
                        getMovies();
                        gridFragment.message.setVisibility(View.GONE);
                        gridFragment.recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        //OMDBMovie not found
                        progressDialog.dismiss();
                        gridFragment.message.setText("No movies found. Try again.");
                        gridFragment.message.setVisibility(View.VISIBLE);
                        gridFragment.recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable t) {
                    Log.e("MainActivity", "Failure : " + t.getMessage());
                    progressDialog.dismiss();
                    gridFragment.message.setText("Query request failed. Try again");
                    gridFragment.message.setVisibility(View.VISIBLE);
                    gridFragment.recyclerView.setVisibility(View.GONE);
                }
            });
        } else {
            if (!reachEnd) {
                loadedPage++;
                ApiCall.Factory.OMDB().search(query, "movie", loadedPage).enqueue(new Callback<SearchResult>() {
                    @Override
                    public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                        searchResult = response.body();
                        if (searchResult.getResponse().equals("True")) {
                            //OMDBMovie Found
                            getMovies();
                        } else {
                            //Reached End
                            movies.remove(movies.size() - 1);
                            reachEnd = true;
                            gridFragment.recyclerAdapter.notifyItemRemoved(movies.size());
                            gridFragment.recyclerAdapter.notifyDataSetChanged();
                            gridFragment.setLoaded();
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResult> call, Throwable t) {
                        movies.remove(movies.size() - 1);
                        gridFragment.recyclerAdapter.notifyItemRemoved(movies.size());
                        gridFragment.recyclerAdapter.notifyDataSetChanged();
                        gridFragment.setLoaded();
                    }
                });
            }
        }
    }

    public void getMovies() {
        final int[] count = {0};
        for (int i = 0; i < searchResult.getSearch().size(); i++) {
            String imdbId = searchResult.getSearch().get(i).getImdbID();
            ApiCall.Factory.OMDB().getMovie(imdbId).enqueue(new Callback<OMDBMovie>() {
                @Override
                public void onResponse(Call<OMDBMovie> call, Response<OMDBMovie> response) {
                    movies.add(response.body());
                    gridFragment.recyclerAdapter.notifyItemInserted(movies.size() - 1);
                    count[0]++;
                    isDataFetchComplete(count[0]);
                }

                @Override
                public void onFailure(Call<OMDBMovie> call, Throwable t) {
                    Log.e(TAG, "Failure : " + t.getMessage());
                    count[0]++;
                    isDataFetchComplete(count[0]);
                }
            });
        }
    }

    public void isDataFetchComplete(int count) {
        if (searchResult.getResponse().equals("True") && count == searchResult.getSearch().size()) {
            progressDialog.dismiss();
            gridFragment.setLoaded();
        }
    }

    public void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(homePageFragment, "HOME");
        viewPagerAdapter.addFragment(gridFragment, "MOVIES");
        viewPager.setAdapter(viewPagerAdapter);
    }

}
