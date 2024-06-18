package com.example.prm_shoppingproject.Model;

public class Account {
    public int AccountID;
    public String Name;
    public String Email;
    public String Phone;
    public String UserName;
    public String Password;
    public int RoleID;
    public int Status;

    public Account(int accountID, String name, String email, String phone, String userName, String password, int roleID, int status) {
        AccountID = accountID;
        Name = name;
        Email = email;
        Phone = phone;
        UserName = userName;
        Password = password;
        RoleID = roleID;
        Status = status;
    }

    public Account() {

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
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
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
