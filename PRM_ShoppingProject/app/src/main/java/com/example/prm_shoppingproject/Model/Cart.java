package com.example.prm_shoppingproject.Model;

import java.util.List;

public class Cart {
    public int CartID;
    public int AccountID;
    public String OrderDate;
    public double TotalBill;
    public String Address;
    public int Status;

    public List<CartDetail> CartDetail;

    public Cart(int cartID, int accountID, String orderDate, double totalBill, String address, int status) {
        CartID = cartID;
        AccountID = accountID;
        OrderDate = orderDate;
        TotalBill = totalBill;
        Address = address;
        Status = status;
    }

    public Cart() {
    }

    public List<com.example.prm_shoppingproject.Model.CartDetail> getCartDetail() {
        return CartDetail;
    }

    public void setCartDetail(List<com.example.prm_shoppingproject.Model.CartDetail> cartDetail) {
        CartDetail = cartDetail;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getCartID() {
        return CartID;
    }

    public void setCartID(int cartID) {
        CartID = cartID;
    }

    public int getAccountID() {
        return AccountID;
    }

    public void setAccountID(int accountID) {
        AccountID = accountID;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public double getTotalBill() {
        return TotalBill;
    }

    public void setTotalBill(double totalBill) {
        TotalBill = totalBill;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
