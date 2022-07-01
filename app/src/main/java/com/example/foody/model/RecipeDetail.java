package com.example.foody.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetail {

    private String id;
    private  boolean liked;
    private boolean cheap;
    private boolean dairyFree;
    private String description;
    private boolean glutentFree;
    private boolean healthy;
    private String imageName;
    private Bitmap imageRecipe;
    private String imageType;
    private int totalLike;
    private List<Process> processList ;
    private List<Ingredients> ingredientsList;
    private String recipeId;
    private String summary;
    private String title;
    private int totalTime;
    private boolean vegan;
    private boolean vegetarian;

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public RecipeDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheap() {
        return cheap;
    }

    public void setCheap(boolean cheap) {
        this.cheap = cheap;
    }

    public boolean isDairyFree() {
        return dairyFree;
    }

    public void setDairyFree(boolean dairyFree) {
        this.dairyFree = dairyFree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGlutentFree() {
        return glutentFree;
    }

    public void setGlutentFree(boolean glutentFree) {
        this.glutentFree = glutentFree;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public List<Process> getProcessList() {
        if (processList == null )
            return new ArrayList<>();
        else {
            return processList;
        }
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }

    public List<Ingredients> getIngredientsList() {
        if (ingredientsList == null )
        return new ArrayList<>();
        else {
            return ingredientsList;
        }
    }

    public void setIngredientsList(List<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public Bitmap getImageRecipe() {
        return imageRecipe;
    }

    public void setImageRecipe(Bitmap imageRecipe) {
        this.imageRecipe = imageRecipe;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
