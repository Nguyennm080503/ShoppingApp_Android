package com.example.prm_shoppingproject.Model;

public class CartDetail {
    public int CartDetailID;
    public int OrderID;
    public int ProductID;
    public int Quantity;
    public double Total;

    public CartDetail() {
    }

    public CartDetail(int cartDetailID, int orderID, int productID, int quantity, double total) {
        CartDetailID = cartDetailID;
        OrderID = orderID;
        ProductID = productID;
        Quantity = quantity;
        Total = total;
    }

    public int getCartDetailID() {
        return CartDetailID;
    }

    public void setCartDetailID(int cartDetailID) {
        CartDetailID = cartDetailID;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
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

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }
}
