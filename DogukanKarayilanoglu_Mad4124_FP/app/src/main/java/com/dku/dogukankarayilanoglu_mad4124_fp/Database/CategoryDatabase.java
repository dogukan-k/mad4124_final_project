package com.dku.dogukankarayilanoglu_mad4124_fp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Category;

import java.util.ArrayList;

public class CategoryDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "categories";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "category";
    public static final String ROW_ID = "categoryId";
    public static final String ROW_ITEMS = "arrayOfItems";
    public static final String ROW_CATEGORY_NAME = "categoryName";


    public CategoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ROW_ITEMS + " TEXT , " + ROW_CATEGORY_NAME + " TEXT NOT NULL);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public ArrayList<Category> loadCategories() {

        ArrayList listCategory = new ArrayList();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()){
            Category category = new Category();

            category.setId(cursor.getInt(0));
            category.setArrayOfItems(cursor.getString(1));
            category.setCategoryName(cursor.getString(2));


            listCategory.add(category);

        }
        cursor.close();
        db.close();
        return  listCategory;
    }

    public void addCategory (Category category){

        ContentValues values = new ContentValues();

        values.put(ROW_CATEGORY_NAME,category.getCategoryName());
        values.put(ROW_ITEMS,category.getArrayOfItems());


        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();

    }

    public Category getCategory(int id){
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ROW_ID +"="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        Category category = new Category();

        while (cursor.moveToNext()){

            category.setId(cursor.getInt(0));
            category.setArrayOfItems(cursor.getString(1));
            category.setCategoryName(cursor.getString(2));

        }

        return  category;
    }

    public  void updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();

        String where = ROW_ID + " = " + category.getId();

        ContentValues values = new ContentValues();
        values.put(ROW_ITEMS,category.getArrayOfItems());

        db.update(TABLE_NAME,values,where,null);
        db.close();


    }


}
