package com.example.prm_shoppingproject.Model;

import java.util.List;

public class Account {
    public int AccountID;
    public String Name;
    public String Email;
    public String Phone;
    public String Username;
    public String Password;
    public int RoleID;
    public int Status;
    public List<Cart> Cart;

    public Account(int accountID, String name, String email, String phone, String userName, String password, int roleID, int status) {
        AccountID = accountID;
        Name = name;
        Email = email;
        Phone = phone;
        Username = userName;
        Password = password;
        RoleID = roleID;
        Status = status;
    }

    public Account() {

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public List<com.example.prm_shoppingproject.Model.Cart> getCart() {
        return Cart;
    }

    public void setCart(List<com.example.prm_shoppingproject.Model.Cart> cart) {
        Cart = cart;
    }

    public int getAccountID() {
        return AccountID;
    }

    public void setAccountID(int accountID) {
        AccountID = accountID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getUserName() {
        return Username;
    }

    public void setUserName(String userName) {
        Username = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getRoleID() {
        return RoleID;
    }

    public void setRoleID(int roleID) {
        RoleID = roleID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setEmail(String email) {
        Email = email;
    }


}
