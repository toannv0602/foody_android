package com.example.foody.model;

import android.graphics.Bitmap;

public class CommentRecipe {
    public String key;
    public User author;
    public String content;
    public String imageType;
    public String imageName;
    public Bitmap Image;
    public  CommentRecipe(){
        author = new User();
    }
}
