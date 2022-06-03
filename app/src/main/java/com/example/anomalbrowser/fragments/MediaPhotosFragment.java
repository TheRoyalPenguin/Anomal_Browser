package com.example.anomalbrowser.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.mediaLogic.Media;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.example.anomalbrowser.mediaLogic.MediaGlobalFolder;
import com.example.anomalbrowser.mediaLogic.PhotoDialogFragment;
import com.example.anomalbrowser.myGlobalFolders.Folder;
import com.example.anomalbrowser.myGlobalFolders.FolderAdapter;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MediaPhotosFragment extends Fragment implements MediaAdapter.OnPhotoListener {
    private Button btnNewPhoto, btnCameraNewPhoto, btnDeleteSelectedImage, btnShareGlobal, btnShareSelectedImage;
    public static RecyclerView recyclerViewProfile;

    private ArrayList<Media> medias;
    private MediaAdapter mediaAdapter;
    private StorageReference storageRef;
    private ImageView GONEIMG;
    private static String userEmail, userName;
    private DatabaseReference mDataBase;
    private String DATA_KEY = "USERS";
    private FirebaseUser user;
    private NavController navController;
    public static ArrayList<Media> selectedMedias = new ArrayList<>();
    private BDControl bdControl;
    private ArrayList<Folder> folders = new ArrayList<>();
    public static RecyclerView rvFoldersGlobal;
    private LinearLayout llMyFolders;
    private TextView tvMyFolders;

    private void init(View view)
    {
        btnNewPhoto = view.findViewById(R.id.btnNewPhoto);
        btnCameraNewPhoto = view.findViewById(R.id.btnCameraNewPhoto);
        recyclerViewProfile = view.findViewById(R.id.mediaProfile);
        medias = new ArrayList<>();
        btnDeleteSelectedImage = view.findViewById(R.id.btnDeleteSelectedImage);
        mediaAdapter = new MediaAdapter(this.getContext(), medias, this, btnDeleteSelectedImage);
        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        GONEIMG = new ImageView(view.getContext());

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail().toString();
        mDataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + userEmail.hashCode());
        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        navController = Navigation.findNavController(this.requireActivity(), R.id.navHostFragment);

        btnShareGlobal = view.findViewById(R.id.btnShareGlobal);
        btnShareSelectedImage = view.findViewById(R.id.btnShareSelectedImage);
        rvFoldersGlobal = view.findViewById(R.id.rvFolders);
        bdControl = new BDControl();

        llMyFolders = view.findViewById(R.id.llMyFolders);
        tvMyFolders = view.findViewById(R.id.tvMyFolders);


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_photos, container, false);
        init(view);
        selectedMedias.clear();
        bdControl.getAllMyGlobalFolders(getContext(), MediaPhotosFragment.this, tvMyFolders, llMyFolders);


        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage("file");
//                Toast.makeText(MediaPhotosFragment.this.getContext().getApplicationContext(), "BTNCLICK COMPLITE!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCameraNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage("camera");
            }
        });

        recyclerViewProfile.setAdapter(mediaAdapter);
        recyclerViewProfile.setItemViewCacheSize(30);



        btnShareGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMedias.size() > 0) {
                    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                    View promptsView = layoutInflater.inflate(R.layout.dialog_new_share_global, null);

                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
                    mDialogBuilder.setView(promptsView);
                    final EditText edName = (EditText) promptsView.findViewById(R.id.itName);
                    final EditText edPass = (EditText) promptsView.findViewById(R.id.itPass);

                    mDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            BDControl bdControl = new BDControl();
                                            String name = edName.getText().toString();
                                            String pass = edPass.getText().toString();
                                            MediaGlobalFolder folder = new MediaGlobalFolder(name, pass);
                                            bdControl.getLustIdGlobalFolders(folder, selectedMedias);
                                        }
                                    })
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });


                    AlertDialog alertDialog = mDialogBuilder.create();
                    alertDialog.show();

                }
                else Toast.makeText(MediaPhotosFragment.this.getContext(), "Выберите хотя бы один медиафайл", Toast.LENGTH_LONG).show();
            }
        });;

        btnDeleteSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMedias.size() > 0) {
                    for (Media media : selectedMedias) {
                        bdControl.removeFromDBbyObjectMedia(media.getUrl());
                    }
                    selectedMedias.clear();
                }
                else Toast.makeText(MediaPhotosFragment.this.getContext(), "Выберите хотя бы один медиафайл", Toast.LENGTH_LONG).show();
            }
        });

        btnShareSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMedias.size() > 0) {
                    ArrayList<String> urlShare = new ArrayList<>();
                    for (Media media : selectedMedias) {
                        urlShare.add(media.getUrl());
                    }
                    Bundle result = new Bundle();
                    result.putStringArrayList("urlShare", urlShare);
                    getParentFragmentManager().setFragmentResult("requestKey", result);
                    Navigation.findNavController(MediaPhotosFragment.this.requireActivity(), R.id.navHostFragment).navigate(R.id.homeFragment);
                    Toast.makeText(MediaPhotosFragment.this.getContext(), "готово", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(MediaPhotosFragment.this.getContext(), "Выберите хотя бы один медиафайл", Toast.LENGTH_LONG).show();
            }
        });




        setInitialDataFromMedias();
        return view;
    }

    private void getImage(String type)
    {
        if (type.equals("file"))
        {
            Intent intentChooser = new Intent();
            intentChooser.setType("image/*");
            intentChooser.setAction(Intent.ACTION_GET_CONTENT);
            someActivityResultLauncherMain.launch(intentChooser);
        }
        else
        {
            try{
                someActivityResultLauncherCamera.launch(openBackCamera());
            }catch (ActivityNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncherMain = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        GONEIMG.setImageURI(data.getData());
                        BrowserFragment.deleteCache(getContext());
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
                        uploadImage("media");
//                        Toast.makeText(MediaPhotosFragment.this.getContext().getApplicationContext(), "main COMPLITE!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        File imgFile = new  File(pictureImagePath);
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        GONEIMG.setImageBitmap(myBitmap);

                        uploadImage("media");
//                        Toast.makeText(MediaPhotosFragment.this.getContext().getApplicationContext(), "main COMPLITE!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private String pictureImagePath = "";
    private Intent openBackCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Uri imageUri = FileProvider.getUriForFile(
                MediaPhotosFragment.this.getContext(),
                "com.example.anomalbrowser.provider", //(use your app signature + ".provider" )
                file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return cameraIntent;
    }


    private void uploadImage(String dir)
    {

        Bitmap bitmap = ((BitmapDrawable) GONEIMG.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArray = baos.toByteArray();
        final StorageReference mRef = storageRef.child(System.currentTimeMillis() + "userMediaPhoto");
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                mDataBase.child(dir).push().setValue(task.getResult().toString());
                Toast.makeText(MediaPhotosFragment.this.getContext().getApplicationContext(), "Загрузка прошла успешно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInitialDataFromMedias(){

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (medias.size()>0) medias.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    medias.add(0, new Media("media", ds.getValue().toString()));
                }
                if (mediaAdapter != null) mediaAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.child("media").addValueEventListener(vListener);



    }

    @Override
    public void onPhotoClick(int position, Media media) {


        PhotoDialogFragment dialog;
        dialog = new PhotoDialogFragment();
        Bundle args = new Bundle();
        args.putString("URL", media.getUrl());
        dialog.setArguments(args);

        dialog.show(MediaPhotosFragment.this.getParentFragmentManager(), "custom");
    }

}