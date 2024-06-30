package com.example.prm_shoppingproject.Interface.Account;

import com.example.prm_shoppingproject.Model.Account;

public interface MessageCallback {
    void onSuccess(String message);
    void onError(String error);
}
