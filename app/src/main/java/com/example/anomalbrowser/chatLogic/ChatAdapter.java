package com.example.anomalbrowser.chatLogic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.fragments.ChatFragment;
import com.example.anomalbrowser.tabLogic.Tab;
import com.example.anomalbrowser.tabLogic.TabAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private final LayoutInflater inflater;

    ArrayList<Mess> messes;
    BDControl bdControl;
    public ChatAdapter(Context context, ArrayList<Mess> messes)
    {
        ChatFragment.rvChat.setItemViewCacheSize(messes.size());
        this.inflater = LayoutInflater.from(context);
        bdControl = new BDControl();
        this.messes = messes;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.mess_item, parent, false);
        return new ChatViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return messes.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvMess;
        ImageView ivMess;

        public ChatViewHolder(View view)
        {
            super(view);
            tvMess = view.findViewById(R.id.tvMess);
            ivMess = new ImageView(view.getContext());
//            ivMess = view.findViewById(R.id.ivMess);
            view.setOnClickListener(this);
        }
        void bind(int listText) {
            String textMess = messes.get(listText).text;
            String subTextMess = "";
            if (textMess.length() >= 70) subTextMess = textMess.substring(0, 70);
            ConstraintLayout constraintLayout = itemView.findViewById(R.id.layoutMess);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            if (bdControl.edEmailToHash == Integer.parseInt(messes.get(listText).sender))
            {
                if (subTextMess.equals("https://firebasestorage.googleapis.com/v0/b/anomal-browser.appspot.com"))
                {
                    tvMess.setVisibility(View.GONE);
                    ivMess.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_mess_user));
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.rightToRight = ConstraintSet.PARENT_ID;
                    ivMess.setLayoutParams(params);
                    constraintLayout.addView(ivMess);
//                    constraintSet.connect(R.id.ivMess, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                    Picasso.get().load(textMess).resize(1920, 1080).centerInside().into(ivMess);
                }
                else {
                    tvMess.setVisibility(View.VISIBLE);
                    tvMess.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_mess_user));
                    tvMess.setText(textMess);
                    constraintSet.connect(R.id.tvMess, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                }
            }
            else
            {
                if (subTextMess.equals("https://firebasestorage.googleapis.com/v0/b/anomal-browser.appspot.com"))
                {
                    tvMess.setVisibility(View.GONE);
                    ivMess.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_mess_contact));
//                    ivMess.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
//                            ConstraintLayout.LayoutParams.WRAP_CONTENT));
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftToLeft = ConstraintSet.PARENT_ID;
                    ivMess.setLayoutParams(params);
                    constraintLayout.addView(ivMess);
                    Picasso.get().load(textMess).resize(1920, 1080).centerInside().into(ivMess);
//                    constraintSet.connect(R.id.ivMess, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                }
                else {
                    tvMess.setVisibility(View.VISIBLE);
                    tvMess.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_mess_contact));
                    tvMess.setText(textMess);
                    constraintSet.connect(R.id.tvMess, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                }
            }
            constraintSet.applyTo(constraintLayout);
        }

        @Override
        public void onClick(View view) {

        }
    }

}

