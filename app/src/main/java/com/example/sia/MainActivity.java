package com.example.sia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;


public class MainActivity extends AppCompatActivity {

    Button selectImage, shareButton;
    ImageView imageView;
    TextView showhere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showhere = findViewById(R.id.showhere);
        selectImage = findViewById(R.id.selectImage);
        imageView = findViewById(R.id.image_view);
        selectImage.setOnClickListener(view -> checkPermission());
        shareButton = (Button) findViewById(R.id.shareButton);

        shareButton.setOnClickListener(view -> {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
            if (bitmapDrawable != null){
                Bitmap bitmap = bitmapDrawable.getBitmap();
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"Image", null);
                Uri bitmapUri = Uri.parse(bitmapPath);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                startActivity(Intent.createChooser(shareIntent,"Share Image"));
            }
            else {
                Toast.makeText(getApplicationContext(), "Please choose an image and save before sharing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pickImage();
        } else {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            switch (requestCode) {
                case 100:
                    Intent intent = new Intent(MainActivity.this,
                            DsPhotoEditorActivity.class);
                    intent.setData(uri);
                    intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
                            "Images");
                    intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR,
                            Color.parseColor("#FF6200EE"));
                    intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,
                            Color.parseColor("#FFFFFFFF"));
                    intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
                            new int[]{DsPhotoEditorActivity.TOOL_WARMTH,
                                    DsPhotoEditorActivity.TOOL_PIXELATE});
                    startActivityForResult(intent, 101);
                    break;
                case 101:
                    showhere.setVisibility(View.GONE);
                    imageView.setImageURI(uri);
                    Toast.makeText(getApplicationContext(), "Photo Saved", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}