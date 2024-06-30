package com.example.prm_shoppingproject.Interface.Cart;

import com.example.prm_shoppingproject.Model.Cart;

import java.util.List;

public interface CartListCallBack {
    void onSuccess(List<Cart> cartList);
    void onError(String error);
}