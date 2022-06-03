package com.example.anomalbrowser.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.anomalbrowser.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class QRReadFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q_r_read, container, false);


        ImageView dialogImageQR = view.findViewById(R.id.dialogImageQR);
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode("huy", BarcodeFormat.QR_CODE, 350, 350);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    dialogImageQR.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
        // Найдите элемент TextView внутри вашей разметки
        // и установите ему соответствующий текст
//                TextView text = (TextView) dialog.findViewById(R.id.dialogTextView);
//                text.setText("Текст в диалоговом окне. Вы любите котов?");

        return view;
    }
}