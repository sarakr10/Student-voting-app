package com.example.decideit;

import android.graphics.drawable.Drawable;
import android.widget.CheckBox;

public class StudentModel {
    private Drawable image;
    private String name;
    private String index;

    public StudentModel(Drawable image, String name, String index) {
        this.image = image;
        this.index = index;
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(String index) {
        this.index = index;
    }

}
