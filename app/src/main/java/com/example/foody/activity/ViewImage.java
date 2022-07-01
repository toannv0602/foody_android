package com.example.foody.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.foody.R;
import com.example.foody.helper.Contain;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ViewImage extends AppCompatActivity {

    ImageView imageView, buttonBack;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mapView();
        Intent result = getIntent();
        String fileName="";
        String fileType="";
        if (result.hasExtra("imageNamePr")) {
            fileName = result.getStringExtra("imageNamePr");
        }
        if(result.hasExtra("imageTypePr")){
            fileType = result.getStringExtra("imageTypePr");
        }
        if (!fileName.isEmpty() && !fileType.isEmpty()){
            loadImage(fileName, fileType);
        }
        setListenerView();
    }

    private void loadImage(String fileName, String fileType) {
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            StorageReference reference = storageReference.child("ImageProfile/" + fileName+"."+fileType);
            final File localFile = File.createTempFile(fileName, fileType);
            reference.getFile(localFile).addOnSuccessListener(downloadResult -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);

            }).addOnFailureListener(e -> {
                Log.e("ImageView", " get image profile " + fileName + " fail");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListenerView() {
        buttonBack.setOnClickListener(view -> {
            Log.e("Click back icon ", "Back image");
            finish();
        });
    }

    private void mapView(){
        imageView = findViewById(R.id.image_all);
        buttonBack = findViewById(R.id.ic_back_img);

        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.clear();
    }

}