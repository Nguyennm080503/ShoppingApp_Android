package com.example.prm_shoppingproject.Model;

import java.util.List;

public class Cart {
    public int CartID;
    public int AccountID;
    public String OrderDate;
    public double Total;
    public String Address;
    public int Status;

    public List<CartDetail> CartDetail;

    public Cart(int cartID, int accountID, String orderDate, double total, String address, int status) {
        CartID = cartID;
        AccountID = accountID;
        OrderDate = orderDate;
        Total = total;
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

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
