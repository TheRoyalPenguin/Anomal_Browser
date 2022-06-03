package com.example.anomalbrowser.mediaLogic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.fragments.MediaPhotosFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;


public class PhotoDialogFragment extends DialogFragment {

    ImageView dialogImageQR;
    TextView dialogTextView;
    Button btnViewImage;
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        String url = getArguments().getString("URL");

        LayoutInflater factory = LayoutInflater.from(PhotoDialogFragment.this.getContext());
        final View view = factory.inflate(R.layout.fragment_photo_dialog, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        dialogImageQR = view.findViewById(R.id.dialogImageQR);
        dialogTextView = view.findViewById(R.id.dialogTextView);
        btnViewImage = view.findViewById(R.id.btnViewImage);
        dialogTextView.setText(url);

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 1000, 1000);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            dialogImageQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }


        btnViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View promptsView = layoutInflater.inflate(R.layout.media_full_screen_dialog, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(view.getContext());
                mDialogBuilder.setView(promptsView);
                final ImageView ivImage = (ImageView) promptsView.findViewById(R.id.ivImage);
                Picasso.get().load(url).into(ivImage);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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
        });


        return builder
                .setView(view)
                .setTitle("Сканируй QR код на любом устройстве!")
                .setIcon(R.drawable.browserlogo)
                .setPositiveButton("ГОТОВО", null)
                .create();



    }
}