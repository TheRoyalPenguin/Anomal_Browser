package com.example.anomalbrowser.appsLogic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.mediaLogic.Media;
import com.example.anomalbrowser.mediaLogic.MediaAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder>{
    private static ArrayList<App> apps;
    private final LayoutInflater inflater;
    private ImageView appLogo;
    private AppsAdapter.OnAppListener onAppListener;
    private TextView appName;


    public AppsAdapter(Context context, ArrayList<App> apps, OnAppListener onAppListener) {
        this.apps = apps;
        this.inflater = LayoutInflater.from(context);
        this.appLogo = new ImageView(context);
        this.onAppListener = onAppListener;
    }


    @NonNull
    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.app_item, parent, false);
        appName = view.findViewById(R.id.appName);
        return new AppsAdapter.ViewHolder(view, onAppListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsAdapter.ViewHolder holder, int position) {
        App app = apps.get(position);
        Picasso.get().load("https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=" + app.getURL() + "&size=64").into(holder.appLogo);
        appName.setText(app.getName().toString());
    }


    @Override
    public int getItemCount() {
        return apps.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final ImageView appLogo;
        AppsAdapter.OnAppListener onAppListener;
        ViewHolder(View view, AppsAdapter.OnAppListener onAppListener)
        {
            super(view);
            this.onAppListener = onAppListener;
            appLogo = view.findViewById(R.id.appLogo);
            appLogo.setAdjustViewBounds(true);
            appLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            appLogo.setOnClickListener(this);
            appLogo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                    View promptsView = layoutInflater.inflate(R.layout.delete_folder_dialog, null);

                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
                    mDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);

                    mDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Удалить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            BDControl bdControl = new BDControl();
                                            bdControl.deleteApp(apps.get(getAdapterPosition()));
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

        @Override
        public void onClick(View view) {
            onAppListener.onAppClick(getAdapterPosition(), apps.get(getAdapterPosition()));
        }
    }


    public interface OnAppListener
    {
        void onAppClick(int position, App app);
    }
}
