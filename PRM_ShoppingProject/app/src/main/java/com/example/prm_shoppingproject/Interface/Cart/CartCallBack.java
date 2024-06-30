package com.example.prm_shoppingproject.Interface.Cart;

import com.example.prm_shoppingproject.Model.Cart;

import java.util.List;

public interface CartCallBack {
    void onSuccess(Cart cart);
    void onError(String error);
}
