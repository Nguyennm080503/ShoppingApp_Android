package com.example.prm_shoppingproject.Model;

public class ProductCreate {
    public int ProductID;
    public String Name;
    public double Price;
    public byte[] Image;
    public String Description;
    public int TypeID;
    public int Status;

    public ProductCreate() {
    }

    public ProductCreate(int productID, String name, double price, byte[] image, String description, int typeID, int status) {
        ProductID = productID;
        Name = name;
        Price = price;
        Image = image;
        Description = description;
        TypeID = typeID;
        Status = status;
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

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
