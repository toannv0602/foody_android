package com.example.foody.model;

import android.graphics.Bitmap;

public class User {
    public String userName ;
    public String id;
    public String email;
    public String imageName;
    public String imageType;
    public Bitmap image;

    public User() {
    }

    public User(String userName, String id, String email, String imageName, String imageType, Bitmap image) {
        this.userName = userName;
        this.id = id;
        this.email = email;
        this.imageName = imageName;
        this.imageType = imageType;
        this.image = image;
    }

}
