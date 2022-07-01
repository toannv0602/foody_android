package com.example.foody.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.foody.activity.LoginActivity;
import com.example.foody.activity.ProfileActivity;
import com.example.foody.activity.RecipeDetailActivity;
import com.example.foody.adapter.ListRecipeAdapter;
import com.example.foody.helper.Contain;
import com.example.foody.helper.DatabaseLocal;
import com.example.foody.model.Recipe;
import com.example.foody.R;
import com.example.foody.model.RecipeDetail;
import com.example.foody.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.auth.AGConnectAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RecipeFragment extends Fragment  {

    View view;
    User user;
    boolean inputSearchShowing= false;
    Toolbar toolbar;
    ImageView imageAvatar;
    EditText searchView;
    DatabaseReference mReference;
    RecyclerView recyclerView;
    ListRecipeAdapter listRecipeAdapter;
    List<Recipe> listRecipe;
    List<Recipe> listRecipeFilter;
    DatabaseLocal dbHelper ;
    SQLiteDatabase db ;


    public RecipeFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.filter);

        LinearLayout filterTime = bottomSheetDialog.findViewById(R.id.filter_time);
        LinearLayout filterVegan = bottomSheetDialog.findViewById(R.id.filter_vegan);
        LinearLayout filterLike = bottomSheetDialog.findViewById(R.id.filter_like);
        LinearLayout listAll = bottomSheetDialog.findViewById(R.id.list_all);

        filterLike.setOnClickListener(v -> {
            filterByLike();
            bottomSheetDialog.dismiss();
        });

        filterVegan.setOnClickListener(v -> {
            filterByVegan();
            bottomSheetDialog.dismiss();
        });

        filterTime.setOnClickListener(v -> {
            filterByTime();
            bottomSheetDialog.dismiss();
        });

        listAll.setOnClickListener(v -> {
            listRecipeAdapter.newListData(listRecipe);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void filterByLike(){
        listRecipeFilter.sort(Comparator.comparing(a -> a.totalLike));
        listRecipeAdapter.newListData(listRecipeFilter);
    }

    private void filterByVegan(){
        List listVegan = listRecipeFilter.stream().filter(c -> c.vegan == true).collect(Collectors.toList());
        listRecipeAdapter.newListData(listVegan);
    }

    // tang dan
    private void filterByTime(){
        listRecipeFilter.sort(Comparator.comparing(a -> a.totalTime));
        // desc -- giam dan
        Collections.reverse(listRecipeFilter);
        listRecipeAdapter.newListData(listRecipeFilter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipe, container, false);
        mapview();
        listRecipe = new ArrayList<Recipe> ();
        listRecipeFilter = new ArrayList<Recipe> ();
        LinearLayoutManager myLayout = new LinearLayoutManager(getContext());
        myLayout.setStackFromEnd(false);
        recyclerView.setLayoutManager(myLayout);
        listRecipeAdapter = new ListRecipeAdapter(listRecipe,Contain.LIST_RECIPE, user);
        recyclerView.setAdapter(listRecipeAdapter);
        getListRecipe();
        return view;
    }


    void mapview() {
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) ||
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        //do your action here
                        Log.e("Print ", searchView.getText().toString());
                        return true;
                    }
                return false;
            }
        } );
        recyclerView = view.findViewById(R.id.list_recipe);
        imageAvatar = view.findViewById(R.id.image_user);
        imageAvatar.setOnClickListener(view -> {
            Intent detail = new Intent(view.getContext(), ProfileActivity.class);
            detail.putExtra("userId", user.id);
            startActivity(detail);
        });
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
        toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu_recipe);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        AGConnectAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                        return true;
                    case  R.id.search:
                        if (!inputSearchShowing){
                            item.setIcon(R.drawable.ic_cancel);
                            searchView.setVisibility(View.VISIBLE);
                            inputSearchShowing = true;
                        }
                        else {
                            item.setIcon(R.drawable.ic_baseline_search_24);
                            searchView.setVisibility(View.GONE);
                            inputSearchShowing=false;
                        }

                        return true;
                    default:
                        showBottomSheetDialog();
                        return true;
                }
            }
        });
    }


    void searchList (){

    }



    void getListRecipe() {
        DatabaseReference mRecipe = mReference.child("RecipeDetail");
        mRecipe.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Recipe recipe = new Recipe();
                recipe.id = snapshot.child("Id").getValue().toString();
                recipe.liked = false;
                try{
                    if (!snapshot.child("PeopleLike").child(user.id).getValue().toString().equals("")){
                        recipe.liked = true;
                    }
                }catch (Exception e){
                    Log.e("Recipe Fragment","No Like");
                }
                recipe.imageName = snapshot.child("ImageName").getValue().toString();
                recipe.imageType = snapshot.child("ImageType").getValue().toString();
                recipe.totalLike = Integer.parseInt(snapshot.child("Like").getValue().toString());
                recipe.totalTime = Integer.parseInt(snapshot.child("TotalTime").getValue().toString());
                recipe.summary = snapshot.child("Summary").getValue().toString();
                recipe.title = snapshot.child("Title").getValue().toString();
                recipe.vegan = (boolean) snapshot.child("Vegan").getValue();
                listRecipe.add(recipe);
                listRecipeFilter.add(recipe);
                listRecipeAdapter.notifyItemInserted(listRecipe.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Recipe recipe = new Recipe();
                recipe.id = snapshot.child("Id").getValue().toString();
                recipe.liked = false;
                try{
                    if (!snapshot.child("PeopleLike").child(user.id).getValue().toString().equals("")){
                        recipe.liked = true;
                    }
                }catch (Exception e){
                    Log.e("Recipe Fragment","No Like");
                }
                recipe.imageName = snapshot.child("ImageName").getValue().toString();
                recipe.imageType = snapshot.child("ImageType").getValue().toString();
                recipe.totalLike = Integer.parseInt(snapshot.child("Like").getValue().toString());
                recipe.totalTime = Integer.parseInt(snapshot.child("TotalTime").getValue().toString());
                recipe.summary = snapshot.child("Summary").getValue().toString();
                recipe.title = snapshot.child("Title").getValue().toString();
                recipe.vegan = (boolean) snapshot.child("Vegan").getValue();
                for (int i = 0;i<listRecipe.size();i++){
                    if (listRecipe.get(i).id == recipe.id){
                        listRecipe.set(i,recipe);
                        listRecipeAdapter.notifyItemChanged(i);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}