package com.example.prm_shoppingproject.Model;

public class Product {
    public int ProductID;
    public String Name;
    public double Price;
    public String Image;
    public String Description;
    public int CategoryID;
    public String TypeName;
    public int Status;

    public Product(int productID, String name, double price, String image, String description, String typeName, int status) {
        ProductID = productID;
        Name = name;
        Price = price;
        Image = image;
        Description = description;
        TypeName = typeName;
        Status = status;
    }

    public Product() {
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typename) {
        TypeName = typename;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
