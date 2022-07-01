package com.example.foody.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.foody.R;
import com.example.foody.adapter.ViewPageAdapter;
import com.example.foody.fragment.CommentFragment;
import com.example.foody.fragment.IngredientFragment;
import com.example.foody.fragment.OverviewFragment;
import com.example.foody.helper.Contain;
import com.example.foody.helper.DatabaseLocal;
import com.example.foody.model.Recipe;
import com.example.foody.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final int HAVE_CHANGE_DATABASE = 1;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    Boolean isFavorite = false;
    private  Recipe recipe ;
    private  boolean formLocal = false;
    User user;
    ViewPageAdapter adapter;
    private ImageView backIcon , favoriteIcon ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setActionbar();
        getData();
        tabLayout = findViewById(R.id.tabRecipeDetail);
        viewPager2 = findViewById(R.id.viewPagerDetail);
        adapter = new ViewPageAdapter(this, recipe, user,formLocal);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("Overview"); break;
                    case 1: tab.setText("Ingredient"); break;
                    case 2: tab.setText("Comment"); break;
                }
            }
        }).attach();

    }

    void setActionbar(){
        getSupportActionBar().hide();
        backIcon = findViewById(R.id.ic_back);
        favoriteIcon = findViewById(R.id.ic_favorite);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Click back icon ", "100");
                finish();
            }
        });
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(HAVE_CHANGE_DATABASE);
                if (isFavorite){
                    adapter.deleteDataInDatabase();
                    favoriteIcon.setImageResource(R.drawable.ic_baseline_star_24);
                }else{
                    adapter.saveDataToDatabase();
                    favoriteIcon.setImageResource(R.drawable.ic_star_yellow);
                }

            }
        });

    }



    void getData(){
        recipe = new Recipe();
        user = new User();
        Intent result  = getIntent();
        if (result.hasExtra("RecipeId")){
            Log.e("resultFormMainActivity", result.getStringExtra("RecipeId"));
            recipe.id = result.getStringExtra("RecipeId");
        }
        if (result.hasExtra("ImageType")){
            user.imageType = result.getStringExtra("ImageType");
        }
        if (result.hasExtra("ImageName")){
            user.imageName = result.getStringExtra("ImageName");
        }
        if (result.hasExtra("UserName")){
            user.userName = result.getStringExtra("UserName");
        }
        if (result.hasExtra("UserID")){
            user.id = result.getStringExtra("UserID");
        }
        if (result.hasExtra("fromLocal")){
            formLocal = result.getBooleanExtra("fromLocal", false);
        }
        DatabaseLocal dbHelper = new DatabaseLocal(RecipeDetailActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        isFavorite = DatabaseLocal.haveRecipeInDataBase(recipe.id,db);
        if(isFavorite){
            favoriteIcon.setImageResource(R.drawable.ic_star_yellow);
        }
        else {
            favoriteIcon.setImageResource(R.drawable.ic_baseline_star_24);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if(fragment instanceof CommentFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}