package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartDetailAction {
    private DatabaseHelper openHelper;
    private SQLiteDatabase database;
    private static CartDetailAction instance;

    public CartDetailAction(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    public static CartDetailAction getInstance(Context context) {
        if (instance == null) {
            instance = new CartDetailAction(context);
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

    public void addCartDetail(int orderID, int productID, int quantity, double total) {

        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("OrderID", orderID);
        values.put("ProductID", productID);
        values.put("Quantity", quantity);
        values.put("Total", total);
        db.insert("CartDetail", null, values);
        db.close();
    }

    public List<CartDetail> getAllCartDetailByOrder(int orderID) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        List<CartDetail> cartdetailList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM CartDetail Where OrderID = ?", new String[]{String.valueOf(orderID)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int cartDetailID = cursor.getInt(cursor.getColumnIndex("CartDetailID"));
                @SuppressLint("Range") int orderdbID  = cursor.getInt(cursor.getColumnIndex("OrderID"));
                @SuppressLint("Range") int productID = cursor.getInt(cursor.getColumnIndex("ProductID"));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("Quantity"));
                @SuppressLint("Range") double total = cursor.getDouble(cursor.getColumnIndex("Total"));

                CartDetail cartDetail = new CartDetail(cartDetailID, orderdbID, productID, quantity, total);
                cartdetailList.add(cartDetail);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cartdetailList;
    }
}
