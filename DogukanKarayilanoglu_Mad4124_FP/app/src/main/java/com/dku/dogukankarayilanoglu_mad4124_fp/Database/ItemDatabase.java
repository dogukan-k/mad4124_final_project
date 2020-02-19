package com.dku.dogukankarayilanoglu_mad4124_fp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Item;

import java.util.ArrayList;

public class ItemDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "item";
    public static final String ROW_ID = "itemId";
    public static final String ROW_LATITUDE = "latitude";
    public static final String ROW_LONGITUDE = "longitude";
    public static final String ROW_TITLE = "title";
    public static final String ROW_CONTENT = "content";
    public static final String ROW_DATE = "date";

    public ItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "(" + ROW_ID + " INTEGER PRIMARY KEY , " + ROW_LATITUDE + " TEXT NOT NULL, " + ROW_LONGITUDE + " TEXT NOT NULL, " + ROW_TITLE + " TEXT, " +  ROW_DATE + " TEXT NOT NULL, " + ROW_CONTENT + " TEXT);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {


        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Item> loadItems() {

        ArrayList listItem = new ArrayList();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()){
            Item item = new Item();

            item.setId(cursor.getInt(0));
            item.setLatitude(cursor.getString(1));
            item.setLongitude(cursor.getString(2));
            item.setTitle(cursor.getString(3));
            item.setDateAdded(cursor.getString(4));
            item.setContent(cursor.getString(5));


            listItem.add(item);

        }
        cursor.close();
        db.close();
        return  listItem;
    }

    public void addItem(Item item){

        ContentValues values = new ContentValues();
        values.put(ROW_ID,item.getId());
        values.put(ROW_LATITUDE,item.getLatitude());
        values.put(ROW_LONGITUDE,item.getLongitude());
        values.put(ROW_TITLE,item.getTitle());
        values.put(ROW_DATE,item.getDateAdded());
        values.put(ROW_CONTENT,item.getContent());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();


    }




    public Item getItem(int id){
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ROW_ID +"="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        Item item = new Item();

        while (cursor.moveToNext()){

            item.setId(cursor.getInt(0));
            item.setLatitude(cursor.getString(1));
            item.setLongitude(cursor.getString(2));
            item.setTitle(cursor.getString(3));
            item.setDateAdded(cursor.getString(4));
            item.setContent(cursor.getString(5));

        }

        return  item;
    }

    public  void deleteItem(int id){

        SQLiteDatabase db = this.getWritableDatabase();

        String where = ROW_ID + "=" + id;
        db.delete(TABLE_NAME,where,null);
        db.close();

    }

    public  void updateItem(Item item){
        SQLiteDatabase db = this.getWritableDatabase();

        String where = ROW_ID + " = " + item.getId();

        ContentValues values = new ContentValues();
        values.put(ROW_LATITUDE,item.getLatitude());
        values.put(ROW_LONGITUDE,item.getLongitude());
        values.put(ROW_TITLE,item.getTitle());
        values.put(ROW_CONTENT,item.getContent());

        db.update(TABLE_NAME,values,where,null);
        db.close();


    }




}
