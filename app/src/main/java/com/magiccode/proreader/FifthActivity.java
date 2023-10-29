package com.magiccode.proreader;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class FifthActivity extends AppCompatActivity {

    private TextView scannedtext;
    private ImageView iv, copy;
    private Button scan, recognize;

    private static final String TAG = "TAG_MAIN";
    private Uri imageUri = null;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;

    private ProgressDialog progressDialog;
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        scannedtext = findViewById(R.id.scannedtext);
        iv = findViewById(R.id.image);
        copy = findViewById(R.id.copytxt);
        scan = findViewById(R.id.scan);
        recognize = findViewById(R.id.decode);
        scannedtext.setInputType(InputType.TYPE_NULL);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        getSupportActionBar().hide();

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showinputimageDialog();
            }
        });
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null) {
                    Toast.makeText(FifthActivity.this, "Pick the image first to scan", Toast.LENGTH_SHORT).show();
                } else {
                    recognizeTextfromImage();
                }
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String output = scannedtext.getText().toString();
                if (output.isEmpty())
                    Toast.makeText(FifthActivity.this, "The Output is null or empty", Toast.LENGTH_LONG).show();
                else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("MyData", output);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(FifthActivity.this, "Text is Copied!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void recognizeTextfromImage() {
        Log.d(TAG, "recognizeTextfromImage");
        progressDialog.setMessage("Preparing Image...");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);
            progressDialog.setMessage("Recognizing Text...");
            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            progressDialog.dismiss();
                            String RecognizedText = text.getText();
                            Log.d(TAG, "onSuccess: RecognizedText: " + RecognizedText);
                            scannedtext.setText(RecognizedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.e(TAG, "onFailure:", e);
                            Toast.makeText(FifthActivity.this, "Failed Recognizing Text Due To" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e(TAG, "recognizeTextfromImage: ", e);
            Toast.makeText(this, "Failed Preparing Image Due To " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showinputimageDialog() {
        PopupMenu popupMenu = new PopupMenu(this, scan);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALLERY");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 1) {
                    Log.d(TAG, "onMenuItemClick: Camera Clicked...");
                    if (checkCameraPermissions()) {
                        pickImageCamera();
                    } else {
                        requestCameraPermission();
                    }
                } else if (id == 2) {
                    Log.d(TAG, "onMenuItemClick: Gallery Clicked...");
                    pickImageGallery();
                }
                return true;
            }
        });
    }

    private void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ");
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        galleryActivityResultLauncher.launch(i);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri" + imageUri);
                        iv.setImageURI(imageUri);
                    } else {
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(FifthActivity.this, "Permission Rejected", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "SAMPLE TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "SAMPLE DESCRIPTION");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: imageUri" + imageUri);
                        iv.setImageURI(imageUri);
                    } else {
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(FifthActivity.this, "Canceled", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private boolean checkCameraPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageCamera();
            } else {
                Toast.makeText(this, "Camera Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
