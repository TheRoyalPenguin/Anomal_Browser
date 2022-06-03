package com.example.anomalbrowser.fragments;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.tabLogic.Tab;
import com.example.anomalbrowser.tabLogic.TabAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TabsFragment extends Fragment implements TabAdapter.TabButtonClickListener, TabAdapter.TabImgClickListener {
//    private TabAdapter adapter;
//    private LinearLayoutManager linearLayoutManager;

    private ArrayList<Tab> tabs;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private LinearLayoutManager linearLayoutManager;
    private TabAdapter adapter;
    private NavController navController;
    private  TextView textView;
    private EditText etSearchTabs;
    private DatabaseReference mDataBase;
    private String DATA_KEY = "USERS";
    private Spinner spinnerSearch, spinnerSort;
    private ImageButton btnHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
//        listView = view.findViewById(R.id.listView);
//        listData = new ArrayList<>();
//        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, listData);
//        listView.setAdapter(adapter);
        init(view);

        recyclerView = view.findViewById(R.id.listTabs);

        getDataFromDB();
//        ArrayList<Tab> animalNames = new ArrayList<>();
//        animalNames.add(new Tab("Title1", "URL1"));
//        animalNames.add(new Tab("Title2", "URL2"));
//        animalNames.add(new Tab("Title3", "URL3"));
//        animalNames.add(new Tab("Title4", "URL4"));
//        animalNames.add(new Tab("Title5", "URL5"));
//
//        RecyclerView recyclerView = view.findViewById(R.id.listTabs);
//        linearLayoutManager = new LinearLayoutManager(this.getContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
//
//        adapter = new TabAdapter(animalNames.size(), animalNames);
//        recyclerView.setAdapter(adapter);


//        textView = view.findViewById(R.id.textView2);

        etSearchTabs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        });

//        String[] spinersSort = {"названию", "ссылке"};
//        ArrayAdapter<String> spinSortAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinersSort);
//        spinSortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSort.setAdapter(spinSortAdapter);
//
//        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                String item = (String) parent.getItemAtPosition(pos);
//                if (adapter!= null) {
//                    adapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });


        String[] spinersSearch = {"названию", "ссылке", "всему"};
        ArrayAdapter<String> spinSearchAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinersSearch);
        spinSearchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearch.setAdapter(spinSearchAdapter);

        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = (String) parent.getItemAtPosition(pos);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            navController.navigate(R.id.historyFragment);
            }
        });
        return view;

    }


    private void init(View view)
    {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        int edEmailToHash = user.getEmail().hashCode();
        mDataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + edEmailToHash + "/" + "tabs");
        tabs = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this.getContext());
        navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
        etSearchTabs = view.findViewById(R.id.etSearchTabs);
        spinnerSearch = view.findViewById(R.id.spinnerSearch);
        btnHistory = view.findViewById(R.id.btnHistory);
//        spinnerSort = view.findViewById(R.id.spinnerSort);
    }

    private void getDataFromDB()
    {
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (tabs.size()>0) tabs.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Tab tab = new Tab(ds.child("name").getValue().toString(), ds.child("URL").getValue().toString(), ds.child("url_image").getValue().toString());
//                    String name = ds.child("0").getValue().toString();
//                    String URL = ds.child("1").getValue().toString();

                    tabs.add(0, tab);

                    recyclerView.setLayoutManager(linearLayoutManager);

                    if (getContext() != null) {
                        adapter = new TabAdapter(getContext(), tabs, TabsFragment.this, TabsFragment.this, etSearchTabs, spinnerSearch, spinnerSort);
                        recyclerView.setAdapter(adapter);
                    }
                }
                if (adapter != null) adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }

    @Override
    public void onButtonClick(int position) {
        RecyclerView.ViewHolder cycl = recyclerView.findViewHolderForLayoutPosition(position);
        TextView textView = cycl.itemView.findViewById(R.id.tvUrl);
        BrowserFragment.loadPage = textView.getText().toString();

        navController.navigate(R.id.browserFragment);
    }




    @Override
    public void onImgClick(int position) {
        RecyclerView.ViewHolder cycl = recyclerView.findViewHolderForLayoutPosition(position);
        BDControl bdControl = new BDControl();
        TextView tvUrl = cycl.itemView.findViewById(R.id.tvUrl);
        TextView tvName = cycl.itemView.findViewById(R.id.tvName);
        bdControl.removeFromDBbyObject(tvName.getText().toString(), tvUrl.getText().toString());
    }

}