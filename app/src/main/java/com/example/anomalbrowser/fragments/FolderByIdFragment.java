package com.example.anomalbrowser.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.mediaLogic.Media;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.example.anomalbrowser.mediaLogic.PhotoDialogFragment;

public class FolderByIdFragment extends Fragment implements MediaAdapter.OnPhotoListener {

    ImageButton btnSearchFolderById;
    EditText etIdFolder, etPassFolder;
    public static Context context;
    private BDControl bdControl;
    public static RecyclerView rvMediasById;

private void init(View view)
{
    btnSearchFolderById = view.findViewById(R.id.btnSearchFolderById);
    etIdFolder = view.findViewById(R.id.etIdFolder);
    etPassFolder = view.findViewById(R.id.etPassFolder);
    bdControl = new BDControl();
    context = getContext();
    rvMediasById = view.findViewById(R.id.rvMediasById);

}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folder_by_id, container, false);
        init(view);

        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                etIdFolder.setText(bundle.getString("id"));
                etPassFolder.setText(bundle.getString("pass"));
                String text = etIdFolder.getText().toString();
                String pass = etPassFolder.getText().toString();
                bdControl.checkPassFolder(text, pass, FolderByIdFragment.this);
            }
        });


        btnSearchFolderById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etIdFolder.getText().toString();
                String pass = etPassFolder.getText().toString();
                bdControl.checkPassFolder(text, pass, FolderByIdFragment.this);

            }
        });


        return view;
    }

    @Override
    public void onPhotoClick(int position, Media media) {
        PhotoDialogFragment dialog;
        dialog = new PhotoDialogFragment();
        Bundle args = new Bundle();
        args.putString("URL", media.getUrl());
        dialog.setArguments(args);

        dialog.show(FolderByIdFragment.this.getParentFragmentManager(), "custom");
    }
}