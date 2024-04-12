package com.example.android_project;
public class CategoryItem {
    private int imageResource;
    private int id; // 카테고리 ID
    private String title;

    public CategoryItem(int imageResource, int id, String title) {
        this.imageResource = imageResource;
        this.id = id;
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
