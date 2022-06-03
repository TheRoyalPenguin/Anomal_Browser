package com.example.anomalbrowser.firebaseController;

import static android.app.Activity.RESULT_OK;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.appsLogic.App;
import com.example.anomalbrowser.chatLogic.Mess;
import com.example.anomalbrowser.fragments.BrowserFragment;
import com.example.anomalbrowser.fragments.ChatFragment;
import com.example.anomalbrowser.fragments.FolderByIdFragment;
import com.example.anomalbrowser.fragments.HomeFragment;
import com.example.anomalbrowser.fragments.MediaPhotosFragment;
import com.example.anomalbrowser.fragments.ProfileFragment;
import com.example.anomalbrowser.mediaLogic.Media;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.example.anomalbrowser.mediaLogic.MediaGlobalFolder;
import com.example.anomalbrowser.myGlobalFolders.Folder;
import com.example.anomalbrowser.myGlobalFolders.FolderAdapter;
import com.example.anomalbrowser.tabLogic.Tab;
import com.example.anomalbrowser.tabLogic.UrlLogo;
import com.example.anomalbrowser.usersLogic.User;
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
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class BDControl extends AppCompatActivity {
    private DatabaseReference mDataBaseTabs;
    private DatabaseReference mDataBase, dataBase, chatDB, globalFoldersDB;
    private String DATA_KEY = "USERS";
    public int edEmailToHash;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ShortcutManager shortcutManager;
    private StorageReference storageRef;

//    private ImageView GONEIMG;

    private String name, url;

    public BDControl() {
        init();
    }
//    public BDControl(View view) {
//        init();
//        this.GONEIMG = new ImageView(view.getContext());;
//    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        edEmailToHash = user.getEmail().hashCode();

        mDataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + edEmailToHash);
        dataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY);
        chatDB = FirebaseDatabase.getInstance().getReference("CHATS");
        globalFoldersDB = FirebaseDatabase.getInstance().getReference("globalFolders");
        mDataBaseTabs = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + edEmailToHash + "/" + "tabs");

        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
    }

    public void removeFromDBbyObject(String name, String url) {
        init();
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("name").getValue().toString().equals(name) && ds.child("URL").getValue().toString().equals(url)) {
                        ds.getRef().removeValue();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBaseTabs.addValueEventListener(vListener);
    }


    public void removeFromDBbyObjectMedia(String urlImage) {
        init();
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getValue().toString().equals(urlImage)) {
                        ds.getRef().removeValue();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.child("media").addValueEventListener(vListener);
    }


    private Context appContext;
    private App app;
    private Activity appActivity;

    public void pushNewApp(String dir, App app, Context context, Activity activity) {
        mDataBase.child(dir).push().setValue(app);
        appContext = context;
        appActivity = activity;
        this.app = app;
        Picasso.get().load("https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=" + app.getURL() + "&size=64").into(target);
    }

    public void deleteApp(App app)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("name").getValue().toString().equals(app.getName()) && ds.child("url").getValue().toString().equals(app.getURL())) {
                        ds.getRef().removeValue();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.child("apps").addListenerForSingleValueEvent(vListener);
    }

    Target target = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                shortcutManager = appContext.getSystemService(ShortcutManager.class);
                ShortcutInfo shortcut;
                ImageView imageView = (ImageView) new ImageView(appContext);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(app.getURL()), appActivity, webApp.class);
                intent.putExtra("url", app.getURL());
                Uri uri = Uri.parse(app.getURL());
                shortcut = new ShortcutInfo.Builder(appContext, app.getURL())
                        .setShortLabel(app.getName())
                        .setLongLabel(app.getName())
                        .setIcon(Icon.createWithBitmap(bitmap))
                        .setIntent(intent)
                        .build();
                shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));


            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public String getUserNameFromBd() {

        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class); //This is a1
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        return name;
    }

    int lustId = 0;

    public void getLustIdGlobalFolders(MediaGlobalFolder folder, ArrayList<Media> selectedMedias) {
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    lustId = Integer.parseInt(ds.getKey());
                }
                mDataBase.child("globalFolders").child(String.valueOf(lustId + 1)).setValue(folder);
                mDataBase.child("globalFolders").child(String.valueOf(lustId + 1)).child("files").setValue(selectedMedias);

                createNewGlobalFolder(folder, lustId + 1, selectedMedias);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        globalFoldersDB.addListenerForSingleValueEvent(vListener);
    }

    public ArrayList<App> getAllApps(String dir, ArrayList<App> arrayList) {

        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (arrayList.size() > 0) arrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    arrayList.add(new App(ds.child("name").getValue().toString(), ds.child("url").getValue().toString()));
                }
                if (ProfileFragment.appsAdapter != null)
                    ProfileFragment.appsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.child(dir).addValueEventListener(vListener);
        return arrayList;


    }

