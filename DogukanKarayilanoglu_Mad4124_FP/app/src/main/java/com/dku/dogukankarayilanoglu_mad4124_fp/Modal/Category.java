package com.dku.dogukankarayilanoglu_mad4124_fp.Modal;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Category {

    private int id;
    private String categoryName;
    private String arrayOfItems;

    public Category(){

    }

    public Category(int id, String categoryName) {

        this.id = id;
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getArrayOfItems() {
        return arrayOfItems;
    }

    public ArrayList<Item> getArrayList(){
//
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        ArrayList<Item> finalOutput = gson.fromJson(getArrayOfItems(),type);

        return finalOutput;

    }

    public void setArrayOfItems(String input) {

        this.arrayOfItems = input;
    }

    @NonNull
    @Override
    public String toString() {
        return getCategoryName();
    }
}
