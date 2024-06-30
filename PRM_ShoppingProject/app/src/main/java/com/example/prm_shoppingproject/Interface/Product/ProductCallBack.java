package com.example.prm_shoppingproject.Interface.Product;

import com.example.prm_shoppingproject.Model.Product;

public interface ProductCallBack {
    void onSuccess(Product product);
    void onError(String error);
}
