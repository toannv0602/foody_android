package com.example.foody.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foody.model.Ingredients;
import com.example.foody.model.Process;
import com.example.foody.model.Recipe;
import com.example.foody.model.RecipeDetail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLocal extends SQLiteOpenHelper {


    public static class RecipeTable {
        public static final String TABLE_NAME = "recipe";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_LIKE = "totalLike";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_VEGAN = "vegan";
        public static final String COLUMN_NAME_CHEAP = "cheap";
        public static final String COLUMN_NAME_DAIRY_FREE = "dairyFree";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_GLUTEN_FREE = "glutenFree";
        public static final String COLUMN_NAME_HEALTHY = "healthy";
        public static final String COLUMN_NAME_VEGETARIAN = "Vegetarian";
        public static final String ID_RECIPE = "id";

    }

    public static class RecipeIngredientTable {
        public static final String TABLE_NAME = "recipeIngredientTable";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_ID_INGREDIENT = "idIngredient";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_UNIT = "unit";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String ID_RECIPE = "id";
    }

    public static class RecipeInstructionTable {
        public static final String TABLE_NAME = "recipeInstructionTable";
        public static final String COLUMN_NAME_STEP = "step";
        public static final String COLUMN_NAME_ACTION = "buoc";
        public static final String ID_RECIPE = "id";
    }

    private static final String SQL_CREATE_TABLE_RECIPE =
            "CREATE TABLE " + RecipeTable.TABLE_NAME + " (" +
                    RecipeTable.ID_RECIPE + " TEXT PRIMARY KEY," +
                    RecipeTable.COLUMN_NAME_TITLE + " TEXT," +
                    RecipeTable.COLUMN_NAME_TIME + " TEXT," +
                    RecipeTable.COLUMN_NAME_IMAGE + " BLOB," +
                    RecipeTable.COLUMN_NAME_VEGAN + " TEXT," +
                    RecipeTable.COLUMN_NAME_CHEAP + " TEXT," +
                    RecipeTable.COLUMN_NAME_LIKE + " TEXT, " +
                    RecipeTable.COLUMN_NAME_DAIRY_FREE + " TEXT, " +
                    RecipeTable.COLUMN_NAME_VEGETARIAN + " TEXT, " +
                    RecipeTable.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    RecipeTable.COLUMN_NAME_GLUTEN_FREE + " TEXT, " +
                    RecipeTable.COLUMN_NAME_HEALTHY + " TEXT, " +
                    RecipeTable.COLUMN_NAME_SUMMARY + " TEXT)";

    private static final String SQL_CREATE_TABLE_RECIPE_INGREDIENT =
            "CREATE TABLE " + RecipeIngredientTable.TABLE_NAME + " (" +
                    RecipeIngredientTable.COLUMN_NAME_ID_INGREDIENT + " TEXT ," +
                    RecipeIngredientTable.ID_RECIPE + " TEXT , "+
                    RecipeIngredientTable.COLUMN_NAME_IMAGE + " BLOB," +
                    RecipeIngredientTable.COLUMN_NAME_UNIT + " TEXT," +
                    RecipeIngredientTable.COLUMN_NAME_WEIGHT + " TEXT," +
                    RecipeIngredientTable.COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_CREATE_TABLE_RECIPE_INSTRUCTION =
            "CREATE TABLE " + RecipeInstructionTable.TABLE_NAME + " (" +
                    RecipeInstructionTable.ID_RECIPE + " TEXT   , " +
                    RecipeInstructionTable.COLUMN_NAME_STEP + " TEXT , " +
                    RecipeInstructionTable.COLUMN_NAME_ACTION + " TEXT)";

    private static final String SQL_DELETE_TABLE_RECIPE =
            "DROP TABLE IF EXISTS " + RecipeTable.TABLE_NAME;
    private static final String SQL_DELETE_RECIPE_INGREDIENT =
            "DROP TABLE IF EXISTS " + RecipeIngredientTable.TABLE_NAME;
    private static final String SQL_DELETE_RECIPE_INSTRUCTION =
            "DROP TABLE IF EXISTS " + RecipeInstructionTable.TABLE_NAME;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "test";

    public DatabaseLocal( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_RECIPE);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_RECIPE_INGREDIENT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_RECIPE_INSTRUCTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_RECIPE);
        sqLiteDatabase.execSQL(SQL_DELETE_RECIPE_INGREDIENT);
        sqLiteDatabase.execSQL(SQL_DELETE_RECIPE_INSTRUCTION);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void addRecipe (SQLiteDatabase  db , RecipeDetail recipe){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RecipeTable.ID_RECIPE, recipe.getRecipeId());
        values.put(RecipeTable.COLUMN_NAME_IMAGE, getBitmapAsByteArray(recipe.getImageRecipe()));
        values.put(RecipeTable.COLUMN_NAME_LIKE, Integer.toString(recipe.getTotalLike()));
        values.put(RecipeTable.COLUMN_NAME_TITLE, recipe.getTitle());
        values.put(RecipeTable.COLUMN_NAME_SUMMARY, recipe.getSummary());
        values.put(RecipeTable.COLUMN_NAME_TIME, Integer.toString(recipe.getTotalTime()));
        values.put(RecipeTable.COLUMN_NAME_VEGAN, Boolean.toString((recipe.isVegan())));
        values.put(RecipeTable.COLUMN_NAME_HEALTHY, Boolean.toString(recipe.isHealthy()));
        values.put(RecipeTable.COLUMN_NAME_CHEAP, Boolean.toString(recipe.isCheap()));
        values.put(RecipeTable.COLUMN_NAME_DAIRY_FREE, Boolean.toString(recipe.isDairyFree()));
        values.put(RecipeTable.COLUMN_NAME_GLUTEN_FREE, Boolean.toString(recipe.isGlutentFree()));
        values.put(RecipeTable.COLUMN_NAME_VEGETARIAN, Boolean.toString((recipe.isVegetarian())));
        // Insert the new row, returning the primary key value of the new row
        db.insert(RecipeTable.TABLE_NAME, null, values);
        for (Ingredients item :  recipe.getIngredientsList()){
            Log.e("Click favorite icon ", item.getImageName());
            DatabaseLocal.addIngredient(db,recipe.getRecipeId(),item);
        }
        for (Process item :  recipe.getProcessList()){
            Log.e("Click favorite icon ",Integer.toString( item.getStep()));
            DatabaseLocal.addInstruction(db,item ,recipe.getRecipeId());
        }
    }
    public static void addInstruction (SQLiteDatabase  db , Process process,String idRecipe){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RecipeInstructionTable.ID_RECIPE, idRecipe);
        values.put(RecipeInstructionTable.COLUMN_NAME_ACTION, process.getAction());
        values.put(RecipeInstructionTable.COLUMN_NAME_STEP, Integer.toString(process.getStep()));
        // Insert the new row, returning the primary key value of the new row
        db.insert(RecipeInstructionTable.TABLE_NAME, null, values);
    }
    public static void addIngredient (SQLiteDatabase  db,String idRecipe , Ingredients ingredients){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RecipeIngredientTable.ID_RECIPE,idRecipe);
        values.put(RecipeIngredientTable.COLUMN_NAME_IMAGE, getBitmapAsByteArray(ingredients.getImageBitmap()));
        values.put(RecipeIngredientTable.COLUMN_NAME_NAME,ingredients.getName());
        values.put(RecipeIngredientTable.COLUMN_NAME_UNIT, ingredients.getUnit());
        values.put(RecipeIngredientTable.COLUMN_NAME_WEIGHT, Integer.toString(ingredients.getWeight()));
        values.put(RecipeIngredientTable.COLUMN_NAME_ID_INGREDIENT,Integer.toString( ingredients.getId()));
        // Insert the new row, returning the primary key value of the new row
        long  a =  db.insert(RecipeIngredientTable.TABLE_NAME, null, values);
        Log.e(">>>>>>>>>>>>>>", Long.toString(a));
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    public static void deleteRecipe (SQLiteDatabase  db ,@Nullable String id){
        if (id == null){
            db.delete(RecipeTable.TABLE_NAME, null, null);
            db.delete(RecipeInstructionTable.TABLE_NAME, null, null);
            db.delete(RecipeIngredientTable.TABLE_NAME, null, null);
        } else{
            db.delete(RecipeTable.TABLE_NAME, RecipeTable.ID_RECIPE + " = '" + id+"'", null);
            db.delete(RecipeInstructionTable.TABLE_NAME, RecipeInstructionTable.ID_RECIPE + " = '" + id+"'", null);
            db.delete(RecipeIngredientTable.TABLE_NAME, RecipeIngredientTable.ID_RECIPE + " = '" + id+"'", null);
        }


    }

    public static ArrayList<Recipe> getListRecipe(SQLiteDatabase db ){
        ArrayList listRecipeFavorite = new ArrayList<Recipe>();
        Cursor cursor = db.query(
                RecipeTable.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while(cursor.moveToNext()) {
            String id  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.ID_RECIPE));
            String title  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_TITLE));
            String summary  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_SUMMARY));
            Boolean vegan  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_VEGAN)));
            int time  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_TIME)));
            int like  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_LIKE)));
            byte[] image  = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            Recipe recipe = new Recipe();
            recipe.id = id;
            recipe.imageBitmap = bitmap;
            recipe.summary = summary;
            recipe.title = title;
            recipe.totalLike = like;
            recipe.totalTime = time;
            recipe.vegan = vegan;
            listRecipeFavorite.add(recipe);
        }
        cursor.close();
        return listRecipeFavorite;
    }

    public static boolean haveRecipeInDataBase (String idRecipe, SQLiteDatabase db){
        String query = "SELECT * FROM " + RecipeTable.TABLE_NAME + " WHERE " + RecipeTable.ID_RECIPE + " = '" + idRecipe+"'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToFirst()){
            return true;
        }
        return false;
    }
    public static RecipeDetail getRecipeById (String idRecipe, SQLiteDatabase db){
        String query = "SELECT * FROM " + RecipeTable.TABLE_NAME + " WHERE " + RecipeTable.ID_RECIPE + " = '" + idRecipe+"'";
        Cursor cursor = db.rawQuery(query, null);
        RecipeDetail detailRecipe = new RecipeDetail();
        while (cursor.moveToNext()){
            String id  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.ID_RECIPE));
            String title  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_TITLE));
            String summary  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_SUMMARY));
            Boolean vegan  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_VEGAN)));
            int time  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_TIME)));
            int like  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_LIKE)));
            byte[] image  = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_IMAGE));
            Boolean cheap  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_CHEAP)));
            Boolean healthy  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_HEALTHY)));
            Boolean dairyFree  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_DAIRY_FREE)));
            Boolean glutenFree  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_GLUTEN_FREE)));
            Boolean vegetarian  = Boolean.valueOf(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeTable.COLUMN_NAME_VEGETARIAN)));
            detailRecipe.setId(id);
            detailRecipe.setTitle(title);
            detailRecipe.setSummary(summary);
            detailRecipe.setVegan(vegan);
            detailRecipe.setTotalTime(time);
            detailRecipe.setTotalLike(like);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            detailRecipe.setImageRecipe(bitmap);
            detailRecipe.setCheap(cheap);
            detailRecipe.setHealthy(healthy);
            detailRecipe.setDairyFree(dairyFree);
            detailRecipe.setGlutentFree(glutenFree);
            detailRecipe.setVegetarian(vegetarian);

        }
        return  detailRecipe;
    }
    public static ArrayList<Process> getListProcess (String idRecipe, SQLiteDatabase db){
        ArrayList<Process> listProcess = new ArrayList<>();
        String query = "SELECT * FROM " + RecipeInstructionTable.TABLE_NAME + " WHERE " + RecipeInstructionTable.ID_RECIPE + " = '" + idRecipe+"'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            String action  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeInstructionTable.COLUMN_NAME_ACTION));
            Process process = new Process();
            int step  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeInstructionTable.COLUMN_NAME_STEP)));
            process.setAction(action);
            process.setStep(step);
            listProcess.add(process);
        }
        return  listProcess;
    }
    public static ArrayList<Ingredients> getListIngredient (String idRecipe, SQLiteDatabase db){
        ArrayList<Ingredients> listIngredient = new ArrayList<>();
        String query = "SELECT * FROM " + RecipeIngredientTable.TABLE_NAME + " WHERE " + RecipeIngredientTable.ID_RECIPE + " = '" + idRecipe+"'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            String unit  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeIngredientTable.COLUMN_NAME_UNIT));
            String name  = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeIngredientTable.COLUMN_NAME_NAME));
            int weight  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeIngredientTable.COLUMN_NAME_WEIGHT)));
            int id  = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndexOrThrow(RecipeIngredientTable.COLUMN_NAME_ID_INGREDIENT)));
            byte[] image  = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(RecipeIngredientTable.COLUMN_NAME_IMAGE));
            Ingredients ingredients = new Ingredients();
            ingredients.setId(id);
            ingredients.setWeight(weight);
            ingredients.setUnit(unit);
            ingredients.setName(name);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            ingredients.setImageBitmap(bitmap);
            listIngredient.add((ingredients));
        }
        return  listIngredient;
    }

}
