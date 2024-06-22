package com.example.prm_shoppingproject.Model;

public class CartItem {
    public int ProductID;
    public int Quantity;

    public CartItem(int productID, int quantity) {
        ProductID = productID;
        Quantity = quantity;
    }

    public CartItem() {
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
