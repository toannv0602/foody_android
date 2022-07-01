package com.example.foody.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foody.R;
import com.example.foody.adapter.ListIngredientsAdapter;
import com.example.foody.adapter.ListRecipeAdapter;
import com.example.foody.helper.Contain;
import com.example.foody.helper.DatabaseLocal;
import com.example.foody.model.Ingredients;
import com.example.foody.model.Process;
import com.example.foody.model.Recipe;
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
import java.util.List;


public class IngredientFragment extends Fragment {

    List<Ingredients> ingredientsList;
    View view;
    DatabaseReference mReference;
    private RecyclerView recyclerView;
    String recipeId ;
    private ListIngredientsAdapter listIngredientsAdapter;
    Boolean fromLocal;

    public IngredientFragment(String id, Boolean fromLocal) {
        this.recipeId = id;
        this.fromLocal = fromLocal;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_ingredient, container, false);
        mapview();
        LinearLayoutManager myLayout= new LinearLayoutManager(getContext());
        myLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(myLayout);
        recyclerView.setHasFixedSize(true);
        if (fromLocal){
            getIngredientFromLocal();
        }else {
            getIngredientFromRemote();
        }
        return view;
    }

    void mapview(){
        recyclerView = view.findViewById( R.id.list_ingredients);
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientsList = new ArrayList<Ingredients>();

    }

    void getIngredientFromLocal (){
        DatabaseLocal dbHelper = new DatabaseLocal(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ingredientsList =  DatabaseLocal.getListIngredient( recipeId,db);
        listIngredientsAdapter = new ListIngredientsAdapter(ingredientsList,recipeId, fromLocal);
        recyclerView.setAdapter(listIngredientsAdapter);
    }

    void getIngredientFromRemote(){
        DatabaseReference mRecipeDetail = mReference.child("RecipeDetail").child(recipeId);
        mRecipeDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference mIngredients = mRecipeDetail.child("Ingredients");
                mIngredients.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        List<Ingredients> ingredientsList = new ArrayList<>();
                        for (DataSnapshot itemIng : snapshot1.getChildren()){
                            Ingredients ingredients = new Ingredients();
                            ingredients.setId(Integer.parseInt(itemIng.child("Id").getValue().toString()));
                            ingredients.setImageName(itemIng.child("ImageName").getValue().toString());
                            ingredients.setImageType(itemIng.child("ImageType").getValue().toString());
                            ingredients.setName(itemIng.child("Name").getValue().toString());
                            ingredients.setUnit(itemIng.child("Unit").getValue().toString());
                            ingredients.setWeight(Integer.parseInt(itemIng.child("Weight").getValue().toString()));

                            ingredientsList.add(ingredients);
                        }
                        listIngredientsAdapter = new ListIngredientsAdapter(ingredientsList,recipeId, fromLocal);
                        recyclerView.setAdapter(listIngredientsAdapter);
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