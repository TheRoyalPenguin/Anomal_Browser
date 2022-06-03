package com.example.anomalbrowser.tabLogic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.fragments.TabsFragment;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> {

    private static int viewHolderCount;
    private TabButtonClickListener tabButtonClickListener;
    private TabImgClickListener tabImgClickListener;
    ArrayList<Tab> arr;
    private final LayoutInflater inflater;
    private EditText etSearchTabs;
    private Spinner spinnerSearch, spinnerSort;

    public TabAdapter(Context context, ArrayList arr, TabButtonClickListener tabButtonClickListener, TabImgClickListener tabImgClickListener, EditText etSearchTabs, Spinner spinnerSearch, Spinner spinnerSort) {
        this.arr = arr;
        this.tabButtonClickListener = tabButtonClickListener;
        this.tabImgClickListener = tabImgClickListener;
        viewHolderCount = 0;
        this.inflater = LayoutInflater.from(context);
        this.etSearchTabs = etSearchTabs;
        this.spinnerSearch = spinnerSearch;
        this.spinnerSort = spinnerSort;
    }
    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tabs_row, parent, false);
        return new TabAdapter.TabViewHolder(view, tabButtonClickListener, tabImgClickListener);


    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
         holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return arr.size();
    }

    class TabViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        TextView tvUrl;
        ImageView imageView, imageMain;
        TabButtonClickListener tabButtonClickListener;
        TabImgClickListener tabImgClickListener;


        public TabViewHolder(View itemView, TabButtonClickListener tabButtonClickListener, TabImgClickListener tabImgClickListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            imageView = itemView.findViewById(R.id.imageView);
            imageMain = itemView.findViewById(R.id.imageMain);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabImgClickListener.onImgClick(getAdapterPosition());
                }
            });
            this.tabButtonClickListener = tabButtonClickListener;
            itemView.setOnClickListener(this);
        }

        void bind(int listText) {
            if (arr.size() > 0) {
                String spinValue = spinnerSearch.getSelectedItem().toString();
                switch (spinValue)
                {
                    case ("названию"):
                        if (containsIgnoreCase(arr.get(listText).name, etSearchTabs.getText().toString()))
                        {
                            tvName.setText(arr.get(listText).name);
                            String url = arr.get(listText).URL;
                            tvUrl.setText(url);
                            Picasso.get().load(arr.get(listText).url_image).resize(300, 300).centerCrop().into(imageMain);
                        }
                        else
                        {

                            itemView.setVisibility(View.GONE);
                            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                        }
                        break;
                    case ("ссылке"):
                        if (containsIgnoreCase(arr.get(listText).URL, etSearchTabs.getText().toString()))
                        {
                            tvName.setText(arr.get(listText).name);
                            String url = arr.get(listText).URL;
                            tvUrl.setText(url);
                            Picasso.get().load(arr.get(listText).url_image).resize(300, 300).centerCrop().into(imageMain);
                        }
                        else
                        {

                            itemView.setVisibility(View.GONE);
                            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                        }
                        break;
                    case ("всему"):
                        if ((containsIgnoreCase(arr.get(listText).name, etSearchTabs.getText().toString())) || (containsIgnoreCase(arr.get(listText).URL, etSearchTabs.getText().toString())))
                        {
                            tvName.setText(arr.get(listText).name);
                            String url = arr.get(listText).URL;
                            tvUrl.setText(url);
                            Picasso.get().load(arr.get(listText).url_image).resize(300, 300).centerCrop().into(imageMain);
                        }
                        else
                        {

                            itemView.setVisibility(View.GONE);
                            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                        }
                        break;
                }

            } else
            {
            }
        }

        @Override
        public void onClick(View view) {
            tabButtonClickListener.onButtonClick(getAdapterPosition());
        }
    }



    public interface TabButtonClickListener {
            void onButtonClick(int position);
    }

    public interface TabImgClickListener {
        void onImgClick(int position);
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

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
