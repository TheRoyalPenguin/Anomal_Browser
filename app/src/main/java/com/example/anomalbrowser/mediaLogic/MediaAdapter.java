package com.example.anomalbrowser.mediaLogic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.MainActivity;
import com.example.anomalbrowser.R;
import com.example.anomalbrowser.fragments.ChatFragment;
import com.example.anomalbrowser.fragments.MediaPhotosFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder>{
    private static ArrayList<Media> medias;
    private final LayoutInflater inflater;
    private ImageView img;
    private Button btnDeleteSelectedImage;
    private OnPhotoListener mOnPhotoListener;
    private static ArrayList<Media> selectedMedias;
    public MediaAdapter(Context context, ArrayList<Media> medias, OnPhotoListener onPhotoListener, Button btnDeleteSelectedImage) {
        this.medias = medias;
//        MediaPhotosFragment.recyclerViewProfile.setItemViewCacheSize(medias.size());
        this.inflater = LayoutInflater.from(context);
        this.mOnPhotoListener = onPhotoListener;
        this.img = new ImageView(context);
        selectedMedias = new ArrayList<>();
        this.btnDeleteSelectedImage = btnDeleteSelectedImage;
    }

    @NonNull
    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.media_item, parent, false);
        selectedMedias.clear();
        return new ViewHolder(view, mOnPhotoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.ViewHolder holder, int position) {
        Media media = medias.get(position);
        Picasso.get().load(media.getUrl()).resize(480, 480).centerCrop().into(holder.mediaAvatar);
//        RequestCreator requestCreator = Picasso.get().load(media.getUrl());
//        try {
//            Bitmap bitmap = requestCreator.get();
//            int w, h, bacW, bacH;
//            w = bitmap.getWidth();
//            h = bitmap.getHeight();
//            if (w > h)
//            {
//                bacW = w;
//                w = w / (w / 720);
//                h = h / (w / bacW);
//            }
//            else {
//                bacH = h;
//                h = h / (h / 720);
//                w = w / (h / bacH);
//            }
//            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() , 120, false);
//            holder.mediaAvatar.setImageBitmap(bitmapResized);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    @Override
    public int getItemCount() {
        return medias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final ImageView mediaAvatar;
        CheckBox cbSelectImage;
        OnPhotoListener onPhotoListener;
        ViewHolder(View view, OnPhotoListener onPhotoListener)
        {
            super(view);
            this.onPhotoListener = onPhotoListener;
            mediaAvatar = view.findViewById(R.id.mediaAvatar);
            mediaAvatar.setAdjustViewBounds(true);
            mediaAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mediaAvatar.setOnClickListener(this);
            cbSelectImage = view.findViewById(R.id.cbSelectImage);
            cbSelectImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        MediaPhotosFragment.selectedMedias.add(medias.get(getAdapterPosition()));
//                        Toast.makeText(view.getContext(), String.valueOf(MediaPhotosFragment.selectedMedias.size()), Toast.LENGTH_SHORT).show();
                    } else
                    {
                        MediaPhotosFragment.selectedMedias.remove(medias.get(getAdapterPosition()));
//                        Toast.makeText(view.getContext(), String.valueOf(MediaPhotosFragment.selectedMedias.size()), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            onPhotoListener.onPhotoClick(getAdapterPosition(), medias.get(getAdapterPosition()));
        }
    }

    public interface OnPhotoListener
    {
        void onPhotoClick(int position, Media media);
    }



}
