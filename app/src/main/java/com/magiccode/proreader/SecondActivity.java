package com.magiccode.proreader;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

public class SecondActivity extends AppCompatActivity {
    private ImageView qrCodeIV;
    private EditText dataEdt, filenameEdt; // Added filenameEdt
    private Button generateQrBtn, saveqr;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);
        dataEdt = findViewById(R.id.idEdt);
        filenameEdt = findViewById(R.id.filenameEdt); // Added filenameEdt
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);
        saveqr = findViewById(R.id.savethis);
        getSupportActionBar().hide();

        // initializing onclick listener for button.
        generateQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(dataEdt.getText().toString())) {
                    // if the edittext inputs are empty then execute
                    // this method showing a toast message.
                    Toast.makeText(SecondActivity.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
                } else {
                    generateQRCodeInBackground();
                }
            }
        });

        saveqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    saveQRCodeWithCustomName();
                } else {
                    Toast.makeText(SecondActivity.this, "QR Code is not generated to save", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void generateQRCodeInBackground() {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    return barcodeEncoder.encodeBitmap(dataEdt.getText().toString(), BarcodeFormat.QR_CODE, 400, 400);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap qrBitmap) {
                if (qrBitmap != null) {
                    bitmap = qrBitmap;
                    qrCodeIV.setImageBitmap(bitmap);
                    ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
        }.execute();
    }

    private void saveQRCodeWithCustomName() {
        String customFileName = filenameEdt.getText().toString().trim();
        if (!TextUtils.isEmpty(customFileName)) {
            String fileName = customFileName + ".png"; // Add a file extension
            SaveToGallery(bitmap, fileName);
            Toast.makeText(SecondActivity.this, "File Saved", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(SecondActivity.this, "Enter a filename before saving", Toast.LENGTH_LONG).show();
        }
    }

    public void SaveToGallery(Bitmap bitmap, String fileName) {
        FileOutputStream outputStream = null;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        dir.mkdirs();

        File outFile = new File(dir, fileName);
        try {
            outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
