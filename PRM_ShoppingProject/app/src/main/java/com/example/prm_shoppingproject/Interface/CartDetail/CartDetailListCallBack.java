package com.example.prm_shoppingproject.Interface.CartDetail;

import com.example.prm_shoppingproject.Model.CartDetail;

import java.util.List;

public interface CartDetailListCallBack {
    void onSuccess(List<CartDetail> cartList);
    void onError(String error);
}
