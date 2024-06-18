package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Model.Account;

public class AccountAction extends DatabaseHelper {
    public AccountAction(Context context) {
        super(context);
    }

    public void addAccount(String name, String email, String phone, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Email", email);
        values.put("Phone", phone);
        values.put("Username", username);
        values.put("Password", password);
        db.insert("Account", null, values);
        db.close();
    }

    public Account GetAccountIDLogin(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Account account = new Account();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE Username = ? AND Password = ?", new String[]{username, password});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int accountID = cursor.getInt(cursor.getColumnIndex("AccountID"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Name"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("Email"));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("Phone"));
            @SuppressLint("Range") String username_db = cursor.getString(cursor.getColumnIndex("UserName"));
            @SuppressLint("Range") int roleID = cursor.getInt(cursor.getColumnIndex("RoleID"));
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

            account = new Account(accountID, name, email, phone, username_db, "", roleID, status);
        }


        cursor.close();
        db.close();
        return account;
    }

    public Account GetUsernameExisted(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Account account = new Account();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE Username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int accountID = cursor.getInt(cursor.getColumnIndex("AccountID"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Name"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("Email"));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("Phone"));
            @SuppressLint("Range") String username_db = cursor.getString(cursor.getColumnIndex("UserName"));
            @SuppressLint("Range") int roleID = cursor.getInt(cursor.getColumnIndex("RoleID"));
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

            account = new Account(accountID, name, email, phone, username_db, "", roleID, status);
        }


        cursor.close();
        db.close();
        return account;
    }
}
