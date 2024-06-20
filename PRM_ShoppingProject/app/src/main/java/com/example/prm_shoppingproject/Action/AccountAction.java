package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Model.Account;

public class AccountAction {
    private DatabaseHelper openHelper;
    private SQLiteDatabase database;
    private static AccountAction instance;

    public AccountAction(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    public static AccountAction getInstance(Context context) {
        if (instance == null) {
            instance = new AccountAction(context);
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

    public void addAccount(String name, String email, String phone, String username, String password) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Email", email);
        values.put("Phone", phone);
        values.put("Username", username);
        values.put("Password", password);
        values.put("RoleID", 1);
        values.put("Status", 0);
        db.insert("Account", null, values);
        db.close();
    }

    public void AddAdminAccount(){
        Account account = GetAccountIDLogin("admin", "123456");
        if(account.Username == null){
            SQLiteDatabase db = openHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Name", "Admin");
            values.put("Email", "admin@gmail.com");
            values.put("Phone", "111111111");
            values.put("Username", "admin");
            values.put("Password", "123456");
            values.put("RoleID", 0);
            values.put("Status" , 0);
            db.insert("Account", null, values);
            db.close();
        }
    }

    public Account GetAccountIDLogin(String username, String password){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Account account = new Account();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE Username = ? AND Password = ?", new String[]{username, password});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int accountID = cursor.getInt(cursor.getColumnIndex("AccountID"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Name"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("Email"));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("Phone"));
            @SuppressLint("Range") String username_db = cursor.getString(cursor.getColumnIndex("Username"));
            @SuppressLint("Range") int roleID = cursor.getInt(cursor.getColumnIndex("RoleID"));
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));

            account = new Account(accountID, name, email, phone, username_db, "", roleID, status);
        }


        cursor.close();
        db.close();
        return account;
    }

    public Account GetUsernameExisted(String username){
        SQLiteDatabase db = openHelper.getReadableDatabase();
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
