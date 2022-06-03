package com.example.anomalbrowser.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.appsLogic.App;
import com.example.anomalbrowser.appsLogic.AppsAdapter;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.funny_birds.Funny_Birds;
import com.example.anomalbrowser.mediaLogic.Media;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.example.anomalbrowser.webAppLogic.webApp;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ProfileFragment extends Fragment implements AppsAdapter.OnAppListener{

    private DatabaseReference mDataBase;
    private String DATA_KEY = "USERS";
    private FirebaseUser user;
    private static String userEmail, userName;
    private StorageReference storageRef;
    private LinearLayoutManager linearLayoutManager;
    private NavController navController;

    private TextView tvUserName, tvUserEmail;
    private ImageView imageProfile;
    private ImageButton btnNewPhoto, ibGameBirds, btnGlobalSearchMedia;

    RecyclerView listApps;
    public static AppsAdapter appsAdapter;
    private ArrayList<App> apps;
    private BDControl bdControl;
    private ImageButton addNewAppBtn;

    public static void setUserName(String name) {
        userName = name;
    }

    private void init(View view)
    {
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserName.setText(userName);
        tvUserEmail = (TextView) view.findViewById(R.id.tvUserEmail);
        imageProfile = (ImageView) view.findViewById(R.id.imageProfile);
        imageProfile.setClipToOutline(true);
        btnNewPhoto = view.findViewById(R.id.btnNewPhoto);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail().toString();
        tvUserEmail.setText(userEmail);
        mDataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + userEmail.hashCode());
        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);

        listApps = view.findViewById(R.id.listApps);
        apps = new ArrayList<>();
        appsAdapter = new AppsAdapter(this.getContext(), apps, this);

        bdControl = new BDControl();
        apps = bdControl.getAllApps("apps", apps);
        addNewAppBtn = view.findViewById(R.id.addNewApp);

        ibGameBirds = view.findViewById(R.id.ibGameBirds);
        btnGlobalSearchMedia = view.findViewById(R.id.btnGlobalSearchMedia);

        getUserNameFromBd();
        getUserLogoPhoto();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);

        listApps.setAdapter(appsAdapter);


        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View promptsView = layoutInflater.inflate(R.layout.prompt, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
                mDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        String name = userInput.getText().toString();
                                        tvUserName.setText(name);
                                        mDataBase.child("name").setValue(name);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });


                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();

            }
        });
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage("none");
//                Toast.makeText(ProfileFragment.this.getContext().getApplicationContext(), "IMGPROF COMPLITE!", Toast.LENGTH_SHORT).show();
            }
        });

        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.mediaPhotosFragment);
            }
        });


        addNewAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View promptsView = layoutInflater.inflate(R.layout.new_app_dialog, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
                mDialogBuilder.setView(promptsView);
                final EditText userInputName = (EditText) promptsView.findViewById(R.id.input_nameNewApp);
                final EditText userInputURL = (EditText) promptsView.findViewById(R.id.input_urlNewApp);
                final Spinner sPopular_apps = promptsView.findViewById(R.id.sPopular_apps);
                sPopular_apps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String[] choose = getResources().getStringArray(R.array.popular_apps);
                        String select = choose[i];
                        switch (select)
                        {
                            case ("YouTube"):
                                userInputURL.setText("https://www.youtube.com/");
                                userInputName.setText(select);
                                break;
                            case ("VK"):
                                userInputURL.setText("https://vk.com/");
                                userInputName.setText(select);
                                break;
                            case ("Mail.ru"):
                                userInputURL.setText("https://mail.ru/");
                                userInputName.setText(select);
                                break;
                            case ("Telegram"):
                                userInputURL.setText("https://web.telegram.org/");
                                userInputName.setText(select);
                                break;
                            case ("Instagram"):
                                userInputURL.setText("https://instagram.com/");
                                userInputName.setText(select);
                                break;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {


                                        if (!userInputURL.getText().toString().equals("")) {
                                            if (!userInputName.getText().toString().equals(""))
                                            {
                                                apps.add(new App(userInputName.getText().toString(), userInputURL.getText().toString()));
                                                bdControl.pushNewApp("apps", new App(userInputName.getText().toString(), userInputURL.getText().toString()), getContext(), getActivity());
                                            }
                                            else {
                                                Toast.makeText(ProfileFragment.this.getContext(), "Введите корректное название!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(ProfileFragment.this.getContext(), "Введите корректный адрес!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });


                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();

            }
        });

        ibGameBirds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Funny_Birds.class);
                startActivity(intent);
            }
        });

        btnGlobalSearchMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.folderByIdFragment);
            }
        });
        return view;
    }

    private void getImage(String type)
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
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imageProfile.setImageURI(data.getData());
                        uploadImage("logoPhoto");
//                        Toast.makeText(ProfileFragment.this.getContext().getApplicationContext(), "main COMPLITE!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void uploadImage(String dir)
    {


        Bitmap bitmap = ((BitmapDrawable) imageProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);


        byte[] byteArray = baos.toByteArray();
        final StorageReference mRef = storageRef.child(System.currentTimeMillis() + "userProfileLogo");
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (dir.equals("media")) mDataBase.child(dir).push().setValue(task.getResult().toString());
                else mDataBase.child(dir).setValue(task.getResult().toString());
                Toast.makeText(ProfileFragment.this.getContext().getApplicationContext(), "Загрузка прошла успешно!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUserNameFromBd()
    {
        mDataBase.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String value = dataSnapshot.child("name").getValue(String.class); //This is a1
                userName = value;
                tvUserName.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void getUserLogoPhoto()
    {
        mDataBase.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String value = dataSnapshot.child("logoPhoto").getValue(String.class);
                Picasso.get().load(value).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    @Override
    public void onAppClick(int position, App app) {
        Intent intent = new Intent(this.getActivity(), webApp.class);
        intent.putExtra("url", app.getURL());
        startActivity(intent);
//        Toast.makeText(this.getContext(), "goooooooo to WEBAPP", Toast.LENGTH_SHORT).show();
    }
}