package com.example.foody.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foody.R;
import com.example.foody.fragment.CommentFragment;
import com.example.foody.fragment.IngredientFragment;
import com.example.foody.fragment.OverviewFragment;
import com.example.foody.helper.Contain;
import com.example.foody.model.Ingredients;
import com.example.foody.model.Process;
import com.example.foody.model.Recipe;
import com.example.foody.model.RecipeDetail;
import com.example.foody.model.User;
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

public class ViewPageAdapter extends FragmentStateAdapter {

    Recipe recipe;
    User user ;
    IngredientFragment ingredientFragment;
    CommentFragment commentFragment;
    OverviewFragment overviewFragment;
    DatabaseReference mReference;
    Boolean fromLocal;

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity, Recipe recipe, User user, Boolean fromLocal ) {
        super(fragmentActivity);
        this.fromLocal = fromLocal;
        this.recipe = recipe;
        this.user = user;
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
        ingredientFragment = new IngredientFragment(recipe.id, fromLocal);
        commentFragment =  new CommentFragment(recipe, user);
        overviewFragment =  new OverviewFragment(recipe.id, fromLocal, user.id);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return ingredientFragment;
            case 2: return commentFragment;
            default: return overviewFragment;
        }
    }
    public void saveDataToDatabase(){
        overviewFragment.saveToDatabase();
    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public void deleteDataInDatabase() {
        overviewFragment.deleteInDatabase();
    }
}
