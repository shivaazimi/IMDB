package com.example.imdb.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imdb.Activity.DetailActivity;
import com.example.imdb.Model.Celebrity;
import com.example.imdb.Model.Genre;
import com.example.imdb.Model.Model;
import com.example.imdb.Model.OMDBMovie;
import com.example.imdb.Model.ParentModel;
import com.example.imdb.Model.TMDBMovie;
import com.example.imdb.Model.TVShow;
import com.example.imdb.R;
import com.example.imdb.Utility.GridSpacingItemDecoration;
import com.example.imdb.Utility.ItemTouchListener;

import java.util.ArrayList;
import java.util.List;

public class VerticalRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    public ArrayList<ParentModel> models;


    public VerticalRecyclerAdapter(Context context, ArrayList<ParentModel> models) {
        this.context = context;
        this.models = models;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_recycler, parent, false);
        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ParentModel model = models.get(position);
        VerticalViewHolder viewHolder = (VerticalViewHolder) holder;
        viewHolder.topic.setText(model.getTitle());
        final List<Model> cards = model.getModels();

        //RecycleView
        RecyclerView recyclerView = viewHolder.recyclerView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        //RecycleViewAdapter
        final HorizontalRecyclerAdapter recyclerAdapter = new HorizontalRecyclerAdapter(context, (ArrayList<Model>) cards);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerAdapter);

        //item Animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //item Decoration
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(7), false));
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.addOnItemTouchListener(new ItemTouchListener(recyclerView) {

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Model card = recyclerAdapter.list.get(position);
                Intent intent = new Intent(context, DetailActivity.class);
                if (card.TYPE != Model.MOVIE_TYPE)
                {
                    intent.putExtra("TYPE", card.getTYPE());
                    if (card instanceof TMDBMovie)
                        intent.putExtra("ID", ((TMDBMovie) card).getId());
                    else if (card instanceof Celebrity)
                        intent.putExtra("NAME", ((Celebrity) card).getName());
                    else if (card instanceof TVShow)
                        intent.putExtra("ID", ((TVShow) card).getId());
                    else if (card instanceof Genre)
                        intent.putExtra("ID", ((Genre) card).getId());
                    else if (card instanceof OMDBMovie)
                        intent.putExtra("ID", ((OMDBMovie) card).getImdbID());

                }
                context.startActivity(intent);
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    public int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()));
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView topic;

        public VerticalViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.horizontal_recycler);
            topic = (TextView) itemView.findViewById(R.id.topic);
        }
    }
}


