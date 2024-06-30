package com.example.prm_shoppingproject.Model;

public class ProductCreate {

    public String Name;
    public double Price;
    public byte[] Image;
    public String Description;
    public int CategoryID;
    public int Status;

    public ProductCreate() {
    }

    public ProductCreate(String name, double price, byte[] image, String description, int categoryID, int status) {

        Name = name;
        Price = price;
        Image = image;
        Description = description;
        CategoryID = categoryID;
        Status = status;
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

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int typeID) {
        CategoryID = typeID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
