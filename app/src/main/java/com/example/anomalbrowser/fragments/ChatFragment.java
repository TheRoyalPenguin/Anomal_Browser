package com.example.anomalbrowser.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.chatLogic.ChatAdapter;
import com.example.anomalbrowser.chatLogic.Mess;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.tabLogic.TabAdapter;
import com.example.anomalbrowser.usersLogic.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    private DatabaseReference chatDB;
    ImageView ivContactLogo;
    TextView tvContactName;
    ImageButton ibSendMess, ibSendFileMess;
    BDControl bdControl;
    String contactEmail;
    EditText edMess;
    public static RecyclerView rvChat;
    ArrayList<Mess> messes;
    public static ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private ImageView GONEIMG;

    private void init(View view) {
        ivContactLogo = view.findViewById(R.id.chatImageProfile);
        ivContactLogo.setClipToOutline(true);
        tvContactName = view.findViewById(R.id.tvChatUserName);
        ibSendMess = view.findViewById(R.id.ibSendMess);
        ibSendFileMess = view.findViewById(R.id.ibSendFileMess);
        edMess = view.findViewById(R.id.edMess);
        bdControl = new BDControl();
        rvChat = view.findViewById(R.id.rvChat);
        rvChat.setHasFixedSize(true);
        messes = new ArrayList<>();
        chatDB = FirebaseDatabase.getInstance().getReference("CHATS");
        chatAdapter = new ChatAdapter(getContext(), messes);
        layoutManager = new LinearLayoutManager(this.getContext());

        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        GONEIMG = new ImageView(getContext());
    }

    public void getAllMess() {

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (messes.size() > 0) messes.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    messes.add(new Mess(ds.child("text").getValue().toString(), ds.child("sender").getValue().toString()));

                    if (ChatFragment.this.getContext() != null)
                        chatAdapter = new ChatAdapter(ChatFragment.this.getContext(), messes);
                    rvChat.setAdapter(chatAdapter);
                }
                if (chatAdapter != null) chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        if (Integer.parseInt(contactEmail) < bdControl.edEmailToHash)
            chatDB.child(contactEmail + "|" + bdControl.edEmailToHash).addValueEventListener(vListener);
        else chatDB.child(bdControl.edEmailToHash + "|" + contactEmail).addValueEventListener(vListener);
        Log.d("TEST", contactEmail + bdControl.edEmailToHash);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        init(view);
        getParentFragmentManager().setFragmentResultListener("getUser", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                String contactName = bundle.getString("contactName");
                String contactLogoUrl = bundle.getString("contactLogoUrl");
                tvContactName.setText(contactName.toString());
                contactEmail = bundle.getString("contactEmail");
                Picasso.get().load(contactLogoUrl).into(ivContactLogo);

                getAllMess();

            }
        });


        ibSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check(edMess.getText().toString())) {
                    bdControl.putChatMess(contactEmail, edMess.getText().toString());
                    edMess.setText("");
                }
            }
        });
        ibSendFileMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageChat();
            }
        });

        return view;
    }


    public void getImageChat()
    {
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncherMain.launch(intentChooser);

    }
    ActivityResultLauncher<Intent> someActivityResultLauncherMain = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        GONEIMG.setImageURI(data.getData());;
                        bdControl.uploadImage(contactEmail, GONEIMG, "userChatImage");
//                        BrowserFragment.deleteCache(getContext());
//
//                        Bundle extras = data.getExtras();
//                        if (extras != null)
//                        {
//                            Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
//                            GONEIMG.setImageBitmap(thumbnailBitmap);
//                        }
//                        else
//                        {
//                            GONEIMG.setImageURI(data.getData());
//                        }
//                        Toast.makeText(MediaPhotosFragment.this.getContext().getApplicationContext(), "main COMPLITE!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    private boolean check(String str) {
        int q = 0;
        for (String i : str.split("")) {
            if (i.equals(" ")) q += 1;
        }
        return q != str.length();
    }
}