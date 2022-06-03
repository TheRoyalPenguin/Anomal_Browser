
package com.example.anomalbrowser.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anomalbrowser.LoginActivity;
import com.example.anomalbrowser.MainActivity;
import com.example.anomalbrowser.R;
import com.example.anomalbrowser.appsLogic.App;
import com.example.anomalbrowser.appsLogic.AppsAdapter;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.mediaLogic.Media;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.example.anomalbrowser.mediaLogic.PhotoDialogFragment;
import com.example.anomalbrowser.usersLogic.User;
import com.example.anomalbrowser.usersLogic.UsersAdapter;
import com.example.anomalbrowser.webAppLogic.webApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment implements UsersAdapter.OnUserListener{
    BDControl bdControl;
    private  DatabaseReference dataBase, dataBaseChats;
    public static UsersAdapter usersAdapter, rvChatsUsersAdapter;
    private ArrayList<User> users, usersByChats;
    private ArrayList<String> chatsUser;
    RecyclerView listUsers, rvChatsUsers;
    private EditText etSearchUsers;
    private  FirebaseAuth mAuth;
    private  FirebaseUser user;
    private  String DATA_KEY = "USERS";
    public int edEmailToHash;
//    private TextView tvGlobalSearch;
    private AppCompatButton btnGlobalSearch, btnChats;
    private String selectCheck = "btnChats";
    private ArrayList<String> urlShareMess;
    private TextView tvHomeShare;

    private void init(View view)
    {
        bdControl = new BDControl();
        etSearchUsers = view.findViewById(R.id.etSearchUsers);
        listUsers = view.findViewById(R.id.rvGlobalUsers);
        rvChatsUsers = view.findViewById(R.id.rvChatsUsers);
        users = new ArrayList<>();
        usersByChats = new ArrayList<>();
        chatsUser = new ArrayList<>();
//        tvGlobalSearch = view.findViewById(R.id.tvGlobalSearch);
        usersAdapter = new UsersAdapter(this.getContext(), users, this, etSearchUsers, selectCheck);
        rvChatsUsersAdapter = new UsersAdapter(this.getContext(), usersByChats, this, etSearchUsers, selectCheck);
        dataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY);
        dataBaseChats = FirebaseDatabase.getInstance().getReference("CHATS");
        btnChats = view.findViewById(R.id.btnChats);
        btnGlobalSearch = view.findViewById(R.id.btnGlobalSearch);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        edEmailToHash = user.getEmail().hashCode();
        urlShareMess = new ArrayList<>();
        tvHomeShare = view.findViewById(R.id.tvHomeShare);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        getAllChats();


        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                tvHomeShare.setVisibility(View.VISIBLE);
                urlShareMess = bundle.getStringArrayList("urlShare");

            }
        });


        etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (selectCheck.equals("btnChats")) getAllChats();
                else if (selectCheck.equals("btnGlobalSearch")) getAllUsers();
//                usersAdapter.notifyDataSetChanged();
//                listUsers.setAdapter(usersAdapter);

            }
        });

        btnChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGlobalSearch.setBackgroundDrawable(null);
                btnChats.setBackgroundDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.btn_background_select_chat));
                listUsers.setVisibility(View.GONE);
                rvChatsUsers.setVisibility(View.VISIBLE);
                selectCheck = "btnChats";
                getAllChats();

            }
        });
        btnGlobalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnChats.setBackgroundDrawable(null);
                btnGlobalSearch.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_background_select_chat));
                listUsers.setVisibility(View.VISIBLE);
                rvChatsUsers.setVisibility(View.GONE);
                selectCheck = "btnGlobalSearch";
                getAllUsers();
            }
        });
        return view;
    }


    @Override
    public void onUserClick(int position, User user) {
        if (urlShareMess.size() > 0)
        {
            for (String s : urlShareMess)
            {
                bdControl.putChatMess(String.valueOf(user.getEmail().hashCode()), s);
            }
            urlShareMess.clear();
            tvHomeShare.setVisibility(View.GONE);
        }
        Bundle result = new Bundle();
        result.putString("contactName", user.getName());
        result.putString("contactLogoUrl", user.getUrlLogo());
        result.putString("contactEmail", Integer.toString(user.getEmail().hashCode()));
        getParentFragmentManager().setFragmentResult("getUser", result);
            Navigation.findNavController(HomeFragment.this.requireActivity(), R.id.navHostFragment).navigate(R.id.chatFragment);
//            Toast.makeText(HomeFragment.this.getContext(), "click " + user.getName(), Toast.LENGTH_LONG).show();
    }


    public void getAllUsers(){

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (users.size()>0) users.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    users.add(new User(Objects.requireNonNull(ds.child("name").getValue()).toString(), Objects.requireNonNull(ds.child("email").getValue()).toString(), Objects.requireNonNull(ds.child("logoPhoto").getValue()).toString()));
                    usersAdapter = new UsersAdapter(HomeFragment.this.getContext(), users, HomeFragment.this, etSearchUsers, selectCheck);
                    listUsers.setAdapter(usersAdapter);
                }
                if (usersAdapter != null) usersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBase.addListenerForSingleValueEvent(vListener);


    }

    public void getAllChatsUsers(){

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (usersByChats.size()>0) usersByChats.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (chatsUser.contains(ds.getKey()))
                    {
                        usersByChats.add(new User(Objects.requireNonNull(ds.child("name").getValue()).toString(), Objects.requireNonNull(ds.child("email").getValue()).toString(), Objects.requireNonNull(ds.child("logoPhoto").getValue()).toString()));
                        rvChatsUsersAdapter = new UsersAdapter(HomeFragment.this.getContext(), usersByChats, HomeFragment.this, etSearchUsers, selectCheck);
                        rvChatsUsers.setAdapter(rvChatsUsersAdapter);

                    }

                }
                if (rvChatsUsersAdapter != null) rvChatsUsersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBase.addValueEventListener(vListener);


    }

    public void getAllChats(){

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (chatsUser.size()>0) chatsUser.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    try {

                        String oneS = ds.getKey().split("\\|")[0];
                        String twoS = ds.getKey().split("\\|")[1];
                        if (oneS.equals(String.valueOf(edEmailToHash))) chatsUser.add(twoS);
                        else if (twoS.equals(String.valueOf(edEmailToHash))) chatsUser.add(oneS);
                    } catch (Exception e)
                    {
//                        ds.getRef().removeValue();
                    }
                }
                getAllChatsUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataBaseChats.addValueEventListener(vListener);


    }

}