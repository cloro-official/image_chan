package com.example.image_chan.core;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.image_chan.ImageViewActivity;
import com.example.image_chan.R;
import com.example.image_chan.ui.gallery.GalleryFragment;

import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.entities.Rating;
import net.kodehawa.lib.imageboards.entities.impl.SafebooruImage;

import java.util.ArrayList;

public class StaggeredAdapter extends RecyclerView.Adapter<StaggeredAdapter.ViewHolder> {
    private static final String TAG = "StaggeredAdapterAd";
    private final boolean[] existing;
    public ArrayList<String> tags;
    public ArrayList<String> ratingShort;
    public ArrayList<String> ratingLong;

    public ArrayList<String> urls;

    private final GalleryFragment Context;

    public StaggeredAdapter(ArrayList<String> urls, ArrayList<String> ratingLong, ArrayList<String> ratingShort, ArrayList<String> directory, ArrayList<String> tags, boolean[] existing, GalleryFragment context) {
        this.urls = urls;

        this.tags = tags;

        this.ratingShort = ratingShort;
        this.ratingLong = ratingLong;

        this.existing = existing;

        Context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bundle b1 = new Bundle();

        b1.putString("tag", this.tags.get(position));
        b1.putString("ratingLong", this.ratingLong.get(position));
        b1.putString("ratingShort", this.ratingShort.get(position));
        b1.putString("url", this.urls.get(position));

        boolean thatExists = false;
        if (existing != null)
            thatExists = existing[position];


        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.error)
                .placeholder(R.drawable.loader);

        Glide.with(Context)
                .load(urls.get(position))
                .apply(requestOptions)
                .into(holder.image);

        holder.ifFavorite(thatExists);
        holder.name.setText("RATING: " + ratingLong.get(position).toUpperCase());

        holder.image.setOnClickListener(view -> {
            Context viewContext = (Context) view.getContext();

            Intent toView = new Intent(viewContext, ImageViewActivity.class);
            toView.putExtras(b1);

            viewContext.startActivity(toView);
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        SafebooruImage innerclass;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.image_name);
        }

        public void ifFavorite(boolean update) {
            CardView card = itemView.findViewById(R.id.item_card);

            card.setBackgroundResource(update ? R.color.rich_black : R.color.raisin_black);
        }
    }
}
