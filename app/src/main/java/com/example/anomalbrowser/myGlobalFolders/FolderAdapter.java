package com.example.anomalbrowser.myGlobalFolders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.fragments.MediaPhotosFragment;
import com.example.anomalbrowser.fragments.ProfileFragment;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<Folder> folders;
    public static MediaPhotosFragment mediaPhotosFragment;
    public String pass;

        public FolderAdapter(Context context, List<Folder> folders, MediaPhotosFragment mediaPhotosFragment) {
        this.inflater = LayoutInflater.from(context);
        this.folders = folders;
        this.mediaPhotosFragment = mediaPhotosFragment;
    }

    @NonNull
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.folder_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.ViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.btnFolderId.setText(folder.getId());
        holder.tvFolderName.setText(folder.getName());
        holder.pass = folder.getPass();


    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final Button btnFolderId;
        final TextView tvFolderName;
        String pass = "";
        ViewHolder(View view){
            super(view);
            btnFolderId = view.findViewById(R.id.btnFolderId);
            tvFolderName = view.findViewById(R.id.tvFolderName);

            btnFolderId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle result = new Bundle();
                    result.putString("id", btnFolderId.getText().toString());
                    result.putString("name", tvFolderName.getText().toString());
                    result.putString("pass", pass);
                   mediaPhotosFragment.getParentFragmentManager().setFragmentResult("requestKey", result);
                    Navigation.findNavController(mediaPhotosFragment.getActivity(), R.id.navHostFragment).navigate(R.id.folderByIdFragment);
                }
            });

            btnFolderId.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                    View promptsView = layoutInflater.inflate(R.layout.delete_folder_dialog, null);

                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
                    mDialogBuilder.setView(promptsView);

                    mDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Удалить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            BDControl bdControl = new BDControl();
                                            bdControl.deleteFolderInMy(new Folder(tvFolderName.getText().toString(), pass, btnFolderId.getText().toString()), mediaPhotosFragment.getContext());
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
                    return false;
                }
            });
        }
    }
}
