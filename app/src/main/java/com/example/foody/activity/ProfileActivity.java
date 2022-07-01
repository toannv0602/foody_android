package com.example.foody.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.foody.R;
import com.example.foody.fragment.CommentFragment;
import com.example.foody.helper.Contain;
import com.example.foody.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    public static int SELECT_IMAGE_CODE = 1;
    private ImageView backIcon;
    DatabaseReference mReference;
    CardView cardView;
    ImageView imageUser;
    Dialog dialog;
    TextView userNameBar, emailBar, userNamePr, emailPr, camera;
    String userId;
    CoordinatorLayout layout;
    Uri uri;
    String fileNamePr = "";
    String fileTypePr = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        mapview();
        Intent result = getIntent();
        if (result.hasExtra("userId")) {
            userId = result.getStringExtra("userId");
        }
        setActionbar();
        setListenerView();
        getUser(userId);

    }

    private void loadImgClient(User user) {
        if (!user.imageName.isEmpty() && !user.imageType.isEmpty()) {
            try {
                final String name = user.imageName;
                final String type = user.imageType;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference reference = storageReference.child("ImageProfile/" + user.imageName + "." + user.imageType);
                final File localFile = File.createTempFile(name, type);
                reference.getFile(localFile).addOnSuccessListener(downloadResult -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageUser.setImageBitmap(bitmap);
                }).addOnFailureListener(e -> {
                    Log.e("Profile", " get image profile " + user.imageName + " fail");
                });
            } catch (IOException e) {
            }
        }

    }

    private void setListenerView() {

        camera.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), SELECT_IMAGE_CODE);
        });

        imageUser.setOnClickListener(view -> {

            Intent detail = new Intent(ProfileActivity.this, ViewImage.class);
            detail.putExtra("imageNamePr", fileNamePr);
            detail.putExtra("imageTypePr", fileTypePr);
            startActivity(detail);
        });

    }

    private void setDialog(boolean show) {
        if (show) dialog.show();
        else dialog.dismiss();
    }

    void mapview() {
        cardView = findViewById(R.id.cardView);
        imageUser = findViewById(R.id.image_user);
        userNameBar = findViewById(R.id.userNameBar);
        emailBar = findViewById(R.id.emailBar);
        userNamePr = findViewById(R.id.userNamePr);
        emailPr = findViewById(R.id.emailPr);
        layout = findViewById(R.id.root_profile);
        camera = findViewById(R.id.change_photo);
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        // This should be called once in your Fragment's onViewCreated() or in Activity onCreate() method to avoid dialog duplicates.
        dialog = builder.create();
    }

    private void loadInfoUser(User user) {
        userNameBar.setText(user.userName);
        userNamePr.setText(user.userName);
        emailPr.setText(user.email);
        emailBar.setText(user.email);
    }

    void setActionbar() {
        backIcon = findViewById(R.id.back_home_profile);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Click back icon ", "100");
                finish();
            }
        });

    }

    public void getUser(String userId) {
        DatabaseReference profile = mReference.child("User").child(userId).child("Profile");
        User user = new User();
        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.userName = dataSnapshot.child("UserName").getValue().toString();
                user.email = dataSnapshot.child("Email").getValue().toString();
                try {
                    user.imageName = dataSnapshot.child("ImageName").getValue().toString();
                    user.imageType = dataSnapshot.child("ImageType").getValue().toString();
                    Log.e("ProfileActivity", "get avt thành công");
                    fileNamePr = user.imageName;
                    fileTypePr = user.imageType;
                    Log.e("FileName: ", fileNamePr);
                    Log.e("FileType: ", fileTypePr);
                    loadImgClient(user);
                } catch (Exception e) {
                    Log.e("ProfileActivity", "no image");
                }
                loadInfoUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("Profile:", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            setDialog(true);
            uri = data.getData();
            String type = getExtension(uri);
            DatabaseReference profile = mReference.child("User").child(userId).child("Profile");
            profile.child("ImageName").setValue(userId);
            profile.child("ImageType").setValue(type);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            StorageReference reference = storageReference.child("ImageProfile/" + userId + "." + type);
            UploadTask task = reference.putFile(uri);
            task.addOnFailureListener(e -> {
                Log.e("error aaaaaaaaaaaaaa", e.getMessage());
                setDialog(false);
                Toast.makeText(ProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_LONG).show();
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e(">>>>>>>>>>>>>>>>>>>>>", "succes");
                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                    imageUser.setImageURI(uri);
                    setDialog(false);
//                    dialog.dismiss();
                }
            });
        }
    }

    String getExtension(Uri url) {
        ContentResolver cr = ProfileActivity.this.getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(url));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.clear();
    }
}