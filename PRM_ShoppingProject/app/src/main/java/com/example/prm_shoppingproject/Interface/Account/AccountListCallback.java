package com.example.prm_shoppingproject.Interface.Account;

import com.example.prm_shoppingproject.Model.Account;

import java.util.List;

public interface AccountListCallback {
    void onSuccess(List<Account> accounts);
    void onError(String error);
}
