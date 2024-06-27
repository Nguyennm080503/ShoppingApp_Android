package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Model.Cart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartAction {
    private DatabaseHelper openHelper;
    private SQLiteDatabase database;
    private static CartAction instance;

    public CartAction(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    public static CartAction getInstance(Context context) {
        if (instance == null) {
            instance = new CartAction(context);
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

    public void addCart(int accountID, double total, String address, int status) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(now);

        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("AccountID", accountID);
        values.put("OrderDate", formattedDate);
        values.put("Total", total);
        values.put("Address", address);
        values.put("Status", status);
        db.insert("Cart", null, values);
        db.close();
    }

    public List<Cart> getAllCartByAccount(int accountID) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        List<Cart> cartList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Cart Where AccountID = ?", new String[]{String.valueOf(accountID)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int cartID = cursor.getInt(cursor.getColumnIndex("CartID"));
                @SuppressLint("Range") int accountdbID  = cursor.getInt(cursor.getColumnIndex("AccountID"));
                @SuppressLint("Range") String orderDate = cursor.getString(cursor.getColumnIndex("OrderDate"));
                @SuppressLint("Range") double total = cursor.getDouble(cursor.getColumnIndex("Total"));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("Address"));
                @SuppressLint("Range") int status  = cursor.getInt(cursor.getColumnIndex("Status"));

                Cart cart = new Cart(cartID, accountdbID, orderDate, total, address, status);
                cartList.add(cart);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cartList;
    }

    public Cart getCartByOrderID(int orderID) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cart cart = new Cart();
        Cursor cursor = db.rawQuery("SELECT * FROM Cart Where CartID = ?", new String[]{String.valueOf(orderID)});

        if (cursor.moveToFirst()) {
                @SuppressLint("Range") int cartID = cursor.getInt(cursor.getColumnIndex("CartID"));
                @SuppressLint("Range") int accountdbID  = cursor.getInt(cursor.getColumnIndex("AccountID"));
                @SuppressLint("Range") String orderDate = cursor.getString(cursor.getColumnIndex("OrderDate"));
                @SuppressLint("Range") double total = cursor.getDouble(cursor.getColumnIndex("Total"));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("Address"));
                @SuppressLint("Range") int status  = cursor.getInt(cursor.getColumnIndex("Status"));

                cart = new Cart(cartID, accountdbID, orderDate, total, address, status);
        }

        cursor.close();
        db.close();
        return cart;
    }

    public Cart getCartNewOrderID() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cart cart = new Cart();
        Cursor cursor = db.rawQuery("SELECT * FROM Cart ORDER BY CartID DESC ", null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int cartID = cursor.getInt(cursor.getColumnIndex("CartID"));
            @SuppressLint("Range") int accountdbID  = cursor.getInt(cursor.getColumnIndex("AccountID"));
            @SuppressLint("Range") String orderDate = cursor.getString(cursor.getColumnIndex("OrderDate"));
            @SuppressLint("Range") double total = cursor.getDouble(cursor.getColumnIndex("Total"));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("Address"));
            @SuppressLint("Range") int status  = cursor.getInt(cursor.getColumnIndex("Status"));

            cart = new Cart(cartID, accountdbID, orderDate, total, address, status);
        }

        cursor.close();
        db.close();
        return cart;
    }
}
