package com.example.foody.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.foody.R;
import com.example.foody.activity.LoginActivity;
import com.example.foody.activity.ProfileActivity;
import com.example.foody.adapter.ListRecipeAdapter;
import com.example.foody.helper.Contain;
import com.example.foody.helper.DatabaseLocal;
import com.example.foody.model.Recipe;
import com.example.foody.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.huawei.agconnect.auth.AGConnectAuth;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {

    View view;
    DatabaseReference mReference;
    User user ;
    Toolbar toolbar;
    TextView emptyText;
    private RecyclerView recyclerView;
    private ListRecipeAdapter listRecipeAdapter;
    List<Recipe> listRecipe;
    public FavoriteFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }
    public FavoriteFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        listRecipe = new ArrayList<Recipe>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mapview();
        LinearLayoutManager myLayout= new LinearLayoutManager(getContext());
        myLayout.setStackFromEnd(false);
        recyclerView.setLayoutManager(myLayout);
        recyclerView.setHasFixedSize(true);
        getListRecipe();
        return view;
    }


    void mapview(){
        recyclerView = view.findViewById( R.id.list_favorite);
        mReference = FirebaseDatabase.getInstance(Contain.REALTIME_DATABASE).getReference();
        emptyText = view.findViewById(R.id.empty_text);
        toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu_favorite);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete){
                    boolean haveUpdate = false;
//              getContext().getDatabasePath(DatabaseLocal.DATABASE_NAME).delete();
                    DatabaseLocal dbHelper = new DatabaseLocal(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    for (String id : listRecipeAdapter.getListPicked()) {
                        if (!id.equals("")) {

                            DatabaseLocal.deleteRecipe(db, id);
                            haveUpdate = true;
                        }
                    }
                    if (!haveUpdate) {
                        DatabaseLocal.deleteRecipe(db, null);
                    }
                    getListRecipe();
                    return true;
                }
                else return false;
            }
        });
    }


    public void getListRecipe(){
        DatabaseLocal dbHelper = new DatabaseLocal(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        listRecipe =  DatabaseLocal.getListRecipe(db);
        listRecipeAdapter = new ListRecipeAdapter(listRecipe,Contain.LIST_FAVORITE, user);
        recyclerView.setAdapter(listRecipeAdapter);
        if (listRecipe.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}