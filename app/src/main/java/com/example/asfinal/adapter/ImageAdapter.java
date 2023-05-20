package com.example.asfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asfinal.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageList;
    private ImageListener imageListener;

    public void setImageListener(ImageAdapter.ImageListener imageListener) {
        this.imageListener = imageListener;
        notifyDataSetChanged();
    }

    public ImageAdapter(List<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_archive, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateData(List<String> newData) {
        imageList.clear();
        imageList.addAll(newData);
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_item_image_archive);
        }

        @Override
        public void onClick(View view) {
            if (imageListener != null) {
                imageListener.onClickImage(view, getAdapterPosition());
            }
        }
    }

    public interface ImageListener {
        void onClickImage(View view, int position);
    }
}
