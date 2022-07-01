package com.example.foody.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foody.R;
import com.example.foody.adapter.ListCommentAdapter;
import com.example.foody.helper.Contain;
import com.example.foody.model.CommentRecipe;
import com.example.foody.model.Recipe;
import com.example.foody.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class CommentFragment extends Fragment {

    public static int SELECT_IMAGE_CODE = 1;

    boolean showImage;
    boolean canSend;

    ImageButton camera, send, cancel;
    ImageView image ;
    RecyclerView recyclerView;
    ConstraintLayout layoutImage ;
    EditText comment;
    TextView emptyText;
    View view;
    Uri uri ;
    Recipe recipe;
    User user;
    DatabaseReference mReference;
    ListCommentAdapter listCommentAdapter;
    List <CommentRecipe> listComment;

    public CommentFragment(Recipe recipe , User user) {
        this.recipe = recipe;
        this.user = user ;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment, container, false);
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
        showImage = false;
        canSend = false;
        mapView();
        LinearLayoutManager myLayout= new LinearLayoutManager(getContext());
        myLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(myLayout);
        recyclerView.setHasFixedSize(true);
        listComment = new ArrayList<>();
        listCommentAdapter = new ListCommentAdapter(listComment,recipe.id);
        recyclerView.setAdapter(listCommentAdapter);
        if (listComment.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
        setListenerView();
        getListComment();
        return view;
    }

    void mapView(){
        layoutImage = view.findViewById(R.id.image_container);
        image = view.findViewById(R.id.image_comment);
        recyclerView = view.findViewById(R.id.list_comment);
        camera = view.findViewById(R.id.button_camera);
        send = view.findViewById(R.id.button_send);
        send.setImageResource(R.drawable.ic_send);
        emptyText = view.findViewById(R.id.empty_text);
        cancel = view.findViewById(R.id.button_cancel);
        comment = view.findViewById(R.id.txt_comment);
    }
    void setListenerView(){
        cancel.setOnClickListener(view -> {
            layoutImage.setVisibility(View.GONE);
            showImage = false;
            if (!comment.getText().toString().equals("") || showImage){
                send.setImageResource(R.drawable.ic_send_active);
                canSend = true;
            }
            else {
                send.setImageResource(R.drawable.ic_send);
                canSend = false;
            }
        });
        camera.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // multiple image
            intent.setAction(Intent.ACTION_GET_CONTENT);
            CommentFragment.this.startActivityForResult(Intent.createChooser(intent,"Chọn Ảnh"),SELECT_IMAGE_CODE);
        });
        send.setOnClickListener(view -> {
            if (canSend)
            sendComment();
            send.setImageResource(R.drawable.ic_send);
            canSend = false;
            layoutImage.setVisibility(View.GONE);
            showImage = false;
            comment.setText(null);
        });

        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String text = charSequence.toString();
                if (!text.trim().equals("") || showImage){
                    send.setImageResource(R.drawable.ic_send_active);
                    canSend = true;
                }
                else {
                    send.setImageResource(R.drawable.ic_send);
                    canSend = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void  setImage(Intent data){
        if (data != null){
            uri = data.getData();
            image.setImageURI(uri);
            layoutImage.setVisibility(View.VISIBLE);
            send.setImageResource(R.drawable.ic_send_active);
            canSend = true;
            showImage = true;
        }

        // multiple image
         String imagePath;
         List<Uri>  imagePathList = new ArrayList<>();
        if (data.getClipData() != null) {

            int count = data.getClipData().getItemCount();
            for (int i=0; i<count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                imagePathList.add(imageUri);
            }
        }
        else if (data.getData() != null) {
            Uri imgUri = data.getData();
            //getImageFilePath(imgUri);
        }


    }


    void getListComment(){
        // get comment
        DatabaseReference comment = mReference.child("RecipeDetail").child(recipe.id).child("CommentRecipe");

        comment.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentRecipe commentRecipe = new CommentRecipe();
                try{
                    commentRecipe.imageName = snapshot.child("ImageName").getValue().toString();
                    commentRecipe.imageType = snapshot.child("ImageType").getValue().toString();
                } catch (Exception e){
                    Log.e("CommentFragment Exception firebase", e.getMessage());
                }
                try{
                    commentRecipe.author.imageName = snapshot.child("AuthorImageName").getValue().toString();
                    commentRecipe.author.imageType = snapshot.child("AuthorImageType").getValue().toString();
                } catch (Exception e){
                    Log.e("CommentFragment Exception firebase", e.getMessage());
                }
                commentRecipe.key = snapshot.getKey();
                commentRecipe.author.userName = snapshot.child("AuthorName").getValue().toString();
                commentRecipe.content = snapshot.child("Content").getValue().toString();
                commentRecipe.author.id = snapshot.child("Author").getValue().toString();
                listComment.add(commentRecipe);
                listCommentAdapter.notifyItemInserted(listComment.size()-1);
                if (listComment.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentRecipe commentRecipe = new CommentRecipe();
                String key = snapshot.getKey();
                try{
                    commentRecipe.imageName = snapshot.child("ImageName").getValue().toString();
                    commentRecipe.imageType = snapshot.child("ImageType").getValue().toString();
                } catch (Exception e){
                    Log.e("CommentFragment Exception firebase", e.getMessage());
                }
                try{
                    commentRecipe.author.imageName = snapshot.child("AuthorImageName").getValue().toString();
                    commentRecipe.author.imageType = snapshot.child("AuthorImageType").getValue().toString();
                } catch (Exception e){
                    Log.e("CommentFragment Exception firebase", e.getMessage());
                }
                commentRecipe.author.userName = snapshot.child("AuthorName").getValue().toString();
                commentRecipe.content = snapshot.child("Content").getValue().toString();
                commentRecipe.author.id = snapshot.child("Author").getValue().toString();
                for (int i =0 ;i<listComment.size();i++){
                    if (listComment.get(i).key.equals(key)){
                        listComment.set(i,commentRecipe);
                        listCommentAdapter.notifyItemChanged(i);
                    }
                }

                if (listComment.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                for (int i =0 ;i<listComment.size();i++){
                    if (listComment.get(i).key.equals(key)){
                        listComment.remove(i);
                        listCommentAdapter.deleteItem(i);

                    }
                }

                if (listComment.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void sendComment (){
        DatabaseReference newComment  = mReference.child("RecipeDetail").child(recipe.id).child("CommentRecipe").push();
        String key =  newComment.getKey();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Author",user.id);
        hashMap.put("AuthorName",user.userName);
        hashMap.put("Content",comment.getText().toString().trim());
        if (user.imageName != null){
            hashMap.put("AuthorImageName",user.imageName);
            hashMap.put("AuthorImageType",user.imageType);
        }
        if (showImage){
            hashMap.put("ImageName",key);
            String type = getExtension(uri);
            hashMap.put("ImageType",type);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            StorageReference reference = storageReference.child("ImageRecipe/" + recipe.id + "/Comment/" + key+"."+ type );
            UploadTask task = reference.putFile(uri);
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("error aaaaaaaaaaaaaa",exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    newComment.setValue(hashMap);
                    Log.e(">>>>>>>>>>>>>>>>>>>>>","succes");
                }
            });

        }
        else {
            newComment.setValue(hashMap);
        }

    }

    public static String getFileExtensionFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty() &&
                    Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }

    String getExtension(Uri url){
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(url));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CommentFragment.SELECT_IMAGE_CODE) {
            setImage(data);
        }
    }

}