//    public ArrayList<User> getAllUsers(ArrayList<User> arrayList){
//
//        ValueEventListener vListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                if (arrayList.size()>0) arrayList.clear();
//
//                for (DataSnapshot ds : snapshot.getChildren())
//                {
//                    arrayList.add(new User(Objects.requireNonNull(ds.child("name").getValue()).toString(), Objects.requireNonNull(ds.child("email").getValue()).toString(), Objects.requireNonNull(ds.child("logoPhoto").getValue()).toString()));
//                }
//                if (HomeFragment.usersAdapter != null) HomeFragment.usersAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        dataBase.addValueEventListener(vListener);
//        return arrayList;
//
//
//    }
//    public void saveLogoUrl(String url, Bitmap bitmap)
//    {
//        ValueEventListener vListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//
//                for (DataSnapshot ds : snapshot.getChildren())
//                {
//                    if (ds.child("URL").getValue().toString().equals(url))
//                    {
//                        Log.d("GO WEB", ds.getKey().toString());
//                    }
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        mDataBaseTabs.addValueEventListener(vListener);
//    }


    public List<Tab> saveTabLogoUrl(String name, String url, Bitmap bitmap, List<Tab> arrTabs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArray = baos.toByteArray();
        final StorageReference mRef = storageRef.child(System.currentTimeMillis() + "urlTabLogos");
        UploadTask up = mRef.putBytes(byteArray);

        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                String res = task.getResult().toString();
                createTabs(name, url, res, arrTabs);
//                push("/logos", url, urlLogo);
            }
        });
        return arrTabs;
    }
//
//    public void push(String dir, String key, String value)
//    {
//        int edValueToHash = value.hashCode();
//        mDataBase.child(dir).child(Integer.toString(edValueToHash)).push().setValue(new UrlLogo(key, value));
//    }

    public List<Tab> clearArrTabs(Tab tab, List<Tab> arrTabs) {
        for (Tab tab1 : arrTabs) {
            if (tab1.URL.equals(tab.URL)) {
                arrTabs.remove(tab1);
                break;
            }
        }
        return arrTabs;
    }

    public List<Tab> createTabs(String name, String url, String url_image, List<Tab> arrTabs) {

//        ArrayList<String> list;
//        list = new ArrayList<>();
//        list.add(name);
//        list.add(url);
        Tab tab = new Tab(name, url, url_image);
        if (arrTabs.size() > 0 && !BrowserFragment.tabDel.URL.equals("test")) {
            String u1 = url.split("//", 2)[1].split("/")[0];
/*
            String u2 = arrTabs.get(arrTabs.size() - 1).URL.split("//", 2)[1].split("/")[0];
            Toast.makeText(BrowserFragment.context, u1, Toast.LENGTH_SHORT).show();
            Toast.makeText(BrowserFragment.context, u2, Toast.LENGTH_SHORT).show();
*/
            if (u1.equals(BrowserFragment.tabDel.URL.split("//", 2)[1].split("/")[0])) {
                Tab tabDel = new Tab(BrowserFragment.tabDel.name, BrowserFragment.tabDel.URL, BrowserFragment.tabDel.url_image);

                arrTabs = clearArrTabs(tabDel, arrTabs);
            }
        }
        BrowserFragment.tabDel = tab;
        arrTabs.add(tab);

        mDataBaseTabs.setValue(arrTabs);
        return arrTabs;
    }

    public void putChatMess(String hcUserEmail, String text) {


        String dir = "";
        if (edEmailToHash < Integer.parseInt(hcUserEmail))
            dir = String.valueOf(edEmailToHash) + "|" + hcUserEmail;
        else dir = hcUserEmail + "|" + String.valueOf(edEmailToHash);
        Mess mess = new Mess(text, String.valueOf(edEmailToHash));
        chatDB.child(dir).push().setValue(mess);
    }

    public String contactEmail;
