package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Model.TypeProduct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TypeAction {
    private DatabaseHelper openHelper;
    private SQLiteDatabase database;
    private static TypeAction instance;

    public TypeAction(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    public static TypeAction getInstance(Context context) {
        if (instance == null) {
            instance = new TypeAction(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void addTypeName(String typename) {

        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TypeName", typename);
        db.insert("TypeProduct", null, values);
        db.close();
    }

    public void addTypeNameInit() {
        List<String> listTypeName = Arrays.asList("Shoe", "T-Shirt", "Short", "Shirt", "Jacket");
        SQLiteDatabase db = openHelper.getWritableDatabase();
        for (String typename : listTypeName) {
            ContentValues values = new ContentValues();
            values.put("TypeName", typename);
            db.insert("TypeProduct", null, values);
        }
        db.close();
    }

    public TypeProduct GetTypeProductByID(int categoryID){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        TypeProduct type = new TypeProduct();
        Cursor cursor = db.rawQuery("SELECT * FROM TypeProduct WHERE TypeID = ?", new String[]{String.valueOf(categoryID)});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int typeID = cursor.getInt(cursor.getColumnIndex("TypeID"));
            @SuppressLint("Range") String typeName  = cursor.getString(cursor.getColumnIndex("TypeName"));

            type = new TypeProduct(typeID, typeName);
        }
        cursor.close();
        db.close();
        return type;
    }

}
