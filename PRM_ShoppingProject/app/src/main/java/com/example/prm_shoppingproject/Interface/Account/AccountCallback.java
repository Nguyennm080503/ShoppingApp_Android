package com.example.prm_shoppingproject.Interface.Account;

import com.example.prm_shoppingproject.Model.Account;

public interface AccountCallback {
    void onSuccess(Account account);
    void onError(String error);
}
