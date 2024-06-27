package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAction {
    private DatabaseHelper openHelper;
    private SQLiteDatabase database;
    private static ProductAction instance;

    public ProductAction(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    public static ProductAction getInstance(Context context) {
        if (instance == null) {
            instance = new ProductAction(context);
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

    public void addProduct(String name, double price, byte[] image, String description, int typeID) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Price", price);
        values.put("Image", image);
        values.put("Description", description);
        values.put("TypeID", typeID);
        values.put("Status", 0);
        db.insert("Product", null, values);
        db.close();
    }


    public Product GetProductByID(int productID){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Product product = new Product();
        Cursor cursor = db.rawQuery("SELECT * FROM Product WHERE ProductID = ?", new String[]{String.valueOf(productID)});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int productdbID = cursor.getInt(cursor.getColumnIndex("ProductID"));
            @SuppressLint("Range") String nameProduct  = cursor.getString(cursor.getColumnIndex("Name"));
            @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("Price"));
            @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("Image"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("Description"));
            @SuppressLint("Range") int typeID = cursor.getInt(cursor.getColumnIndex("TypeID"));
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

            product = new Product(productdbID, nameProduct, price, image, description, typeID, status);
        }


        cursor.close();
        db.close();
        return product;
    }

    public List<Product> getAllProducts() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        List<Product> productList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Product Where Status = 0", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int productID = cursor.getInt(cursor.getColumnIndex("ProductID"));
                @SuppressLint("Range") String nameProduct  = cursor.getString(cursor.getColumnIndex("Name"));
                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("Price"));
                @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("Image"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("Description"));
                @SuppressLint("Range") int typeID = cursor.getInt(cursor.getColumnIndex("TypeID"));
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

                Product product = new Product(productID, nameProduct, price, image, description, typeID, status);
                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return productList;
    }

    public List<Product> getAllProductsByCategory(int typeID) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        List<Product> productList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Product Where TypeID = ?", new String[]{String.valueOf(typeID)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int productID = cursor.getInt(cursor.getColumnIndex("ProductID"));
                @SuppressLint("Range") String nameProduct  = cursor.getString(cursor.getColumnIndex("Name"));
                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("Price"));
                @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("Image"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("Description"));
                @SuppressLint("Range") int typedbID = cursor.getInt(cursor.getColumnIndex("TypeID"));
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

                Product product = new Product(productID, nameProduct, price, image, description, typedbID, status);
                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return productList;
    }


    public Product GetProductNameExisted(String name){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Product product = new Product();
        Cursor cursor = db.rawQuery("SELECT * FROM Product WHERE Name = ?", new String[]{name});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int productID = cursor.getInt(cursor.getColumnIndex("ProductID"));
            @SuppressLint("Range") String nameProduct  = cursor.getString(cursor.getColumnIndex("Name"));
            @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("Price"));
            @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("Image"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("Description"));
            @SuppressLint("Range") int typeID = cursor.getInt(cursor.getColumnIndex("TypeID"));
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

            product = new Product(productID, nameProduct, price, image, description, typeID, status);
        }


        cursor.close();
        db.close();
        return product;
    }
}
