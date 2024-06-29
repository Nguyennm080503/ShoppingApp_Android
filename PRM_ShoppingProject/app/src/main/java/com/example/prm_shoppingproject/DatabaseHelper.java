package com.example.prm_shoppingproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ShoppingDB";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Account (" +
                "AccountID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Username TEXT," +
                "Password TEXT," +
                "Name TEXT," +
                "Email TEXT," +
                "Phone TEXT," +
                "RoleID INTEGER," +
                "Status INTEGER)");

        db.execSQL("CREATE TABLE TypeProduct (" +
                "TypeID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TypeName TEXT)");

        db.execSQL("CREATE TABLE Product (" +
                "ProductID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Price REAL," +
                "Image BLOB," +
                "Description TEXT," +
                "TypeID INTEGER," +
                "Status INTEGER," +
                "FOREIGN KEY(TypeID) REFERENCES TypeProduct(TypeID))");

        db.execSQL("CREATE TABLE Cart (" +
                "CartID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "AccountID INTEGER, " +
                "OrderDate TEXT," +
                "Total REAL, " +
                "Address TEXT," +
                "Status INTEGER," +
                "FOREIGN KEY(AccountID) REFERENCES Account(AccountID))");

        db.execSQL("CREATE TABLE CartDetail (" +
                "CartDetailID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "OrderID INTEGER," +
                "ProductID INTEGER, " +
                "Quantity INTEGER," +
                "Total REAL, " +
                "FOREIGN KEY(OrderID) REFERENCES Cart(CardID)," +
                "FOREIGN KEY(ProductID) REFERENCES Product(ProductID))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Account");
        db.execSQL("DROP TABLE IF EXISTS TypeProduct");
        db.execSQL("DROP TABLE IF EXISTS Product");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        db.execSQL("DROP TABLE IF EXISTS CartDetail");
        onCreate(db);
    }


}
