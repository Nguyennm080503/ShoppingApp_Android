package com.example.prm_shoppingproject.Interface.TypeProduct;

import com.example.prm_shoppingproject.Model.TypeProduct;

import java.util.List;

public interface TypeProductListCallBack {
    void onSuccess(List<TypeProduct> products);
    void onError(String error);
}
