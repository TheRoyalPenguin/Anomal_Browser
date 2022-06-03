package com.example.anomalbrowser.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.dbSQL.DB;
import com.example.anomalbrowser.dbSQL.History;
import com.example.anomalbrowser.history.HistoryAdapter;
import com.example.anomalbrowser.tabLogic.TabAdapter;

import java.util.ArrayList;

public class HistoryFragment extends Fragment  implements HistoryAdapter.TabButtonClickListener, HistoryAdapter.TabImgClickListener {

    private ArrayList<History> historyArrayList;
    private RecyclerView recyclerView;
    private DB db;
    private HistoryAdapter historyAdapter;
    private EditText etSearchTabs;
    private Spinner spinnerSearch, spinnerSort;
    private NavController navController;
    private void init(View view)
    {
        historyArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.listHistory);
        db = new DB(getContext());
        etSearchTabs = view.findViewById(R.id.etSearchTabs);
        spinnerSearch = view.findViewById(R.id.spinnerSearch);
        navController = Navigation.findNavController(this.requireActivity(), R.id.navHostFragment);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        init(view);


        String[] spinersSearch = {"названию", "ссылке", "всему"};
        ArrayAdapter<String> spinSearchAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinersSearch);
        spinSearchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearch.setAdapter(spinSearchAdapter);

        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = (String) parent.getItemAtPosition(pos);
                if (historyAdapter != null) {
                    historyAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(historyAdapter);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        historyArrayList = db.getAllData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        historyAdapter = new HistoryAdapter(getContext(), historyArrayList, HistoryFragment.this, HistoryFragment.this, etSearchTabs, spinnerSearch, spinnerSort);
        recyclerView.setAdapter(historyAdapter);




        return view;
    }

    @Override
    public void onButtonClick(int position) {
        RecyclerView.ViewHolder cycl = recyclerView.findViewHolderForLayoutPosition(position);
        TextView textView = cycl.itemView.findViewById(R.id.tvUrl);
        BrowserFragment.loadPage = textView.getText().toString();

        navController.navigate(R.id.browserFragment);
    }

    @Override
    public void onImgClick(int position, History history) {
        db.delete(history, position);
        historyArrayList = db.getAllData();
        if (historyArrayList != null) {
            historyAdapter = new HistoryAdapter(getContext(), historyArrayList, HistoryFragment.this, HistoryFragment.this, etSearchTabs, spinnerSearch, spinnerSort);
            historyAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(historyAdapter);
        }
    }
}