//    public void getImageChat(String contactEmail, Context context)
//    {
//        Intent intentChooser = new Intent();
//        intentChooser.setType("image/*");
//        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
//        someActivityResultLauncherMain.launch(intentChooser);
//        this.contactEmail = contactEmail;
//
//    }
//    ActivityResultLauncher<Intent> someActivityResultLauncherMain = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == RESULT_OK) {
//                        Intent data = result.getData();
//                        GONEIMG.setImageURI(data.getData());
//                        uploadImage("chatPhotoSend");
////                        BrowserFragment.deleteCache(getContext());
////
////                        Bundle extras = data.getExtras();
////                        if (extras != null)
////                        {
////                            Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
////                            GONEIMG.setImageBitmap(thumbnailBitmap);
////                        }
////                        else
////                        {
////                            GONEIMG.setImageURI(data.getData());
////                        }
////                        Toast.makeText(MediaPhotosFragment.this.getContext().getApplicationContext(), "main COMPLITE!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });


    public void uploadImage(String contactEmail, ImageView GONEIMG, String dir) {


        Bitmap bitmap = ((BitmapDrawable) GONEIMG.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);


        byte[] byteArray = baos.toByteArray();
        final StorageReference mRef = storageRef.child(System.currentTimeMillis() + dir);
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                putChatMess(contactEmail, task.getResult().toString());
            }
        });
    }

    public void createNewGlobalFolder(MediaGlobalFolder folder, int lustId, ArrayList<Media> selectedMedias) {
        globalFoldersDB.child(String.valueOf(lustId)).setValue(folder);
        globalFoldersDB.child(String.valueOf(lustId)).child("files").setValue(selectedMedias);
    }

    public void checkPassFolder(String id, String pass, FolderByIdFragment folderByIdFragment) {
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean check = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(id)) {
                        if (ds.child("pass").getValue().equals(pass)) {
                            getAllFilesFolder(id, folderByIdFragment);
                            Toast.makeText(FolderByIdFragment.context, "Успешно!", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(FolderByIdFragment.context, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                        check = true;
                        break;
                    }
                }
                if (!check) Toast.makeText(FolderByIdFragment.context, "Папка с таким id не найдена!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        globalFoldersDB.addListenerForSingleValueEvent(vListener);
    }

    public void getAllFilesFolder(String id, FolderByIdFragment folderByIdFragment)
    {
        ArrayList<Media> arrayList = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    arrayList.add(0, new Media("media", ds.child("url").getValue().toString()));
                }
                if (arrayList.size() > 0 ) {
                    MediaAdapter mediaAdapter = new MediaAdapter(FolderByIdFragment.context, arrayList, folderByIdFragment, null);
                    if (mediaAdapter != null) mediaAdapter.notifyDataSetChanged();
                    FolderByIdFragment.rvMediasById.setAdapter(mediaAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        globalFoldersDB.child(id).child("files").addListenerForSingleValueEvent(vListener);
    }

    public void getAllMyGlobalFolders(Context context, MediaPhotosFragment mediaPhotosFragment, TextView tvMyFolders, LinearLayout llMyFolders)
    {
        ArrayList<Folder> arrayList = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    arrayList.add(new Folder(ds.child("name").getValue().toString(), ds.child("pass").getValue().toString(), ds.getKey().toString()));
                }
                if (arrayList.size() > 0 ) {
                    llMyFolders.setVisibility(View.VISIBLE);
                    tvMyFolders.setVisibility(View.VISIBLE);
                    FolderAdapter adapter = new FolderAdapter(context, arrayList, mediaPhotosFragment);
                    if (adapter != null) adapter.notifyDataSetChanged();
                    MediaPhotosFragment.rvFoldersGlobal.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.child("globalFolders").addListenerForSingleValueEvent(vListener);
    }

    public void deleteFolderInMy(Folder folder, Context context)
    {
        ArrayList<Folder> arrayList = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Folder folder1 = new Folder(ds.child("name").getValue().toString(), ds.child("pass").getValue().toString(), ds.getKey().toString());
                    if (folder1.getPass().equals(folder.getPass()) && folder1.getName().equals(folder.getName()) && folder1.getId().equals(folder1.getId()))
                    {
                        ds.getRef().removeValue();
                    }
                }
                deleteFolderInGlobal(folder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.child("globalFolders").addListenerForSingleValueEvent(vListener);
    }

    public void deleteFolderInGlobal(Folder folder)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Folder folder1 = new Folder(ds.child("name").getValue().toString(), ds.child("pass").getValue().toString(), ds.getKey().toString());
                    if (folder1.getPass().equals(folder.getPass()) && folder1.getName().equals(folder.getName()) && folder1.getId().equals(folder1.getId()))
                    {
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        globalFoldersDB.addListenerForSingleValueEvent(vListener);
    }

}

