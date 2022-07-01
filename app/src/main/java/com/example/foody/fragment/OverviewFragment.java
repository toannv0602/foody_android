package com.example.foody.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.foody.R;
import com.example.foody.helper.Contain;
import com.example.foody.helper.DatabaseLocal;
import com.example.foody.model.Ingredients;
import com.example.foody.model.Process;
import com.example.foody.model.RecipeDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverviewFragment extends Fragment {

    View view;
    DatabaseReference mReference;
    Boolean fromLocal;
    TextView tvDescription;
    TextView tvProcess;
    TextView tvTotalTime;
    ImageView iconLike;
    TextView tvTotalLike;
    ProgressDialog dialog;
    LinearLayout image ;
    RecipeDetail recipeDetail;
    int sizeIngredientLoadedImage = 0;
    Boolean loadedImageRecipe = false;
    String recipeId;
    String userId;
    RadioButton rdCheap,rdDairyFree, rdGlutent, rdHeathy, rdVegan, rdVegetarian;


    public OverviewFragment(String id, Boolean fromLocal, String userId) {
        // Required empty public constructor
        recipeId= id;
        this.userId = userId;
        this.fromLocal = fromLocal;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }
    public void deleteInDatabase() {
        DatabaseLocal dbHelper = new DatabaseLocal(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DatabaseLocal.deleteRecipe(db, recipeId);
    }


    public void saveToDatabase(){
        DatabaseLocal dbHelper = new DatabaseLocal(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DatabaseLocal.addRecipe(db,recipeDetail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_overview, container, false);
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
        recipeDetail = new RecipeDetail();
        mapView();
        dialog.show();
        if (fromLocal){
            getRecipeDetailFromLocal(this.recipeId);
        }else {
            getRecipeDetailFromRemote(this.recipeId);
        }

        return view;
    }
    void mapView (){
        image = view.findViewById(R.id.image);
        rdCheap = view.findViewById(R.id.rdCheap);
        rdVegetarian = view.findViewById(R.id.rdVegetarian);
        rdDairyFree = view.findViewById(R.id.rdDairy);
        rdGlutent = view.findViewById(R.id.rdGlutentFree);
        rdVegan = view.findViewById(R.id.rdVegan);
        rdHeathy = view.findViewById(R.id.rdHealthy);
        tvProcess = view.findViewById(R.id.tvStep);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvTotalLike = (TextView) view.findViewById(R.id.totalLike) ;
        tvTotalTime = (TextView) view.findViewById(R.id.totalTime) ;
        iconLike = (ImageView) view.findViewById(R.id.ic_like);
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");
    }
    void getRecipeDetailFromLocal (String recipeId){
        DatabaseLocal dbHelper = new DatabaseLocal(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        recipeDetail  =  DatabaseLocal.getRecipeById( recipeId,db);
        recipeDetail.setProcessList(DatabaseLocal.getListProcess(recipeId,db));
        setViewFromData();

        BitmapDrawable background = new BitmapDrawable(recipeDetail.getImageRecipe());
        image.setBackgroundDrawable(background);
        loadedImageRecipe = true;
        if ( loadedImageRecipe){
            dialog.dismiss();
        }
        String  str = "";
        recipeDetail.getProcessList().sort(Comparator.comparing(Process::getStep));
        for (Process item : recipeDetail.getProcessList()){
            str += "- Bước " + item.getStep() + " : " + item.getAction() + "\n";
        }
        tvProcess.setText(str);

    }


    @SuppressLint("SetTextI18n")
    void setViewFromData (){
        rdCheap.setChecked(recipeDetail.isCheap());
        rdVegetarian.setChecked(recipeDetail.isVegetarian());
        rdDairyFree.setChecked(recipeDetail.isDairyFree());
        rdGlutent.setChecked(recipeDetail.isGlutentFree());
        rdVegan.setChecked(recipeDetail.isVegan());
        rdHeathy.setChecked(recipeDetail.isHealthy());
        tvDescription.setText(recipeDetail.getDescription());
        tvTotalTime.setText(Integer.toString(recipeDetail.getTotalTime()));
        tvTotalLike.setText(Integer.toString(recipeDetail.getTotalLike()));
        if(recipeDetail.isLiked()){
            iconLike.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
        else {
            iconLike.setImageResource(R.drawable.ic_no_favorite);
        }
        iconLike.setOnClickListener(view1 -> {
            if (!recipeDetail.isLiked()){
                DatabaseReference mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference().child("RecipeDetail").child(recipeDetail.getRecipeId());
                mReference.child("PeopleLike").child(userId).setValue(userId);
                mReference.child("Like").setValue(recipeDetail.getTotalLike()+1);
            }
            else {
                DatabaseReference mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference().child("RecipeDetail").child(recipeDetail.getRecipeId());
                mReference.child("PeopleLike").child(userId).removeValue();
                mReference.child("Like").setValue(recipeDetail.getTotalLike()-1);
            }
        });

    }

    void getRecipeDetailFromRemote(String recipeId){
        DatabaseReference mRecipeDetail = mReference.child("RecipeDetail").child(recipeId);
        mRecipeDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeDetail.setCheap((boolean) snapshot.child("Cheap").getValue());
                recipeDetail.setDairyFree((boolean) snapshot.child("DairyFree").getValue());
                recipeDetail.setLiked(false);
                try{
                    if (!snapshot.child("PeopleLike").child(userId).getValue().toString().equals("")){
                        recipeDetail.setLiked(true);
                    }
                }catch (Exception e){
                    Log.e("Recipe Fragment","No Like");
                }
                recipeDetail.setDescription(snapshot.child("Description").getValue().toString());
                recipeDetail.setGlutentFree((boolean) snapshot.child("GlutentFree").getValue());
                recipeDetail.setHealthy((boolean) snapshot.child("Healthy").getValue());
                recipeDetail.setId(snapshot.child("Id").getValue().toString());
                recipeDetail.setImageName(snapshot.child("ImageName").getValue().toString());
                recipeDetail.setTotalLike(Integer.parseInt(snapshot.child("Like").getValue().toString()));
                recipeDetail.setImageType(snapshot.child("ImageType").getValue().toString());
                recipeDetail.setTitle(snapshot.child("Title").getValue().toString());
                recipeDetail.setRecipeId(snapshot.child("RecipeId").getValue().toString());
                recipeDetail.setSummary(snapshot.child("Summary").getValue().toString());
                recipeDetail.setTotalTime(Integer.parseInt(snapshot.child("TotalTime").getValue().toString()));
                recipeDetail.setVegan((boolean) snapshot.child("Vegan").getValue());
                recipeDetail.setVegetarian((boolean) snapshot.child("Vegetarian").getValue());
                recipeDetail.setVegetarian((boolean) snapshot.child("Vegetarian").getValue());

                setViewFromData();

                List<Ingredients> ingredientsList = new ArrayList<>();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference reference = storageReference.child("ImageRecipe/" + recipeDetail.getRecipeId() + "/" + recipeDetail.getImageName() + "." + recipeDetail.getImageType());
                try {
                    final File localFile = File.createTempFile(recipeDetail.getImageName(), recipeDetail.getImageType());
                    reference.getFile(localFile).addOnSuccessListener(downloadResult -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        recipeDetail.setImageRecipe(bitmap);
                        BitmapDrawable background = new BitmapDrawable(bitmap);
                        image.setBackgroundDrawable(background);
                        loadedImageRecipe = true;
                        if (sizeIngredientLoadedImage == ingredientsList.size() && loadedImageRecipe){
                            dialog.dismiss();;
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("ListRecipeAdapter", " get image  fail");
                        loadedImageRecipe = true;
                        if (sizeIngredientLoadedImage == ingredientsList.size() && loadedImageRecipe){
                            dialog.dismiss();;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                DatabaseReference mIngredients = mRecipeDetail.child("Ingredients");
                mIngredients.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        for (DataSnapshot itemIng : snapshot1.getChildren()){
                            Ingredients ingredients = new Ingredients();
                            ingredients.setId(Integer.parseInt(itemIng.child("Id").getValue().toString()));
                            ingredients.setImageName(itemIng.child("ImageName").getValue().toString());
                            ingredients.setImageType(itemIng.child("ImageType").getValue().toString());
                            ingredients.setName(itemIng.child("Name").getValue().toString());
                            ingredients.setUnit(itemIng.child("Unit").getValue().toString());
                            ingredients.setWeight(Integer.parseInt(itemIng.child("Weight").getValue().toString()));
                            StorageReference reference = storageReference.child("ImageRecipe/" + recipeDetail.getRecipeId() + "/Ingredients/"+ ingredients.getImageName() +  "." + ingredients.getImageType());
                            try {
                                final File localFile = File.createTempFile("anhnguyenlieu", ingredients.getImageType());
                                reference.getFile(localFile).addOnSuccessListener(downloadResult -> {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    ingredients.setImageBitmap(bitmap);
                                    sizeIngredientLoadedImage ++;
                                    if (sizeIngredientLoadedImage == ingredientsList.size() && loadedImageRecipe){
                                        dialog.dismiss();;
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.e("ListRecipeAdapter", " get image  fail");
                                    sizeIngredientLoadedImage ++;
                                    if (sizeIngredientLoadedImage == ingredientsList.size() && loadedImageRecipe){
                                        dialog.dismiss();;
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e (">>>>>>>>>>>>>>>>>>>>>>>>>",e.getMessage());
                            }

                            ingredientsList.add(ingredients);
                        }
                        recipeDetail.setIngredientsList(ingredientsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference mProcess = mRecipeDetail.child("Process");
                mProcess.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Process> processList = new ArrayList<>();
                        String str = "";
                        for (DataSnapshot itemProcess : snapshot.getChildren()){
                            Process process = new Process();
                            process.setAction(itemProcess.child("Action").getValue().toString());
                            process.setStep(Integer.parseInt(itemProcess.child("Step").getValue().toString()));
                            processList.add(process);
                        }
                        processList.sort(Comparator.comparing(Process::getStep));
                        for (Process item : processList){
                            str += "- Bước " + item.getStep() + " : " + item.getAction() + "\n";
                        }
                        recipeDetail.setProcessList(processList);
                        tvProcess.setText(str);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}