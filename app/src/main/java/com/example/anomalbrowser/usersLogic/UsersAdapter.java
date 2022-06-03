package com.example.anomalbrowser.usersLogic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.R;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{
    private static ArrayList<User> users;
    private final LayoutInflater inflater;
    private ImageView userLogo;
    private OnUserListener onUserListener;
    private TextView tvUserName, tvUserEmail, tvUserTime;
    private EditText etSearchUsers;
    private String selectCheck;
    public UsersAdapter(Context context, ArrayList<User> users, OnUserListener onUserListener, EditText etSearchUsers, String selectCheck) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
        this.userLogo = new ImageView(context);
        this.onUserListener = onUserListener;
        this.etSearchUsers = etSearchUsers;
        this.selectCheck = selectCheck;
    }


    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_search_item, parent, false);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserTime = view.findViewById(R.id.tvUserTime);
        userLogo = view.findViewById(R.id.userLogo);
        return new UsersAdapter.ViewHolder(view, onUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(position, user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        OnUserListener onUserListener;
        ViewHolder(View view, OnUserListener onUserListener)
        {
            super(view);
            this.onUserListener = onUserListener;
            view.setOnClickListener(this);
        }
        void bind(int listText, User user) {
            String edText = etSearchUsers.getText().toString();
            String userName = user.getName();
            String userEmail = user.getEmail();
            if (containsIgnoreCase(userName, edText) || containsIgnoreCase(userEmail, edText))
            {
                tvUserName.setText(user.getName().toString());
                tvUserEmail.setText(user.getEmail().toString());
                userLogo.setClipToOutline(true);
                Picasso.get().load(user.getUrlLogo()).into(userLogo);
            }
            else if (selectCheck.equals("btnChats") && (edText.equals("")))
            {
                tvUserName.setText(user.getName().toString());
                tvUserEmail.setText(user.getEmail().toString());
                userLogo.setClipToOutline(true);
                Picasso.get().load(user.getUrlLogo()).into(userLogo);
            }
            else
            {
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }

        @Override
        public void onClick(View view) {
            onUserListener.onUserClick(getAdapterPosition(), users.get(getAdapterPosition()));
        }
    }

    public interface OnUserListener
    {
        void onUserClick(int position, User user);
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null || searchStr.equals("")) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }
}
