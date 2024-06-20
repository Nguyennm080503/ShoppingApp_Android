package com.example.prm_shoppingproject.Model;

import java.util.List;

public class TypeProduct {
    public int TypeID;
    public String TypeName;

    public List<Product> Products;

    public TypeProduct(int typeID, String typeName) {
        TypeID = typeID;
        TypeName = typeName;
    }

    public TypeProduct() {
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public List<Product> getProducts() {
        return Products;
    }

    public void setProducts(List<Product> products) {
        Products = products;
    }
}
