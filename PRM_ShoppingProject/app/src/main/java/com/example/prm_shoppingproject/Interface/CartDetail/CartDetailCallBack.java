package com.example.prm_shoppingproject.Interface.CartDetail;

import com.example.prm_shoppingproject.Model.CartDetail;


public interface CartDetailCallBack {
    void onSuccess(CartDetail cartDetail);
    void onError(String error);
}
