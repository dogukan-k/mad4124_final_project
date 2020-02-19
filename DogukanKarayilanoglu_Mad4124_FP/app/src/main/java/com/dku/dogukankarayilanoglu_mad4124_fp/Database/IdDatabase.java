package com.dku.dogukankarayilanoglu_mad4124_fp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Id;

import java.util.ArrayList;

public class IdDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ids";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "id";
    public static final String ROW_ID = "idId";

    public IdDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String sql = "CREATE TABLE " + TABLE_NAME + "(" + ROW_ID + " INTEGER);";
        db.execSQL(sql);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addId(Id id){
        ContentValues values = new ContentValues();
        values.put(ROW_ID,id.getId());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public ArrayList<Id> loadIds() {

        ArrayList idList = new ArrayList();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()){
            Id id = new Id();

            id.setId(cursor.getInt(0));
            idList.add(id);

        }
        cursor.close();
        db.close();
        return  idList;
    }

    public  void updateId(int id , Id newId){
        SQLiteDatabase db = this.getWritableDatabase();

        String where = ROW_ID + " = " + id;

        ContentValues values = new ContentValues();
        values.put(ROW_ID,newId.getId());


        db.update(TABLE_NAME,values,where,null);
        db.close();

    }


}
