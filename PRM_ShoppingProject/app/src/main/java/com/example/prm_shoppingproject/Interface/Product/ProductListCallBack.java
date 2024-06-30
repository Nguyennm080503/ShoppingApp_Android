package com.example.prm_shoppingproject.Interface.Product;

import com.example.prm_shoppingproject.Model.Product;

import java.util.List;

public interface ProductListCallBack {
    void onSuccess(List<Product> products);
    void onError(String error);
}
