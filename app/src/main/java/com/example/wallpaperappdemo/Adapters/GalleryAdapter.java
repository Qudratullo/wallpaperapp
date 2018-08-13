package com.example.wallpaperappdemo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wallpaperappdemo.Classes.Image;
import com.example.wallpaperappdemo.Activities.PagerActivity;
import com.example.wallpaperappdemo.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>{
    private ArrayList<Image> images;
    private Context mContext;

    public GalleryAdapter(Context context, ArrayList<Image> images) {
        mContext = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
        final Image image = images.get(position);
        Glide.with(mContext).load(image.getPreviewURL())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PagerActivity.class);
                intent.putExtra("images", images);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public CardView cardView;

        private ImageViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardView);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }
}
