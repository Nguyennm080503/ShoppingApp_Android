package com.example.prm_shoppingproject.Model;

public class CartProduct {
    public int ProductID;
    public int OrderID;
    public String Image;
    public String ProductName;
    public int Quantity;
    public double PriceProduct;
    public double PriceTotal;

    public CartProduct(int productID, int orderID, String image, String productName, int quantity, double priceProduct, double priceTotal) {
        ProductID = productID;
        OrderID = orderID;
        Image = image;
        ProductName = productName;
        Quantity = quantity;
        PriceTotal = priceTotal;
        PriceProduct = priceProduct;
    }

    public CartProduct() {
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getPriceProduct() {
        return PriceProduct;
    }

    public void setPriceProduct(double priceProduct) {
        PriceProduct = priceProduct;
    }

    public double getPriceTotal() {
        return PriceTotal;
    }

    public void setPriceTotal(double priceTotal) {
        PriceTotal = priceTotal;
    }
}
