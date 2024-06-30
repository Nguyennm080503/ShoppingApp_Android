package com.example.prm_shoppingproject.Model;

public class CartProduct {
    public int ProductID;
    public String Image;
    public String ProductName;
    public int Quantity;
    public double Price;

    public CartProduct(int productID, String image, String productName, int quantity, double price) {
        ProductID = productID;
        Image = image;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
    }

    public CartProduct() {
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
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

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }
}
